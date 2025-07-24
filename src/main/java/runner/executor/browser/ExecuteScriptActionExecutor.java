package runner.executor.browser;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.browser.ExecuteScriptActionStep;

public class ExecuteScriptActionExecutor extends ActionExecutor<ExecuteScriptActionStep> {
    @Override
    public void execute(WebDriver driver, ExecuteScriptActionStep step) {
        // Input validation using base class methods
        validateWebDriver(driver);
        validateStep(step);
        validateString(step.getScript(), "JavaScript script");
        
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalStateException("WebDriver does not support JavaScript execution. Driver type: " + driver.getClass().getSimpleName());
        }
        
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        
        try {
            if (step.getArguments() != null && step.getArguments().length > 0) {
                jsExecutor.executeScript(step.getScript(), step.getArguments());
            } else {
                jsExecutor.executeScript(step.getScript());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute JavaScript: " + e.getMessage() + ". Script: " + step.getScript(), e);
        }
    }
}
