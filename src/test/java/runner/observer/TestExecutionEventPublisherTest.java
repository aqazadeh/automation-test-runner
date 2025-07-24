package runner.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import runner.TestBase;
import runner.observer.impl.LoggingObserver;
import runner.observer.impl.MetricsObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TestExecutionEventPublisher Tests")
class TestExecutionEventPublisherTest extends TestBase {
    
    private TestExecutionEventPublisher publisher;
    
    @Mock
    private TestExecutionObserver mockObserver1;
    
    @Mock
    private TestExecutionObserver mockObserver2;
    
    @BeforeEach
    @Override
    protected void setUpTest() {
        publisher = new TestExecutionEventPublisher();
        
        when(mockObserver1.getName()).thenReturn("MockObserver1");
        when(mockObserver2.getName()).thenReturn("MockObserver2");
        when(mockObserver1.shouldNotify(any())).thenReturn(true);
        when(mockObserver2.shouldNotify(any())).thenReturn(true);
    }
    
    @Test
    @DisplayName("Should add observers successfully")
    void shouldAddObserversSuccessfully() {
        // When
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        // Then
        assertEquals(2, publisher.getObserverCount());
        assertTrue(publisher.hasObservers());
    }
    
    @Test
    @DisplayName("Should not add null observer")
    void shouldNotAddNullObserver() {
        // When
        publisher.addObserver(null);
        
        // Then
        assertEquals(0, publisher.getObserverCount());
        assertFalse(publisher.hasObservers());
    }
    
    @Test
    @DisplayName("Should not add duplicate observers")
    void shouldNotAddDuplicateObservers() {
        // When
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver1);
        
        // Then
        assertEquals(1, publisher.getObserverCount());
    }
    
    @Test
    @DisplayName("Should remove observers successfully")
    void shouldRemoveObserversSuccessfully() {
        // Given
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        // When
        publisher.removeObserver(mockObserver1);
        
        // Then
        assertEquals(1, publisher.getObserverCount());
    }
    
    @Test
    @DisplayName("Should clear all observers")
    void shouldClearAllObservers() {
        // Given
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        // When
        publisher.clearObservers();
        
        // Then
        assertEquals(0, publisher.getObserverCount());
        assertFalse(publisher.hasObservers());
    }
    
    @Test
    @DisplayName("Should publish events to all observers")
    void shouldPublishEventsToAllObservers() {
        // Given
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        TestExecutionEvent event = new TestExecutionEvent(
            TestExecutionEvent.EventType.SCENARIO_STARTED, "Test scenario");
        
        // When
        publisher.publishEvent(event);
        
        // Then
        verify(mockObserver1).onEvent(event);
        verify(mockObserver2).onEvent(event);
    }
    
    @Test
    @DisplayName("Should not publish null events")
    void shouldNotPublishNullEvents() {
        // Given
        publisher.addObserver(mockObserver1);
        
        // When
        publisher.publishEvent(null);
        
        // Then
        verify(mockObserver1, never()).onEvent(any());
    }
    
    @Test
    @DisplayName("Should handle observer exceptions gracefully")
    void shouldHandleObserverExceptionsGracefully() {
        // Given
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        doThrow(new RuntimeException("Observer 1 failed")).when(mockObserver1).onEvent(any());
        
        TestExecutionEvent event = new TestExecutionEvent(
            TestExecutionEvent.EventType.SCENARIO_STARTED, "Test scenario");
        
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> publisher.publishEvent(event));
        
        // Observer 2 should still be notified despite observer 1 failing
        verify(mockObserver2).onEvent(event);
    }
    
    @Test
    @DisplayName("Should respect observer shouldNotify filter")
    void shouldRespectObserverShouldNotifyFilter() {
        // Given
        when(mockObserver1.shouldNotify(TestExecutionEvent.EventType.SCENARIO_STARTED)).thenReturn(false);
        when(mockObserver2.shouldNotify(TestExecutionEvent.EventType.SCENARIO_STARTED)).thenReturn(true);
        
        publisher.addObserver(mockObserver1);
        publisher.addObserver(mockObserver2);
        
        TestExecutionEvent event = new TestExecutionEvent(
            TestExecutionEvent.EventType.SCENARIO_STARTED, "Test scenario");
        
        // When
        publisher.publishEvent(event);
        
        // Then
        verify(mockObserver1, never()).onEvent(any());
        verify(mockObserver2).onEvent(event);
    }
    
    @Test
    @DisplayName("Should publish scenario convenience methods")
    void shouldPublishScenarioConvenienceMethods() {
        // Given
        publisher.addObserver(mockObserver1);
        
        // When
        publisher.publishScenarioStarted("Test started");
        publisher.publishScenarioCompleted("Test completed");
        publisher.publishScenarioFailed("Test failed", new RuntimeException("Test error"));
        
        // Then
        verify(mockObserver1, times(3)).onEvent(any());
    }
    
    @Test
    @DisplayName("Should work with real observers")
    void shouldWorkWithRealObservers() {
        // Given
        LoggingObserver loggingObserver = new LoggingObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        
        publisher.addObserver(loggingObserver);
        publisher.addObserver(metricsObserver);
        
        // When
        publisher.publishScenarioStarted("Integration test scenario");
        publisher.publishScenarioCompleted("Integration test completed");
        
        // Then
        assertEquals(2, publisher.getObserverCount());
        
        // Check metrics observer received events
        var metrics = metricsObserver.getMetrics();
        assertEquals(1, metrics.getScenariosStarted());
        assertEquals(1, metrics.getScenariosCompleted());
    }
}