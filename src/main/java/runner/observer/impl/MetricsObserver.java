package runner.observer.impl;

import lombok.extern.slf4j.Slf4j;
import runner.observer.TestExecutionEvent;
import runner.observer.TestExecutionObserver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Observer that collects metrics about test execution
 */
@Slf4j
public class MetricsObserver implements TestExecutionObserver {
    
    private final AtomicInteger scenariosStarted = new AtomicInteger(0);
    private final AtomicInteger scenariosCompleted = new AtomicInteger(0);
    private final AtomicInteger scenariosFailed = new AtomicInteger(0);
    private final AtomicInteger stepsStarted = new AtomicInteger(0);
    private final AtomicInteger stepsCompleted = new AtomicInteger(0);
    private final AtomicInteger stepsFailed = new AtomicInteger(0);
    private final AtomicInteger stepsSkipped = new AtomicInteger(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    
    private LocalDateTime scenarioStartTime;
    private LocalDateTime stepStartTime;
    
    @Override
    public void onEvent(TestExecutionEvent event) {
        switch (event.getEventType()) {
            case SCENARIO_STARTED -> {
                scenariosStarted.incrementAndGet();
                scenarioStartTime = event.getTimestamp();
                log.debug("Scenario started. Total scenarios started: {}", scenariosStarted.get());
            }
            case SCENARIO_COMPLETED -> {
                scenariosCompleted.incrementAndGet();
                if (scenarioStartTime != null) {
                    long duration = Duration.between(scenarioStartTime, event.getTimestamp()).toMillis();
                    totalExecutionTime.addAndGet(duration);
                    log.debug("Scenario completed in {}ms. Total completed: {}", duration, scenariosCompleted.get());
                }
            }
            case SCENARIO_FAILED -> {
                scenariosFailed.incrementAndGet();
                if (scenarioStartTime != null) {
                    long duration = Duration.between(scenarioStartTime, event.getTimestamp()).toMillis();
                    totalExecutionTime.addAndGet(duration);
                    log.debug("Scenario failed after {}ms. Total failed: {}", duration, scenariosFailed.get());
                }
            }
            case STEP_STARTED -> {
                stepsStarted.incrementAndGet();
                stepStartTime = event.getTimestamp();
            }
            case STEP_COMPLETED -> {
                stepsCompleted.incrementAndGet();
                logStepDuration(event, "completed");
            }
            case STEP_FAILED -> {
                stepsFailed.incrementAndGet();
                logStepDuration(event, "failed");
            }
            case STEP_SKIPPED -> {
                stepsSkipped.incrementAndGet();
            }
        }
    }
    
    private void logStepDuration(TestExecutionEvent event, String status) {
        if (stepStartTime != null) {
            long duration = Duration.between(stepStartTime, event.getTimestamp()).toMillis();
            if (event.getStep() != null) {
                log.trace("Step {} {} in {}ms", event.getStepIndex() + 1, status, duration);
            }
        }
    }
    
    /**
     * Get execution metrics summary
     */
    public ExecutionMetrics getMetrics() {
        return new ExecutionMetrics(
            scenariosStarted.get(),
            scenariosCompleted.get(),
            scenariosFailed.get(),
            stepsStarted.get(),
            stepsCompleted.get(),
            stepsFailed.get(),
            stepsSkipped.get(),
            totalExecutionTime.get()
        );
    }
    
    /**
     * Reset all metrics
     */
    public void reset() {
        scenariosStarted.set(0);
        scenariosCompleted.set(0);
        scenariosFailed.set(0);
        stepsStarted.set(0);
        stepsCompleted.set(0);
        stepsFailed.set(0);
        stepsSkipped.set(0);
        totalExecutionTime.set(0);
        log.debug("Metrics reset");
    }
    
    @Override
    public String getName() {
        return "MetricsObserver";
    }
    
    /**
     * Immutable metrics data
     */
    public static class ExecutionMetrics {
        private final int scenariosStarted;
        private final int scenariosCompleted;
        private final int scenariosFailed;
        private final int stepsStarted;
        private final int stepsCompleted;
        private final int stepsFailed;
        private final int stepsSkipped;
        private final long totalExecutionTimeMs;
        
        public ExecutionMetrics(int scenariosStarted, int scenariosCompleted, int scenariosFailed,
                              int stepsStarted, int stepsCompleted, int stepsFailed, int stepsSkipped,
                              long totalExecutionTimeMs) {
            this.scenariosStarted = scenariosStarted;
            this.scenariosCompleted = scenariosCompleted;
            this.scenariosFailed = scenariosFailed;
            this.stepsStarted = stepsStarted;
            this.stepsCompleted = stepsCompleted;
            this.stepsFailed = stepsFailed;
            this.stepsSkipped = stepsSkipped;
            this.totalExecutionTimeMs = totalExecutionTimeMs;
        }
        
        // Getters
        public int getScenariosStarted() { return scenariosStarted; }
        public int getScenariosCompleted() { return scenariosCompleted; }
        public int getScenariosFailed() { return scenariosFailed; }
        public int getStepsStarted() { return stepsStarted; }
        public int getStepsCompleted() { return stepsCompleted; }
        public int getStepsFailed() { return stepsFailed; }
        public int getStepsSkipped() { return stepsSkipped; }
        public long getTotalExecutionTimeMs() { return totalExecutionTimeMs; }
        
        public double getScenarioSuccessRate() {
            int total = scenariosCompleted + scenariosFailed;
            return total == 0 ? 0.0 : (double) scenariosCompleted / total * 100;
        }
        
        public double getStepSuccessRate() {
            int total = stepsCompleted + stepsFailed;
            return total == 0 ? 0.0 : (double) stepsCompleted / total * 100;
        }
        
        @Override
        public String toString() {
            return String.format(
                "ExecutionMetrics{scenarios: %d started, %d completed, %d failed (%.1f%% success), " +
                "steps: %d started, %d completed, %d failed, %d skipped (%.1f%% success), " +
                "total time: %dms}",
                scenariosStarted, scenariosCompleted, scenariosFailed, getScenarioSuccessRate(),
                stepsStarted, stepsCompleted, stepsFailed, stepsSkipped, getStepSuccessRate(),
                totalExecutionTimeMs
            );
        }
    }
}