package runner.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.model.Target;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WebDriverWaitUtil {
    
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final int DEFAULT_POLL_INTERVAL_MILLIS = 500;
    
    // Get default timeout from configuration
    private static int getDefaultTimeoutSeconds() {
        try {
            runner.config.TestConfiguration config = runner.config.TestConfiguration.getInstance();
            return config.getExplicitWaitSeconds();
        } catch (Exception e) {
            return DEFAULT_TIMEOUT_SECONDS;
        }
    }
    
    public enum WaitCondition {
        VISIBLE,
        INVISIBLE,
        PRESENT,
        CLICKABLE,
        SELECTED,
        TEXT_PRESENT,
        VALUE_PRESENT,
        ATTRIBUTE_CONTAINS,
        URL_CONTAINS,
        TITLE_CONTAINS,
        ALERT_PRESENT,
        FRAME_AVAILABLE
    }
    
    // Basic wait creation methods
    public static WebDriverWait createWait(WebDriver driver) {
        return createWait(driver, getDefaultTimeoutSeconds());
    }
    
    public static WebDriverWait createWait(WebDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
    
    public static WebDriverWait createWait(WebDriver driver, int timeoutSeconds, int pollIntervalMillis) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds), Duration.ofMillis(pollIntervalMillis));
    }
    
    // Wait for element conditions
    public static WebElement waitForElement(WebDriver driver, Target target, WaitCondition condition) {
        return waitForElement(driver, target, condition, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static WebElement waitForElement(WebDriver driver, Target target, WaitCondition condition, int timeoutSeconds) {
        By locator = TargetLocatorUtil.getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        
        return switch (condition) {
            case VISIBLE -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            case PRESENT -> wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            case CLICKABLE -> wait.until(ExpectedConditions.elementToBeClickable(locator));
            default -> throw new IllegalArgumentException("Unsupported wait condition for element: " + condition);
        };
    }
    
    // Wait for specific conditions
    public static boolean waitForInvisibility(WebDriver driver, Target target) {
        return waitForInvisibility(driver, target, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForInvisibility(WebDriver driver, Target target, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    // Wait for text conditions
    public static boolean waitForTextPresent(WebDriver driver, Target target, String text) {
        return waitForTextPresent(driver, target, text, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForTextPresent(WebDriver driver, Target target, String text, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    
    public static boolean waitForTextPresentInValue(WebDriver driver, Target target, String value) {
        return waitForTextPresentInValue(driver, target, value, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForTextPresentInValue(WebDriver driver, Target target, String value, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.textToBePresentInElementValue(locator, value));
    }
    
    // Wait for attribute conditions
    public static boolean waitForAttributeContains(WebDriver driver, Target target, String attribute, String value) {
        return waitForAttributeContains(driver, target, attribute, value, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForAttributeContains(WebDriver driver, Target target, String attribute, String value, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
    }
    
    public static boolean waitForAttributeToBe(WebDriver driver, Target target, String attribute, String value) {
        return waitForAttributeToBe(driver, target, attribute, value, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForAttributeToBe(WebDriver driver, Target target, String attribute, String value, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }
    
    // Wait for page conditions
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        return waitForUrlContains(driver, urlFragment, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }
    
    public static boolean waitForTitleContains(WebDriver driver, String title) {
        return waitForTitleContains(driver, title, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForTitleContains(WebDriver driver, String title, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.titleContains(title));
    }
    
    // Wait for alert
    public static Alert waitForAlert(WebDriver driver) {
        return waitForAlert(driver, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static Alert waitForAlert(WebDriver driver, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.alertIsPresent());
    }
    
    // Wait for frame availability
    public static WebDriver waitForFrameAndSwitchToIt(WebDriver driver, Target frameTarget) {
        return waitForFrameAndSwitchToIt(driver, frameTarget, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static WebDriver waitForFrameAndSwitchToIt(WebDriver driver, Target frameTarget, int timeoutSeconds) {
        By locator = getBy(frameTarget);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }
    
    // Wait for multiple elements
    public static List<WebElement> waitForMultipleElements(WebDriver driver, Target target) {
        return waitForMultipleElements(driver, target, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static List<WebElement> waitForMultipleElements(WebDriver driver, Target target, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
    
    // Wait for element count
    public static List<WebElement> waitForElementCount(WebDriver driver, Target target, int expectedCount) {
        return waitForElementCount(driver, target, expectedCount, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static List<WebElement> waitForElementCount(WebDriver driver, Target target, int expectedCount, int timeoutSeconds) {
        By locator = getBy(target);
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }
    
    // Custom wait condition
    public static <T> T waitForCustomCondition(WebDriver driver, Function<WebDriver, T> condition) {
        return waitForCustomCondition(driver, condition, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static <T> T waitForCustomCondition(WebDriver driver, Function<WebDriver, T> condition, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(condition);
    }
    
    // Wait for JavaScript condition
    public static Object waitForJavaScript(WebDriver driver, String script) {
        return waitForJavaScript(driver, script, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static Object waitForJavaScript(WebDriver driver, String script, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript(script));
    }
    
    public static boolean waitForJavaScriptCondition(WebDriver driver, String script) {
        return waitForJavaScriptCondition(driver, script, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static boolean waitForJavaScriptCondition(WebDriver driver, String script, int timeoutSeconds) {
        WebDriverWait wait = createWait(driver, timeoutSeconds);
        return wait.until(webDriver -> {
            Object result = ((JavascriptExecutor) webDriver).executeScript(script);
            return Boolean.TRUE.equals(result);
        });
    }
    
    // Helper method to convert Target to By
    private static By getBy(Target target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        return switch (target.getBy()) {
            case "id" -> By.id(target.getValue());
            case "name" -> By.name(target.getValue());
            case "css" -> By.cssSelector(target.getValue());
            case "xpath" -> By.xpath(target.getValue());
            case "className" -> By.className(target.getValue());
            case "tagName" -> By.tagName(target.getValue());
            case "linkText" -> By.linkText(target.getValue());
            case "partialLinkText" -> By.partialLinkText(target.getValue());
            default -> throw new IllegalArgumentException("Unknown locator type: " + target.getBy());
        };
    }
    
    // Polling wait - useful for conditions that might not trigger standard WebDriverWait
    public static boolean pollForCondition(Function<Void, Boolean> condition, int timeoutSeconds, int pollIntervalMillis) {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                if (Boolean.TRUE.equals(condition.apply(null))) {
                    return true;
                }
                Thread.sleep(pollIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted", e);
            } catch (Exception e) {
                // Continue polling on exceptions
            }
        }
        return false;
    }
}