package runner.factory;

import lombok.extern.slf4j.Slf4j;
import runner.config.TestConfiguration;
import runner.strategy.ExecutionStrategy;
import runner.strategy.impl.ConditionalExecutionStrategy;
import runner.strategy.impl.RetryExecutionStrategy;
import runner.strategy.impl.SequentialExecutionStrategy;

/**
 * Factory for creating execution strategies based on configuration and requirements
 */
@Slf4j
public class ExecutionStrategyFactory {
    
    public enum StrategyType {
        SEQUENTIAL_STOP_ON_FAILURE,
        SEQUENTIAL_CONTINUE_ON_FAILURE,
        RETRY_SEQUENTIAL,
        CONDITIONAL_SEQUENTIAL,
        AUTO // Automatically choose based on configuration
    }
    
    /**
     * Create a strategy based on the specified type
     */
    public static ExecutionStrategy createStrategy(StrategyType type) {
        return createStrategy(type, TestConfiguration.getInstance());
    }
    
    /**
     * Create a strategy based on the specified type and configuration
     */
    public static ExecutionStrategy createStrategy(StrategyType type, TestConfiguration config) {
        if (type == null) {
            type = StrategyType.AUTO;
        }
        
        if (config == null) {
            config = TestConfiguration.getInstance();
        }
        
        log.debug("Creating execution strategy: {} with config from environment: {}", type, config.getEnvironment());
        
        return switch (type) {
            case SEQUENTIAL_STOP_ON_FAILURE -> createSequentialStopOnFailure();
            case SEQUENTIAL_CONTINUE_ON_FAILURE -> createSequentialContinueOnFailure();
            case RETRY_SEQUENTIAL -> createRetryStrategy(config);
            case CONDITIONAL_SEQUENTIAL -> createConditionalStrategy();
            case AUTO -> createAutoStrategy(config);
        };
    }
    
    /**
     * Create a sequential strategy that stops on first failure
     */
    protected static ExecutionStrategy createSequentialStopOnFailure() {
        log.debug("Creating sequential strategy (stop on failure)");
        return new SequentialExecutionStrategy(true);
    }
    
    /**
     * Create a sequential strategy that continues on failure
     */
    protected static ExecutionStrategy createSequentialContinueOnFailure() {
        log.debug("Creating sequential strategy (continue on failure)");
        return new SequentialExecutionStrategy(false);
    }
    
    /**
     * Create a retry strategy using configuration
     */
    protected static ExecutionStrategy createRetryStrategy(TestConfiguration config) {
        int retryCount = config.getRetryCount();
        log.debug("Creating retry strategy with {} retries", retryCount);
        
        ExecutionStrategy baseStrategy = createSequentialStopOnFailure();
        return new RetryExecutionStrategy(baseStrategy, retryCount);
    }
    
    /**
     * Create a conditional execution strategy
     */
    protected static ExecutionStrategy createConditionalStrategy() {
        log.debug("Creating conditional strategy");
        ExecutionStrategy baseStrategy = createSequentialStopOnFailure();
        return new ConditionalExecutionStrategy(baseStrategy);
    }
    
    /**
     * Automatically create the best strategy based on configuration
     */
    protected static ExecutionStrategy createAutoStrategy(TestConfiguration config) {
        log.debug("Auto-creating strategy based on configuration");
        
        // Start with base sequential strategy
        ExecutionStrategy strategy = createSequentialStopOnFailure();
        
        // Add retry capability if configured
        int retryCount = config.getRetryCount();
        if (retryCount > 0) {
            log.debug("Wrapping with retry strategy: {} retries", retryCount);
            strategy = new RetryExecutionStrategy(strategy, retryCount);
        }
        
        // Add conditional logic if in CI environment
        if ("ci".equals(config.getEnvironment())) {
            log.debug("Wrapping with conditional strategy for CI environment");
            strategy = new ConditionalExecutionStrategy(strategy);
        }
        
        return strategy;
    }
    
