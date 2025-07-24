package runner.executor.alert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import runner.executor.ActionExecutor;
import runner.model.step.alert.DismissAlertActionStep;

import java.time.Duration;

public class DismissAlertActionExecutor extends ActionExecutor<DismissAlertActionStep> {
    @Override
    public void execute(WebDriver driver, DismissAlertActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        
        int timeout = step.getTimeout() != null ? step.getTimeout() : 10;
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be positive, got: " + timeout);
        }
        
        try {
            // Wait for alert to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
        } catch (NoAlertPresentException e) {
            throw new IllegalStateException("No alert present to dismiss", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to dismiss alert: " + e.getMessage(), e);
        }
    }
}
