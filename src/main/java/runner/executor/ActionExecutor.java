package runner.executor;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.model.Target;
import runner.util.ElementFinder;
import runner.util.TargetLocatorUtil;
import runner.util.WebDriverWaitUtil;

public abstract class ActionExecutor<T>{

    protected WebElement find(WebDriver driver, Target target) {
        TargetLocatorUtil.validateTarget(target);
        return driver.findElement(TargetLocatorUtil.getBy(target));
    }
    
    // Enhanced element finding with utility classes
    protected WebElement findWithWait(WebDriver driver, Target target) {
        TargetLocatorUtil.validateTarget(target);
        return WebDriverWaitUtil.waitForElement(driver, target, WebDriverWaitUtil.WaitCondition.PRESENT);
    }
    
    protected WebElement findWithWait(WebDriver driver, Target target, int timeoutSeconds) {
        TargetLocatorUtil.validateTarget(target);
        return WebDriverWaitUtil.waitForElement(driver, target, WebDriverWaitUtil.WaitCondition.PRESENT, timeoutSeconds);
    }
    
    protected WebElement findClickableElement(WebDriver driver, Target target) {
        TargetLocatorUtil.validateTarget(target);
        return WebDriverWaitUtil.waitForElement(driver, target, WebDriverWaitUtil.WaitCondition.CLICKABLE);
    }
    
    protected ElementFinder getElementFinder(WebDriver driver) {
        return ElementFinder.using(driver);
    }

    
    // Common validation methods
    protected void validateWebDriver(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
    }
    
    protected void validateStep(T step) {
        if (step == null) {
            throw new IllegalArgumentException("Step cannot be null");
        }
    }
    
    
    protected void validateString(String value, String parameterName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " cannot be null or empty");
        }
    }
    
    protected void validatePositiveInteger(Integer value, String parameterName) {
        if (value == null) {
            throw new IllegalArgumentException(parameterName + " cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive, got: " + value);
        }
    }
    
    protected void validateNotNull(Object value, String parameterName) {
        if (value == null) {
            throw new IllegalArgumentException(parameterName + " cannot be null");
        }
    }

    public abstract void execute(WebDriver driver, T step);
}