    /**
     * Create a custom retry strategy with specific parameters
     */
    public static ExecutionStrategy createCustomRetryStrategy(int maxRetries, long retryDelayMs) {
        log.debug("Creating custom retry strategy: {} retries, {}ms delay", maxRetries, retryDelayMs);
        ExecutionStrategy baseStrategy = createSequentialStopOnFailure();
        return new RetryExecutionStrategy(baseStrategy, maxRetries, retryDelayMs);
    }
    
    /**
     * Create a strategy with custom stop-on-failure behavior and retry
     */
    public static ExecutionStrategy createCustomStrategy(boolean stopOnFailure, int retryCount) {
        log.debug("Creating custom strategy: stopOnFailure={}, retryCount={}", stopOnFailure, retryCount);
        
        ExecutionStrategy baseStrategy = new SequentialExecutionStrategy(stopOnFailure);
        
        if (retryCount > 0) {
            baseStrategy = new RetryExecutionStrategy(baseStrategy, retryCount);
        }
        
        return baseStrategy;
    }
    
    /**
     * Create a strategy for a specific environment
     */
    public static ExecutionStrategy createForEnvironment(String environment) {
        log.debug("Creating strategy for environment: {}", environment);
        
        return switch (environment != null ? environment.toLowerCase() : "default") {
            case "dev", "development" -> {
                // Development: stop on failure, no retry for fast feedback
                yield createSequentialStopOnFailure();
            }
            case "test", "testing" -> {
                // Testing: continue on failure to see all issues, with retry
                ExecutionStrategy base = createSequentialContinueOnFailure();
                yield new RetryExecutionStrategy(base, 1);
            }
            case "ci", "continuous-integration" -> {
                // CI: stop on failure with retry, conditional execution
                ExecutionStrategy base = createSequentialStopOnFailure();
                ExecutionStrategy retry = new RetryExecutionStrategy(base, 2);
                yield new ConditionalExecutionStrategy(retry);
            }
            case "prod", "production" -> {
                // Production: very conservative with multiple retries
                ExecutionStrategy base = createSequentialStopOnFailure();
                yield new RetryExecutionStrategy(base, 3, 2000); // 3 retries, 2s delay
            }
            default -> {
                // Default: auto strategy based on current configuration
                yield createAutoStrategy(TestConfiguration.getInstance());
            }
        };
    }
    
    /**
     * Get strategy type name for a strategy instance
     */
    public static String getStrategyTypeName(ExecutionStrategy strategy) {
        if (strategy == null) {
            return "Unknown";
        }
        
        String className = strategy.getClass().getSimpleName();
        
        if (strategy instanceof RetryExecutionStrategy retryStrategy) {
            ExecutionStrategy delegate = retryStrategy.getDelegateStrategy();
            return "Retry(" + getStrategyTypeName(delegate) + ")";
        } else if (strategy instanceof ConditionalExecutionStrategy conditionalStrategy) {
            ExecutionStrategy delegate = conditionalStrategy.getDelegateStrategy();
            return "Conditional(" + getStrategyTypeName(delegate) + ")";
        } else if (strategy instanceof SequentialExecutionStrategy sequentialStrategy) {
            return sequentialStrategy.isStopOnFirstFailure() ? 
                "Sequential(StopOnFailure)" : "Sequential(ContinueOnFailure)";
        }
        
        return className;
    }
    
    /**
     * Check if a strategy is suitable for a specific environment
     */
    public static boolean isSuitableForEnvironment(ExecutionStrategy strategy, String environment) {
        if (strategy == null || environment == null) {
            return false;
        }
        
        return switch (environment.toLowerCase()) {
            case "dev", "development" -> !(strategy instanceof RetryExecutionStrategy);
            case "test", "testing" -> true; // All strategies suitable for testing
            case "ci", "continuous-integration" -> strategy instanceof RetryExecutionStrategy || 
                                                 strategy instanceof ConditionalExecutionStrategy;
            case "prod", "production" -> strategy instanceof RetryExecutionStrategy;
            default -> true;
        };
    }
}