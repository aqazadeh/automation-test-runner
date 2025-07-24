package runner.executor.browser;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.executor.ActionExecutor;
import runner.model.step.browser.ScrollToActionStep;

import static runner.util.TargetLocatorUtil.validateTarget;

public class ScrollToActionExecutor extends ActionExecutor<ScrollToActionStep> {
    @Override
    public void execute(WebDriver driver, ScrollToActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalStateException("WebDriver does not support JavaScript execution. Driver type: " + driver.getClass().getSimpleName());
        }
        
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        
        // Validate that either target or coordinates are provided
        boolean hasTarget = step.getTarget() != null;
        boolean hasCoordinates = step.getX() != null && step.getY() != null;
        
        if (!hasTarget && !hasCoordinates) {
            throw new IllegalArgumentException("Either target element or coordinates (x, y) must be provided");
        }
        
        if (hasTarget && hasCoordinates) {
            throw new IllegalArgumentException("Cannot specify both target element and coordinates. Choose one.");
        }
        
        try {
            if (hasTarget) {
                validateTarget(step.getTarget());
                WebElement element = find(driver, step.getTarget());
                jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            } else {
                // Validate coordinates
                if (step.getX() < 0 || step.getY() < 0) {
                    throw new IllegalArgumentException("Coordinates must be non-negative. X: " + step.getX() + ", Y: " + step.getY());
                }
                jsExecutor.executeScript("window.scrollTo({left: arguments[0], top: arguments[1], behavior: 'smooth'});", step.getX(), step.getY());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to scroll: " + e.getMessage(), e);
        }
    }
}
