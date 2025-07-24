package runner.observer;

import runner.model.step.TestStep;

import java.time.LocalDateTime;

/**
 * Represents an event that occurs during test execution
 */
public class TestExecutionEvent {
    
    public enum EventType {
        SCENARIO_STARTED,
        SCENARIO_COMPLETED,
        SCENARIO_FAILED,
        STEP_STARTED,
        STEP_COMPLETED,
        STEP_FAILED,
        STEP_SKIPPED
    }
    
    private final EventType eventType;
    private final TestStep step;
    private final int stepIndex;
    private final String message;
    private final Throwable exception;
    private final LocalDateTime timestamp;
    private final String threadName;
    
    public TestExecutionEvent(EventType eventType, TestStep step, int stepIndex, String message, Throwable exception) {
        this.eventType = eventType;
        this.step = step;
        this.stepIndex = stepIndex;
        this.message = message;
        this.exception = exception;
        this.timestamp = LocalDateTime.now();
        this.threadName = Thread.currentThread().getName();
    }
    
    public TestExecutionEvent(EventType eventType, String message) {
        this(eventType, null, -1, message, null);
    }
    
    public TestExecutionEvent(EventType eventType, TestStep step, int stepIndex, String message) {
        this(eventType, step, stepIndex, message, null);
    }
    
    // Getters
    public EventType getEventType() { return eventType; }
    public TestStep getStep() { return step; }
    public int getStepIndex() { return stepIndex; }
    public String getMessage() { return message; }
    public Throwable getException() { return exception; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getThreadName() { return threadName; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TestExecutionEvent{")
          .append("type=").append(eventType)
          .append(", timestamp=").append(timestamp)
          .append(", thread=").append(threadName);
        
        if (step != null) {
            sb.append(", step=").append(step.getClass().getSimpleName())
              .append(", stepIndex=").append(stepIndex);
        }
        
        if (message != null) {
            sb.append(", message='").append(message).append("'");
        }
        
        if (exception != null) {
            sb.append(", exception=").append(exception.getClass().getSimpleName());
        }
        
        sb.append("}");
        return sb.toString();
    }
}