package runner.strategy;

import org.openqa.selenium.WebDriver;
import runner.model.step.TestStep;

import java.util.List;

/**
 * Strategy interface for different test execution modes
 */
public interface ExecutionStrategy {
    
    /**
     * Execute a list of test steps using this strategy
     * @param driver The WebDriver instance
     * @param steps The list of test steps to execute
     * @throws Exception if execution fails
     */
    void execute(WebDriver driver, List<TestStep> steps) throws Exception;
    
    /**
     * Get the name of this execution strategy
     * @return Strategy name
     */
    String getStrategyName();
    
    /**
     * Check if this strategy supports the given execution context
     * @param steps The test steps to execute
     * @return true if this strategy can handle the steps, false otherwise
     */
    default boolean supports(List<TestStep> steps) {
        return steps != null && !steps.isEmpty();
    }
    
    /**
     * Get a description of what this strategy does
     * @return Strategy description
     */
    default String getDescription() {
        return "Executes test steps using " + getStrategyName() + " strategy";
    }
}