package runner.observer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Event publisher for test execution events using Observer pattern
 */
@Slf4j
public class TestExecutionEventPublisher {
    
    private final List<TestExecutionObserver> observers = new CopyOnWriteArrayList<>();
    
    /**
     * Add an observer to receive test execution events
     * @param observer The observer to add
     */
    public void addObserver(TestExecutionObserver observer) {
        if (observer == null) {
            log.warn("Cannot add null observer");
            return;
        }
        
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.debug("Added observer: {}", observer.getName());
        } else {
            log.debug("Observer {} already registered", observer.getName());
        }
    }
    
    /**
     * Remove an observer from receiving test execution events
     * @param observer The observer to remove
     */
    public void removeObserver(TestExecutionObserver observer) {
        if (observer == null) {
            return;
        }
        
        if (observers.remove(observer)) {
            log.debug("Removed observer: {}", observer.getName());
        }
    }
    
    /**
     * Remove all observers
     */
    public void clearObservers() {
        int count = observers.size();
        observers.clear();
        log.debug("Cleared {} observers", count);
    }
    
    /**
     * Publish an event to all registered observers
     * @param event The event to publish
     */
    public void publishEvent(TestExecutionEvent event) {
        if (event == null) {
            log.warn("Cannot publish null event");
            return;
        }
        
        log.trace("Publishing event: {}", event);
        
        for (TestExecutionObserver observer : observers) {
            try {
                if (observer.shouldNotify(event.getEventType())) {
                    observer.onEvent(event);
                }
            } catch (Exception e) {
                log.error("Error notifying observer {} about event {}: {}", 
                    observer.getName(), event.getEventType(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Convenience method to publish scenario started event
     * @param message The message describing the scenario
     */
    public void publishScenarioStarted(String message) {
        publishEvent(new TestExecutionEvent(TestExecutionEvent.EventType.SCENARIO_STARTED, message));
    }
    
    /**
     * Convenience method to publish scenario completed event
     * @param message The message describing the completion
     */
    public void publishScenarioCompleted(String message) {
        publishEvent(new TestExecutionEvent(TestExecutionEvent.EventType.SCENARIO_COMPLETED, message));
    }
    
    /**
     * Convenience method to publish scenario failed event
     * @param message The failure message
     * @param exception The exception that caused the failure
     */
    public void publishScenarioFailed(String message, Throwable exception) {
        publishEvent(new TestExecutionEvent(TestExecutionEvent.EventType.SCENARIO_FAILED, null, -1, message, exception));
    }
    
    /**
     * Get the number of registered observers
     * @return Number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Check if there are any observers registered
     * @return true if there are observers, false otherwise
     */
    public boolean hasObservers() {
        return !observers.isEmpty();
    }
}