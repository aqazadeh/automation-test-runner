package runner.command;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.manager.ScenarioManager;
import runner.model.step.TestStep;
import runner.observer.TestExecutionEvent;

/**
 * Command that encapsulates the execution of a single test step
 */
@Slf4j
public class StepExecutionCommand implements Command {
    
    private final WebDriver driver;
    private final TestStep step;
    private final int stepIndex;
    private final String stepName;
    private final String stepInfo;
    
    public StepExecutionCommand(WebDriver driver, TestStep step, int stepIndex) {
        this.driver = driver;
        this.step = step;
        this.stepIndex = stepIndex;
        this.stepName = step.getName() != null ? step.getName() : "Unnamed Step";
        this.stepInfo = String.format("Step %d: %s (%s)", stepIndex + 1, stepName, step.getClass().getSimpleName());
    }
    
    @Override
    public void execute() throws Exception {
        if (driver == null) {
            throw new IllegalStateException("WebDriver is null");
        }
        
        if (step == null) {
            throw new IllegalStateException("TestStep is null");
        }
        
        log.debug("Executing command: {}", stepInfo);
        
        // Publish step started event
        ScenarioManager.getEventPublisher().publishEvent(new TestExecutionEvent(
            TestExecutionEvent.EventType.STEP_STARTED, step, stepIndex, stepInfo));
        
        try {
            // Validate step
            validateStep();
            
            // Execute the step
            @SuppressWarnings("unchecked")
            ActionExecutor<TestStep> executor = (ActionExecutor<TestStep>) step.getAction().executor();
            executor.execute(driver, step);
            
            log.debug("Successfully executed command: {}", stepInfo);
            
            // Publish step completed event
            ScenarioManager.getEventPublisher().publishEvent(new TestExecutionEvent(
                TestExecutionEvent.EventType.STEP_COMPLETED, step, stepIndex, "Step executed successfully"));
                
        } catch (Exception e) {
            log.error("Command execution failed: {} - {}", stepInfo, e.getMessage());
            
            // Publish step failed event
            ScenarioManager.getEventPublisher().publishEvent(new TestExecutionEvent(
                TestExecutionEvent.EventType.STEP_FAILED, step, stepIndex, e.getMessage(), e));
            throw e;
        }
    }
    
    private void validateStep() {
        if (step.getAction() == null) {
            throw new IllegalArgumentException("Step action cannot be null");
        }
        
        try {
            step.getAction().executor();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get executor for action: " + step.getAction(), e);
        }
    }
    
    @Override
    public boolean canExecute() {
        return driver != null && step != null && step.getAction() != null;
    }
    
    @Override
    public String getDescription() {
        return String.format("Execute %s", stepInfo);
    }
    
    @Override
    public String getName() {
        return "StepExecution[" + stepName + "]";
    }
    
    // Getters for access to command data
    public WebDriver getDriver() { return driver; }
    public TestStep getStep() { return step; }
    public int getStepIndex() { return stepIndex; }
    public String getStepName() { return stepName; }
    public String getStepInfo() { return stepInfo; }
}