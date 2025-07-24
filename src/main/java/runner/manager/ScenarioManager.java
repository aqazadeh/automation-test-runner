package runner.manager;

import com.aventstack.extentreports.Status;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import runner.executor.ActionExecutor;
import runner.model.step.TestStep;

import java.util.List;

@Slf4j
public class ScenarioManager {
    
    public static class StepExecutionException extends RuntimeException {
        private final TestStep failedStep;
        private final int stepIndex;
        
        public StepExecutionException(String message, Throwable cause, TestStep failedStep, int stepIndex) {
            super(message, cause);
            this.failedStep = failedStep;
            this.stepIndex = stepIndex;
        }
        
        public TestStep getFailedStep() { return failedStep; }
        public int getStepIndex() { return stepIndex; }
    }
    
    public static void start(WebDriver driver, List<TestStep> steps) {
        // Input validation
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        
        if (steps == null || steps.isEmpty()) {
            log.warn("No steps provided to execute");
            return;
        }
        
        log.info("Starting scenario execution with {} steps", steps.size());
        
        for (int i = 0; i < steps.size(); i++) {
            TestStep step = steps.get(i);
            executeStep(driver, step, i);
        }
        
        log.info("Scenario execution completed successfully");
    }
    
    @SuppressWarnings("unchecked")
    private static void executeStep(WebDriver driver, TestStep step, int stepIndex) {
        if (step == null) {
            String errorMsg = "Step at index " + stepIndex + " is null";
            log.error(errorMsg);
            ReportManager.logStep(Status.FAIL, "ERROR", errorMsg);
            throw new StepExecutionException(errorMsg, null, null, stepIndex);
        }
        
        String stepName = step.getName() != null ? step.getName() : "Unnamed Step";
        String stepInfo = String.format("Step %d: %s (%s)", stepIndex + 1, stepName, step.getClass().getSimpleName());
        
        log.info("Executing {}", stepInfo);
        ReportManager.logStep(Status.INFO, "Started: " + stepName, step.toString());
        
        try {
            // Validate step configuration
            validateStep(step);
            
            // Execute the step
            ActionExecutor<TestStep> executor = (ActionExecutor<TestStep>) step.getAction().executor();
            executor.execute(driver, step);
            
            log.info("Successfully executed {}", stepInfo);
            ReportManager.logStep(Status.PASS, "Completed: " + stepName, "Step executed successfully");
            
        } catch (NoSuchElementException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Element not found: " + e.getMessage(), e);
        } catch (ElementClickInterceptedException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Element click intercepted: " + e.getMessage(), e);
        } catch (ElementNotInteractableException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Element not interactable: " + e.getMessage(), e);
        } catch (TimeoutException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Timeout occurred: " + e.getMessage(), e);
        } catch (StaleElementReferenceException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Stale element reference: " + e.getMessage(), e);
        } catch (WebDriverException e) {
            handleStepFailure(step, stepIndex, stepInfo, "WebDriver error: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Invalid step configuration: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            handleStepFailure(step, stepIndex, stepInfo, "Invalid state: " + e.getMessage(), e);
        } catch (Exception e) {
            handleStepFailure(step, stepIndex, stepInfo, "Unexpected error: " + e.getMessage(), e);
        }
    }
    
    private static void validateStep(TestStep step) {
        if (step.getAction() == null) {
            throw new IllegalArgumentException("Step action cannot be null");
        }
        
        try {
            step.getAction().executor();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get executor for action: " + step.getAction(), e);
        }
    }
    
    private static void handleStepFailure(TestStep step, int stepIndex, String stepInfo, String errorMessage, Throwable cause) {
        String fullErrorMessage = String.format("Failed to execute %s: %s", stepInfo, errorMessage);
        
        log.error(fullErrorMessage, cause);
        ReportManager.logStep(Status.FAIL, "FAILED: " + (step.getName() != null ? step.getName() : "Unnamed Step"), 
                             fullErrorMessage + "\nStep details: " + step.toString());
        
        throw new StepExecutionException(fullErrorMessage, cause, step, stepIndex);
    }
}
