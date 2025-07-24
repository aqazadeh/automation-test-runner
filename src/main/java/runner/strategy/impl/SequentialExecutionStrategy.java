package runner.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.command.CommandInvoker;
import runner.command.StepExecutionCommand;
import runner.model.step.TestStep;
import runner.strategy.ExecutionStrategy;

import java.util.List;

/**
 * Sequential execution strategy - executes steps one by one in order
 */
@Slf4j
public class SequentialExecutionStrategy implements ExecutionStrategy {
    
    private final boolean stopOnFirstFailure;
    
    public SequentialExecutionStrategy() {
        this(true);
    }
    
    public SequentialExecutionStrategy(boolean stopOnFirstFailure) {
        this.stopOnFirstFailure = stopOnFirstFailure;
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
        
        log.info("Executing {} steps sequentially using Command pattern (stopOnFirstFailure: {})", 
            steps.size(), stopOnFirstFailure);
        
        // Create command invoker
        CommandInvoker invoker = new CommandInvoker();
        
        // Create step execution commands
        for (int i = 0; i < steps.size(); i++) {
            TestStep step = steps.get(i);
            StepExecutionCommand command = new StepExecutionCommand(driver, step, i);
            invoker.addCommand(command);
        }
        
        if (stopOnFirstFailure) {
            // Execute all commands - will stop on first failure
            invoker.executeAll();
        } else {
            // Execute commands individually to continue on failure
            Exception firstException = null;
            int failedSteps = 0;
            int successfulSteps = 0;
            
            List<runner.command.Command> commands = invoker.getQueuedCommands();
            invoker.clearQueue(); // Clear the queue since we're executing manually
            
            for (int i = 0; i < commands.size(); i++) {
                try {
                    invoker.executeCommand(commands.get(i));
                    successfulSteps++;
                } catch (Exception e) {
                    failedSteps++;
                    log.error("Step {} failed: {}", i + 1, e.getMessage());
                    
                    if (firstException == null) {
                        firstException = e;
                    }
                    
                    // Continue with next step
                    log.info("Continuing with next step (stopOnFirstFailure=false)");
                }
            }
            
            if (firstException != null) {
                log.error("Execution completed with {} failed steps out of {} (stopOnFirstFailure=false)", 
                    failedSteps, steps.size());
                throw new RuntimeException(String.format(
                    "Sequential execution completed with %d failed steps out of %d total steps. First failure: %s",
                    failedSteps, steps.size(), firstException.getMessage()), firstException);
            }
        }
        
        log.info("Sequential execution completed successfully for {} steps", steps.size());
    }
    
    
    @Override
    public String getStrategyName() {
        return "Sequential" + (stopOnFirstFailure ? " (Stop on Failure)" : " (Continue on Failure)");
    }
    
    @Override
    public String getDescription() {
        return String.format("Executes test steps one by one in sequential order. %s",
            stopOnFirstFailure ? "Stops execution on first failure." : "Continues execution even if steps fail.");
    }
    
    public boolean isStopOnFirstFailure() {
        return stopOnFirstFailure;
    }
}