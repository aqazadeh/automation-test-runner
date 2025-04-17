package runner.executor.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.model.TestStep;

import java.time.Duration;

/**
 * Base abstract class for all action executors.
 * Provides common functionality used by specific action executors.
 */
public abstract class ActionExecutorBase {
    
    protected final WebDriver driver;
    protected final JavascriptExecutor jsExecutor;
    
    public ActionExecutorBase(WebDriver driver) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    /**
     * Find a web element based on the target in the test step
     * @param step The test step containing the target
     * @return The found web element
     */
    protected WebElement find(TestStep step) {
        return driver.findElement(getBy(step.getTarget()));
    }
    
    /**
     * Convert a target specification to a Selenium By locator
     * @param target The target specification
     * @return The corresponding By locator
     */
    protected By getBy(TestStep.Target target) {
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
    
    /**
     * Create a WebDriverWait instance with the specified timeout
     * @param timeout The timeout in seconds
     * @return A WebDriverWait instance
     */
    protected WebDriverWait getWait(Integer timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout != null ? timeout : 10));
    }
    
    /**
     * Check if this executor can handle the specified action
     * @param action The action to check
     * @return true if this executor can handle the action, false otherwise
     */
    public abstract boolean canExecute(String action);
    
    /**
     * Execute the specified test step
     * @param step The test step to execute
     */
    public abstract void execute(TestStep step);
}
