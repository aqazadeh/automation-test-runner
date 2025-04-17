package runner.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import runner.model.TestStep;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

public class ActionExecutor {

    private final WebDriver driver;

    public ActionExecutor(WebDriver driver) {
        this.driver = driver;
    }

    public void execute(TestStep step) {
        try {
            System.out.println("Action: " + step.getAction());
            System.out.println("Selector: " + step.getTarget());
            System.out.println("Value: " + step.getValue());
            if (step.condition != null && !checkCondition(step.condition)) return;

            switch (step.action) {
                case "navigate" -> driver.get(step.url);
                case "click" -> find(step).click();
                case "type" -> find(step).sendKeys(step.value);
                case "clear" -> find(step).clear();
                case "selectOption" -> {
                    WebElement el = find(step);
                    new Select(el).selectByVisibleText(step.value);
                }
                case "waitFor" -> waitFor(step);
                case "assertText" -> {
                    String actual = find(step).getText();
                    if (!actual.contains(step.value)) {
                        throw new AssertionError("Expected text: " + step.value + ", but found: " + actual);
                    }
                }
                case "repeat" -> {
                    for (int i = 0; i < step.times; i++) {
                        for (TestStep inner : step.steps) {
                            execute(inner);
                        }
                    }
                }
                case "screenshot" -> {
                    TakesScreenshot ts = (TakesScreenshot) driver;
                    File src = ts.getScreenshotAs(OutputType.FILE);
                    File dest = new File("screenshots/" + step.value);
                    dest.getParentFile().mkdirs();
                    Files.copy(src.toPath(), dest.toPath());
                }
                default -> throw new IllegalArgumentException("Unknown action: " + step.action);
            }
        } catch (Exception e) {
            System.err.println("[FAILED] Action: " + step.action + " -> " + e.getMessage());
            if (Boolean.FALSE.equals(step.continueOnFailure)) {
                throw new RuntimeException(e);
            }
        }
    }

    private WebElement find(TestStep step) {
        return driver.findElement(getBy(step.target));
    }

    private By getBy(TestStep.Target target) {
        return switch (target.by) {
            case "id" -> By.id(target.value);
            case "name" -> By.name(target.value);
            case "css" -> By.cssSelector(target.value);
            case "xpath" -> By.xpath(target.value);
            default -> throw new IllegalArgumentException("Unknown locator: " + target.by);
        };
    }

    private void waitFor(TestStep step) {
        new WebDriverWait(driver, Duration.ofSeconds(step.timeout != null ? step.timeout : 10))
                .until(ExpectedConditions.visibilityOfElementLocated(getBy(step.target)));
    }

    private boolean checkCondition(TestStep.Condition condition) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            switch (condition.type) {
                case "visible" -> wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(condition.target)));
                case "present" -> wait.until(ExpectedConditions.presenceOfElementLocated(getBy(condition.target)));
                default -> throw new IllegalArgumentException("Unsupported condition: " + condition.type);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
