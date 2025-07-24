package runner.strategy;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.config.TestConfiguration;
import runner.model.step.TestStep;
import runner.strategy.impl.SequentialExecutionStrategy;
import runner.strategy.impl.RetryExecutionStrategy;
import runner.validation.ValidationChainBuilder;
import runner.validation.ValidationHandler;
import runner.validation.ValidationResult;

import java.util.List;

/**
 * Context class for strategy pattern - manages execution strategy selection and execution
 */
@Slf4j
public class ExecutionContext {
    
    private ExecutionStrategy strategy;
    private ValidationHandler<WebDriver> webDriverValidator;
    private ValidationHandler<TestStep> stepValidator;
    
    public ExecutionContext() {
        this(createDefaultStrategy());
    }
    
    public ExecutionContext(ExecutionStrategy strategy) {
        this.strategy = strategy != null ? strategy : createDefaultStrategy();
        this.webDriverValidator = ValidationChainBuilder.createWebDriverChain();
        this.stepValidator = ValidationChainBuilder.createTestStepChain();
    }
    
    public ExecutionContext(ExecutionStrategy strategy, 
                          ValidationHandler<WebDriver> webDriverValidator,
                          ValidationHandler<TestStep> stepValidator) {
        this.strategy = strategy != null ? strategy : createDefaultStrategy();
        this.webDriverValidator = webDriverValidator != null ? webDriverValidator : ValidationChainBuilder.createWebDriverChain();
        this.stepValidator = stepValidator != null ? stepValidator : ValidationChainBuilder.createTestStepChain();
    }
    
    /**
     * Set the execution strategy
     */
    public void setStrategy(ExecutionStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Execution strategy cannot be null");
        }
        
        log.debug("Switching execution strategy from {} to {}", 
            this.strategy.getStrategyName(), strategy.getStrategyName());
        this.strategy = strategy;
    }
    
    /**
     * Get the current execution strategy
     */
    public ExecutionStrategy getStrategy() {
        return strategy;
    }
    
    /**
     * Execute steps using the current strategy with validation
     */
    public void execute(WebDriver driver, List<TestStep> steps) throws Exception {
        if (strategy == null) {
            throw new IllegalStateException("No execution strategy set");
        }
        
        // Validate WebDriver first
        if (webDriverValidator != null) {
            ValidationResult driverResult = webDriverValidator.handle(driver);
            if (driverResult.isInvalid()) {
                throw new IllegalArgumentException("WebDriver validation failed: " + driverResult.getErrorsAsString());
            }
            if (driverResult.hasWarnings()) {
                log.warn("WebDriver validation warnings: {}", driverResult.getWarningsAsString());
            }
        }
        
        // Validate each step if validator is set
        if (stepValidator != null && steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                TestStep step = steps.get(i);
                ValidationResult stepResult = stepValidator.handle(step);
                if (stepResult.isInvalid()) {
                    throw new IllegalArgumentException(
                        String.format("Step %d validation failed: %s", i + 1, stepResult.getErrorsAsString()));
                }
                if (stepResult.hasWarnings()) {
                    log.warn("Step {} validation warnings: {}", i + 1, stepResult.getWarningsAsString());
                }
            }
        }
        
        if (!strategy.supports(steps)) {
            throw new IllegalArgumentException("Current strategy '" + strategy.getStrategyName() + 
                "' does not support the provided steps");
        }
        
        log.info("Executing {} steps using strategy: {} (validation enabled)", 
            steps != null ? steps.size() : 0, strategy.getStrategyName());
        
        strategy.execute(driver, steps);
    }
    
    /**
     * Create default strategy based on configuration
     */
    private static ExecutionStrategy createDefaultStrategy() {
        TestConfiguration config = TestConfiguration.getInstance();
        
        // Base strategy is sequential
        ExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        
        // Wrap with retry if configured
        int retryCount = config.getRetryCount();
        if (retryCount > 0) {
            baseStrategy = new RetryExecutionStrategy(baseStrategy, retryCount);
        }
        
        return baseStrategy;
    }
    
    /**
     * Create a strategy context with automatic strategy selection based on configuration
     */
    public static ExecutionContext createConfigured() {
        return new ExecutionContext(createDefaultStrategy());
    }
    
    /**
     * Create a strategy context with sequential execution (stop on failure)
     */
    public static ExecutionContext createSequential() {
        return new ExecutionContext(new SequentialExecutionStrategy(true));
    }
    
    /**
     * Create a strategy context with sequential execution (continue on failure)
     */
    public static ExecutionContext createSequentialContinueOnFailure() {
        return new ExecutionContext(new SequentialExecutionStrategy(false));
    }
    
    /**
     * Create a strategy context with retry capability
     */
    public static ExecutionContext createWithRetry(int maxRetries) {
        ExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        ExecutionStrategy retryStrategy = new RetryExecutionStrategy(baseStrategy, maxRetries);
        return new ExecutionContext(retryStrategy);
    }
    
    /**
     * Get strategy information
     */
    public String getStrategyInfo() {
        return String.format("Strategy: %s - %s", 
            strategy.getStrategyName(), strategy.getDescription());
    }
}