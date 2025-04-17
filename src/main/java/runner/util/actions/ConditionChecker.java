package runner.util.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.model.TestStep;

import java.time.Duration;

/**
 * Utility class for checking conditions in test steps
 */
public class ConditionChecker {
    
    private final WebDriver driver;
    private final JavascriptExecutor jsExecutor;
    
    public ConditionChecker(WebDriver driver, JavascriptExecutor jsExecutor) {
        this.driver = driver;
        this.jsExecutor = jsExecutor;
    }
    
    /**
     * Checks if a condition is true
     * @param condition The condition to check
     * @return true if the condition is met, false otherwise
     */
    public boolean checkCondition(TestStep.Condition condition) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            
            if (condition.jsCondition != null) {
                // JavaScript condition check
                return (Boolean) jsExecutor.executeScript(condition.jsCondition);
            }
            
            switch (condition.type) {
                case "visible":
                    return driver.findElement(getBy(condition.target)).isDisplayed();
                case "present":
                    try {
                        return driver.findElement(getBy(condition.target)) != null;
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        return false;
                    }
                case "clickable":
                    WebElement element = driver.findElement(getBy(condition.target));
                    return element.isDisplayed() && element.isEnabled();
                case "invisible":
                    try {
                        element = driver.findElement(getBy(condition.target));
                        return !element.isDisplayed();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        return true; // Not found means invisible
                    }
                case "selected":
                    return driver.findElement(getBy(condition.target)).isSelected();
                case "titleContains":
                    return driver.getTitle().contains(condition.target.value);
                case "urlContains":
                    return driver.getCurrentUrl().contains(condition.target.value);
                default:
                    throw new IllegalArgumentException("Unsupported condition: " + condition.type);
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Convert a target specification to a Selenium By locator
     * @param target The target specification
     * @return The corresponding By locator
     */
    private By getBy(TestStep.Target target) {
        return switch (target.by) {
            case "id" -> By.id(target.value);
            case "name" -> By.name(target.value);
            case "css" -> By.cssSelector(target.value);
            case "xpath" -> By.xpath(target.value);
            case "className" -> By.className(target.value);
            case "tagName" -> By.tagName(target.value);
            case "linkText" -> By.linkText(target.value);
            case "partialLinkText" -> By.partialLinkText(target.value);
            default -> throw new IllegalArgumentException("Unknown locator: " + target.by);
        };
    }
}
