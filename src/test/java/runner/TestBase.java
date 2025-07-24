package runner;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import runner.config.TestConfiguration;
import runner.observer.TestExecutionEventPublisher;
import runner.observer.impl.MetricsObserver;

/**
 * Base class for all test classes providing common setup and utilities
 */
@ExtendWith(MockitoExtension.class)
public abstract class TestBase {
    
    protected static TestConfiguration testConfig;
    protected MetricsObserver metricsObserver;
    
    @BeforeAll
    static void setUpClass() {
        // Initialize test configuration
        System.setProperty("test.environment", "test");
        testConfig = TestConfiguration.getInstance();
    }
    
    @AfterAll
    static void tearDownClass() {
        // Clean up system properties
        System.clearProperty("test.environment");
    }
    
    @BeforeEach
    void setUp() {
        // Set up metrics observer for each test
        metricsObserver = new MetricsObserver();
        
        // Additional setup can be added by subclasses
        setUpTest();
    }
    
    @AfterEach
    void tearDown() {
        // Reset metrics observer
        if (metricsObserver != null) {
            metricsObserver.reset();
        }
        
        // Additional cleanup can be added by subclasses
        tearDownTest();
    }
    
    /**
     * Template method for subclasses to add specific setup
     */
    protected void setUpTest() {
        // Override in subclasses if needed
    }
    
    /**
     * Template method for subclasses to add specific cleanup
     */
    protected void tearDownTest() {
        // Override in subclasses if needed
    }
    
    /**
     * Utility method to wait for a short period
     */
    protected void waitShort() throws InterruptedException {
        Thread.sleep(100);
    }
    
    /**
     * Utility method to wait for a medium period
     */
    protected void waitMedium() throws InterruptedException {
        Thread.sleep(500);
    }
    
    /**
     * Utility method to create a test event publisher with metrics observer
     */
    protected TestExecutionEventPublisher createEventPublisher() {
        TestExecutionEventPublisher publisher = new TestExecutionEventPublisher();
        publisher.addObserver(metricsObserver);
        return publisher;
    }
    
    /**
     * Assert that a condition becomes true within a timeout
     */
    protected void assertEventually(BooleanSupplier condition, long timeoutMs, String message) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (condition.getAsBoolean()) {
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        Assertions.fail(message);
    }
    
    /**
     * Assert that a condition becomes true within 5 seconds
     */
    protected void assertEventually(BooleanSupplier condition, String message) {
        assertEventually(condition, 5000, message);
    }
    
    @FunctionalInterface
    protected interface BooleanSupplier {
        boolean getAsBoolean();
    }
}