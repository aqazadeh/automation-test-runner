package runner.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.config.TestConfiguration;
import runner.model.step.TestStep;
import runner.strategy.ExecutionStrategy;

import java.util.List;

/**
 * Retry execution strategy - wraps another strategy with retry logic
 */
@Slf4j
public class RetryExecutionStrategy implements ExecutionStrategy {
    
    private final ExecutionStrategy delegateStrategy;
    private final int maxRetries;
    private final long retryDelayMs;
    
    public RetryExecutionStrategy(ExecutionStrategy delegateStrategy) {
        this(delegateStrategy, TestConfiguration.getInstance().getRetryCount(), 1000);
    }
    
    public RetryExecutionStrategy(ExecutionStrategy delegateStrategy, int maxRetries) {
        this(delegateStrategy, maxRetries, 1000);
    }
    
    public RetryExecutionStrategy(ExecutionStrategy delegateStrategy, int maxRetries, long retryDelayMs) {
        this.delegateStrategy = delegateStrategy;
        this.maxRetries = Math.max(0, maxRetries);
        this.retryDelayMs = Math.max(0, retryDelayMs);
    }
    
    @Override
    public void execute(WebDriver driver, List<TestStep> steps) throws Exception {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        
        if (steps == null || steps.isEmpty()) {
            log.warn("No steps provided to execute");
            return;
        }
        
        log.info("Executing {} steps with retry strategy (maxRetries: {}, delay: {}ms)", 
            steps.size(), maxRetries, retryDelayMs);
        
        Exception lastException = null;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    log.info("Retry attempt {} of {}", attempt, maxRetries);
                    
                    if (retryDelayMs > 0) {
                        log.debug("Waiting {}ms before retry", retryDelayMs);
                        Thread.sleep(retryDelayMs);
                    }
                }
                
                // Delegate to the wrapped strategy
                delegateStrategy.execute(driver, steps);
                
                if (attempt > 0) {
                    log.info("Execution succeeded on retry attempt {}", attempt);
                }
                
                return; // Success, no need to retry
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Execution interrupted during retry", e);
            } catch (Exception e) {
                lastException = e;
                
                if (attempt == maxRetries) {
                    log.error("Execution failed after {} attempts. Last error: {}", 
                        maxRetries + 1, e.getMessage());
                } else {
                    log.warn("Execution attempt {} failed: {}. Will retry.", attempt + 1, e.getMessage());
                }
            }
        }
        
        // If we get here, all attempts failed
        throw new RuntimeException(String.format(
            "Execution failed after %d attempts. Last error: %s", 
            maxRetries + 1, lastException.getMessage()), lastException);
    }
    
    @Override
    public String getStrategyName() {
        return "Retry(" + delegateStrategy.getStrategyName() + ", maxRetries=" + maxRetries + ")";
    }
    
    @Override
    public String getDescription() {
        return String.format("Wraps %s with retry logic. Will retry up to %d times with %dms delay between attempts.",
            delegateStrategy.getStrategyName(), maxRetries, retryDelayMs);
    }
    
    @Override
    public boolean supports(List<TestStep> steps) {
        return delegateStrategy.supports(steps);
    }
    
    public ExecutionStrategy getDelegateStrategy() {
        return delegateStrategy;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public long getRetryDelayMs() {
        return retryDelayMs;
    }
}