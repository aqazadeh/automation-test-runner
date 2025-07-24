package runner.observer;

/**
 * Observer interface for test execution events
 */
public interface TestExecutionObserver {
    
    /**
     * Called when a test execution event occurs
     * @param event The test execution event
     */
    void onEvent(TestExecutionEvent event);
    
    /**
     * Get the name of this observer (for logging and debugging)
     * @return Observer name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Check if this observer should be notified of the given event type
     * @param eventType The event type to check
     * @return true if this observer should be notified, false otherwise
     */
    default boolean shouldNotify(TestExecutionEvent.EventType eventType) {
        return true; // By default, observe all events
    }
}