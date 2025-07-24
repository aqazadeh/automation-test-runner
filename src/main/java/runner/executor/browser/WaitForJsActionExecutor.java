package runner.executor.browser;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.executor.ActionExecutor;
import runner.model.step.browser.WaitForJsActionStep;

import java.time.Duration;

public class WaitForJsActionExecutor extends ActionExecutor<WaitForJsActionStep> {
    @Override
    public void execute(WebDriver driver, WaitForJsActionStep step) {
        if (step.getScript() == null || step.getScript().trim().isEmpty()) {
            throw new IllegalArgumentException("Script cannot be null or empty");
        }
        
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalStateException("WebDriver does not support JavaScript execution");
        }
        
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(step.getTimeout()));
        
        try {
            wait.until(webDriver -> {
                Object result;
                if (step.getArguments() != null && step.getArguments().length > 0) {
                    result = jsExecutor.executeScript(step.getScript(), step.getArguments());
                } else {
                    result = jsExecutor.executeScript(step.getScript());
                }
                
                if (result instanceof Boolean) {
                    return (Boolean) result;
                } else if (result != null) {
                    return true;
                } else {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException("JavaScript condition did not become true within " + step.getTimeout() + " seconds", e);
        }
    }
}
