package runner.executor.condition;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.executor.ActionExecutor;
import runner.model.step.condition.ConditionActionStep;

import java.time.Duration;

import static runner.util.TargetLocatorUtil.getBy;

public class ConditionActionExecutor extends ActionExecutor<ConditionActionStep> {
    @Override
    public void execute(WebDriver driver, ConditionActionStep step) {
        // Null checks and validation
        if (step.getTarget() == null) {
            throw new IllegalArgumentException("Target cannot be null for condition action");
        }
        
        if (step.getType() == null) {
            throw new IllegalArgumentException("Condition type cannot be null");
        }
        
        if (step.getValue() == null) {
            throw new IllegalArgumentException("Expected value cannot be null for condition action");
        }
        
        int timeout = step.getTimeout() != null ? step.getTimeout() : 10;
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be positive, got: " + timeout);
        }
        
        By by = getBy(step.getTarget());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        boolean condition = switch (step.getType()) {
            case VISIBLE -> {
                wait.until(ExpectedConditions.visibilityOfElementLocated(by));
                WebElement element = driver.findElement(by);
                yield element.isDisplayed();
            }
            case PRESENT -> {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                WebElement element = driver.findElement(by);
                yield element != null;
            }
            case CLICKABLE -> {
                wait.until(ExpectedConditions.elementToBeClickable(by));
                WebElement element = driver.findElement(by);
                yield element.isDisplayed() && element.isEnabled();
            }
            case SELECTED -> {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                WebElement element = driver.findElement(by);
                yield element.isSelected();
            }
        };

        // Safe comparison with null check
        boolean expectedValue = Boolean.TRUE.equals(step.getValue());
        if (condition != expectedValue) {
            throw new IllegalStateException("Expected " + step.getType() + " to be " + expectedValue + " but was " + condition);
        }
    }
}
