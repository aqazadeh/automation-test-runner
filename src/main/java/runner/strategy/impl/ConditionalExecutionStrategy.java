package runner.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.model.step.TestStep;
import runner.model.step.condition.ConditionActionStep;
import runner.observer.TestExecutionEvent;
import runner.manager.ScenarioManager;
import runner.strategy.ExecutionStrategy;

import java.util.List;
import java.util.function.Predicate;

/**
 * Conditional execution strategy - can skip steps based on conditions
 */
@Slf4j
public class ConditionalExecutionStrategy implements ExecutionStrategy {
    
    private final ExecutionStrategy delegateStrategy;
    private final Predicate<TestStep> shouldExecuteStep;
    
    public ConditionalExecutionStrategy(ExecutionStrategy delegateStrategy) {
        this(delegateStrategy, step -> true); // Execute all steps by default
    }
    
    public ConditionalExecutionStrategy(ExecutionStrategy delegateStrategy, Predicate<TestStep> shouldExecuteStep) {
        this.delegateStrategy = delegateStrategy;
        this.shouldExecuteStep = shouldExecuteStep != null ? shouldExecuteStep : step -> true;
    }
    
    @Override
    public void execute(WebDriver driver, List<TestStep> steps) throws Exception {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        
        if (steps == null || steps.isEmpty()) {
            log.warn("No steps provided to execute");
            return;
        }
        
        // Filter steps based on conditions
        List<TestStep> stepsToExecute = steps.stream()
            .filter(step -> {
                boolean shouldExecute = shouldExecuteStep.test(step);
                if (!shouldExecute) {
                    String stepName = step.getName() != null ? step.getName() : "Unnamed Step";
                    log.info("Skipping step: {} ({})", stepName, step.getClass().getSimpleName());
                    
                    // Publish step skipped event
                    int stepIndex = steps.indexOf(step);
                    ScenarioManager.getEventPublisher().publishEvent(new TestExecutionEvent(
                        TestExecutionEvent.EventType.STEP_SKIPPED, step, stepIndex, "Step skipped by conditional strategy"));
                }
                return shouldExecute;
            })
            .toList();
        
        int originalCount = steps.size();
        int filteredCount = stepsToExecute.size();
        int skippedCount = originalCount - filteredCount;
        
        log.info("Conditional execution: {} steps to execute, {} skipped out of {} total", 
            filteredCount, skippedCount, originalCount);
        
        if (stepsToExecute.isEmpty()) {
            log.warn("All steps were skipped by conditional execution strategy");
            return;
        }
        
        // Delegate to the wrapped strategy with filtered steps
        delegateStrategy.execute(driver, stepsToExecute);
    }
    
    @Override
    public String getStrategyName() {
        return "Conditional(" + delegateStrategy.getStrategyName() + ")";
    }
    
    @Override
    public String getDescription() {
        return String.format("Wraps %s with conditional execution logic. Steps may be skipped based on runtime conditions.",
            delegateStrategy.getStrategyName());
    }
    
    @Override
    public boolean supports(List<TestStep> steps) {
        return delegateStrategy.supports(steps);
    }
    
    public ExecutionStrategy getDelegateStrategy() {
        return delegateStrategy;
    }
    
    /**
     * Create a conditional strategy that skips steps with failed conditions
     */
    public static ConditionalExecutionStrategy skipFailedConditions(ExecutionStrategy delegateStrategy) {
        return new ConditionalExecutionStrategy(delegateStrategy, step -> {
            // Skip ConditionActionStep if it would fail
            if (step instanceof ConditionActionStep conditionStep) {
                // This is a simplified check - in practice, you'd evaluate the condition
                return conditionStep.getCondition() != null;
            }
            return true; // Execute all non-condition steps
        });
    }
    
    /**
     * Create a conditional strategy that only executes specific step types
     */
    public static ConditionalExecutionStrategy onlyStepTypes(ExecutionStrategy delegateStrategy, Class<?>... allowedTypes) {
        return new ConditionalExecutionStrategy(delegateStrategy, step -> {
            for (Class<?> allowedType : allowedTypes) {
                if (allowedType.isInstance(step)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    /**
     * Create a conditional strategy that skips steps with specific names
     */
    public static ConditionalExecutionStrategy skipStepsWithNames(ExecutionStrategy delegateStrategy, String... namesToSkip) {
        return new ConditionalExecutionStrategy(delegateStrategy, step -> {
            String stepName = step.getName();
            if (stepName != null) {
                for (String nameToSkip : namesToSkip) {
                    if (stepName.contains(nameToSkip)) {
                        return false;
                    }
                }
            }
            return true;
        });
    }
}