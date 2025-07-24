package runner.factory;

import lombok.extern.slf4j.Slf4j;
import runner.config.TestConfiguration;
import runner.observer.TestExecutionObserver;
import runner.observer.impl.LoggingObserver;
import runner.observer.impl.MetricsObserver;
import runner.observer.impl.ReportingObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating test execution observers based on configuration and requirements
 */
@Slf4j
public class ObserverFactory {
    
    public enum ObserverType {
        LOGGING,
        LOGGING_WITH_STACK_TRACES,
        REPORTING,
        REPORTING_NO_STACK_TRACES,
        METRICS,
        ALL_DEFAULT, // Create default set of observers
        MINIMAL // Only essential observers
    }
    
    /**
     * Create a single observer based on the specified type
     */
    public static TestExecutionObserver createObserver(ObserverType type) {
        if (type == null) {
            type = ObserverType.LOGGING;
        }
        
        log.debug("Creating observer: {}", type);
        
        return switch (type) {
            case LOGGING -> new LoggingObserver(false);
            case LOGGING_WITH_STACK_TRACES -> new LoggingObserver(true);
            case REPORTING -> new ReportingObserver(true);
            case REPORTING_NO_STACK_TRACES -> new ReportingObserver(false);
            case METRICS -> new MetricsObserver();
            case ALL_DEFAULT, MINIMAL -> throw new IllegalArgumentException(
                type + " creates multiple observers, use createObservers() method");
        };
    }
    
    /**
     * Create multiple observers based on the specified type
     */
    public static List<TestExecutionObserver> createObservers(ObserverType type) {
        return createObservers(type, TestConfiguration.getInstance());
    }
    
    /**
     * Create multiple observers based on the specified type and configuration
     */
    public static List<TestExecutionObserver> createObservers(ObserverType type, TestConfiguration config) {
        if (type == null) {
            type = ObserverType.ALL_DEFAULT;
        }
        
        if (config == null) {
            config = TestConfiguration.getInstance();
        }
        
        log.debug("Creating observers: {} for environment: {}", type, config.getEnvironment());
        
        return switch (type) {
            case ALL_DEFAULT -> createDefaultObservers(config);
            case MINIMAL -> createMinimalObservers(config);
            case LOGGING, LOGGING_WITH_STACK_TRACES, REPORTING, REPORTING_NO_STACK_TRACES, METRICS -> 
                List.of(createObserver(type));
        };
    }
    
    /**
     * Create default set of observers based on configuration
     */
    protected static List<TestExecutionObserver> createDefaultObservers(TestConfiguration config) {
        List<TestExecutionObserver> observers = new ArrayList<>();
        
        // Always include logging observer
        boolean includeStackTraces = "dev".equals(config.getEnvironment()) || 
                                   "development".equals(config.getEnvironment());
        observers.add(new LoggingObserver(includeStackTraces));
        
        // Include reporting observer
        observers.add(new ReportingObserver(true));
        
        // Include metrics observer for performance monitoring
        observers.add(new MetricsObserver());
        
        log.debug("Created {} default observers", observers.size());
        return observers;
    }
    
    /**
     * Create minimal set of observers for lightweight execution
     */
    protected static List<TestExecutionObserver> createMinimalObservers(TestConfiguration config) {
        List<TestExecutionObserver> observers = new ArrayList<>();
        
        // Only essential logging
        observers.add(new LoggingObserver(false));
        
        // Only include reporting if explicitly configured
        if (config.getReportsDirectory() != null && !config.getReportsDirectory().trim().isEmpty()) {
            observers.add(new ReportingObserver(false));
        }
        
        log.debug("Created {} minimal observers", observers.size());
        return observers;
    }
    
    /**
     * Create observers for a specific environment
     */
    public static List<TestExecutionObserver> createForEnvironment(String environment) {
        log.debug("Creating observers for environment: {}", environment);
        
        List<TestExecutionObserver> observers = new ArrayList<>();
        
        switch (environment != null ? environment.toLowerCase() : "default") {
            case "dev", "development" -> {
                // Development: detailed logging with stack traces, no metrics
                observers.add(new LoggingObserver(true));
                observers.add(new ReportingObserver(true));
            }
            case "test", "testing" -> {
                // Testing: standard logging, reporting, and metrics
                observers.add(new LoggingObserver(false));
                observers.add(new ReportingObserver(true));
                observers.add(new MetricsObserver());
            }
            case "ci", "continuous-integration" -> {
                // CI: minimal logging, full reporting, detailed metrics
                observers.add(new LoggingObserver(false));
                observers.add(new ReportingObserver(true));
                observers.add(new MetricsObserver());
            }
            case "prod", "production" -> {
                // Production: minimal logging, essential reporting
                observers.add(new LoggingObserver(false));
                observers.add(new ReportingObserver(false));
            }
            default -> {
                // Default: use configuration-based creation
                TestConfiguration config = TestConfiguration.getInstance();
                return createDefaultObservers(config);
            }
        }
        
        log.debug("Created {} observers for environment: {}", observers.size(), environment);
        return observers;
    }
    
    /**
     * Create observers based on performance requirements
     */
    public static List<TestExecutionObserver> createForPerformance(boolean highPerformance) {
        List<TestExecutionObserver> observers = new ArrayList<>();
        
        if (highPerformance) {
            // High performance: minimal observers
            observers.add(new LoggingObserver(false));
            log.debug("Created {} high-performance observers", observers.size());
        } else {
            // Standard performance: full observability
            observers.add(new LoggingObserver(true));
            observers.add(new ReportingObserver(true));
            observers.add(new MetricsObserver());
            log.debug("Created {} standard observers", observers.size());
        }
        
        return observers;
    }
    
    /**
     * Create custom observer configuration
     */
    public static List<TestExecutionObserver> createCustom(boolean enableLogging, 
                                                          boolean enableStackTraces,
                                                          boolean enableReporting, 
                                                          boolean enableMetrics) {
        List<TestExecutionObserver> observers = new ArrayList<>();
        
        if (enableLogging) {
            observers.add(new LoggingObserver(enableStackTraces));
        }
        
        if (enableReporting) {
            observers.add(new ReportingObserver(enableStackTraces));
        }
        
        if (enableMetrics) {
            observers.add(new MetricsObserver());
        }
        
        log.debug("Created {} custom observers (logging: {}, stackTraces: {}, reporting: {}, metrics: {})", 
            observers.size(), enableLogging, enableStackTraces, enableReporting, enableMetrics);
        
        return observers;
    }
    
    /**
     * Get observer type name for an observer instance
     */
    public static String getObserverTypeName(TestExecutionObserver observer) {
        if (observer == null) {
            return "Unknown";
        }
        
        return observer.getClass().getSimpleName();
    }
    
    /**
     * Check if an observer is suitable for a specific environment
     */
    public static boolean isSuitableForEnvironment(TestExecutionObserver observer, String environment) {
        if (observer == null || environment == null) {
            return false;
        }
        
        String observerType = observer.getClass().getSimpleName();
        
        return switch (environment.toLowerCase()) {
            case "dev", "development" -> true; // All observers suitable for development
            case "test", "testing" -> true; // All observers suitable for testing
            case "ci", "continuous-integration" -> !observerType.contains("Detailed"); // Avoid overly verbose observers
            case "prod", "production" -> observerType.equals("LoggingObserver") || 
                                       observerType.equals("ReportingObserver"); // Only essential observers
            default -> true;
        };
    }
}