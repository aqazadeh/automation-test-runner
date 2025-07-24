package runner.observer.impl;

import lombok.extern.slf4j.Slf4j;
import runner.observer.TestExecutionEvent;
import runner.observer.TestExecutionObserver;

/**
 * Observer that logs test execution events
 */
@Slf4j
public class LoggingObserver implements TestExecutionObserver {
    
    private final boolean logStackTraces;
    
    public LoggingObserver() {
        this(false);
    }
    
    public LoggingObserver(boolean logStackTraces) {
        this.logStackTraces = logStackTraces;
    }
    
    @Override
    public void onEvent(TestExecutionEvent event) {
        switch (event.getEventType()) {
            case SCENARIO_STARTED -> log.info("🚀 Scenario Started: {}", event.getMessage());
            case SCENARIO_COMPLETED -> log.info("✅ Scenario Completed: {}", event.getMessage());
            case SCENARIO_FAILED -> {
                log.error("❌ Scenario Failed: {}", event.getMessage());
                if (event.getException() != null && logStackTraces) {
                    log.error("Exception details:", event.getException());
                }
            }
            case STEP_STARTED -> {
                if (event.getStep() != null) {
                    log.info("▶️  Step {} Started: {} ({})", 
                        event.getStepIndex() + 1, 
                        event.getStep().getName() != null ? event.getStep().getName() : "Unnamed",
                        event.getStep().getClass().getSimpleName());
                }
            }
            case STEP_COMPLETED -> {
                if (event.getStep() != null) {
                    log.info("✅ Step {} Completed: {}", 
                        event.getStepIndex() + 1, 
                        event.getStep().getName() != null ? event.getStep().getName() : "Unnamed");
                }
            }
            case STEP_FAILED -> {
                if (event.getStep() != null) {
                    log.error("❌ Step {} Failed: {} - {}", 
                        event.getStepIndex() + 1, 
                        event.getStep().getName() != null ? event.getStep().getName() : "Unnamed",
                        event.getMessage());
                    if (event.getException() != null && logStackTraces) {
                        log.error("Step failure details:", event.getException());
                    }
                }
            }
            case STEP_SKIPPED -> {
                if (event.getStep() != null) {
                    log.warn("⏭️  Step {} Skipped: {}", 
                        event.getStepIndex() + 1, 
                        event.getMessage());
                }
            }
        }
    }
    
    @Override
    public String getName() {
        return "LoggingObserver" + (logStackTraces ? " (with stack traces)" : "");
    }
}