package runner.observer.impl;

import com.aventstack.extentreports.Status;
import lombok.extern.slf4j.Slf4j;
import runner.manager.ReportManager;
import runner.observer.TestExecutionEvent;
import runner.observer.TestExecutionObserver;

/**
 * Observer that integrates with the ReportManager for test reporting
 */
@Slf4j
public class ReportingObserver implements TestExecutionObserver {
    
    private final boolean includeStackTraces;
    
    public ReportingObserver() {
        this(true);
    }
    
    public ReportingObserver(boolean includeStackTraces) {
        this.includeStackTraces = includeStackTraces;
    }
    
    @Override
    public void onEvent(TestExecutionEvent event) {
        switch (event.getEventType()) {
            case SCENARIO_STARTED -> {
                ReportManager.log(Status.INFO, "Scenario started: " + event.getMessage());
            }
            case SCENARIO_COMPLETED -> {
                ReportManager.log(Status.PASS, "Scenario completed successfully: " + event.getMessage());
            }
            case SCENARIO_FAILED -> {
                String message = "Scenario failed: " + event.getMessage();
                if (event.getException() != null && includeStackTraces) {
                    message += "\\nException: " + event.getException().getMessage();
                }
                ReportManager.log(Status.FAIL, message);
            }
            case STEP_STARTED -> {
                if (event.getStep() != null) {
                    String stepName = event.getStep().getName() != null ? 
                        event.getStep().getName() : "Step " + (event.getStepIndex() + 1);
                    String stepType = event.getStep().getClass().getSimpleName();
                    ReportManager.logStep(Status.INFO, "Started: " + stepName, 
                        "Executing " + stepType + ": " + event.getStep().toString());
                }
            }
            case STEP_COMPLETED -> {
                if (event.getStep() != null) {
                    String stepName = event.getStep().getName() != null ? 
                        event.getStep().getName() : "Step " + (event.getStepIndex() + 1);
                    ReportManager.logStep(Status.PASS, "Completed: " + stepName, 
                        "Step executed successfully");
                }
            }
            case STEP_FAILED -> {
                if (event.getStep() != null) {
                    String stepName = event.getStep().getName() != null ? 
                        event.getStep().getName() : "Step " + (event.getStepIndex() + 1);
                    String message = "Step failed: " + event.getMessage();
                    if (event.getException() != null && includeStackTraces) {
                        message += "\\nException: " + event.getException().getClass().getSimpleName() + 
                                  ": " + event.getException().getMessage();
                    }
                    ReportManager.logStep(Status.FAIL, "Failed: " + stepName, message);
                }
            }
            case STEP_SKIPPED -> {
                if (event.getStep() != null) {
                    String stepName = event.getStep().getName() != null ? 
                        event.getStep().getName() : "Step " + (event.getStepIndex() + 1);
                    ReportManager.logStep(Status.SKIP, "Skipped: " + stepName, 
                        "Step was skipped: " + event.getMessage());
                }
            }
        }
    }
    
    @Override
    public String getName() {
        return "ReportingObserver";
    }
}