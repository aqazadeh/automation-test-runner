package runner.executor.condition;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.executor.ActionExecutor;
import runner.model.step.condition.ConditionActionStep;

import java.time.Duration;

public class ConditionActionExecutor extends ActionExecutor<ConditionActionStep> {
    public void execute(WebDriver driver, ConditionActionStep step) {
        By by = super.getBy(step.getTarget());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(step.getTimeout()));

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
                wait.until(ExpectedConditions.presenceOfElementLocated(by)); // veya başka bir uygun wait
                WebElement element = driver.findElement(by);
                yield element.isSelected();
            }
        };

        if (condition != step.getValue()) {
            throw new IllegalStateException("Expected " + step.getType() + " to be " + step.getValue() + " but was " + condition);
        }
    }
}
