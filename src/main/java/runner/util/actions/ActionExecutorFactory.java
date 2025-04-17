package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.manager.ReportManager;
import runner.model.TestStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class that manages all action executors and routes execution requests
 * to the appropriate executor based on the action type.
 */
public class ActionExecutorFactory {
    
    private final List<ActionExecutorBase> executors = new ArrayList<>();
    private final WebDriver driver;
    private final JavascriptExecutor jsExecutor;
    
    public ActionExecutorFactory(WebDriver driver) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
        
        // Initialize all executors
        executors.add(new BasicActionExecutor(driver));
        executors.add(new MouseActionExecutor(driver));
        executors.add(new JavaScriptActionExecutor(driver));
        executors.add(new FrameActionExecutor(driver));
        executors.add(new WindowActionExecutor(driver));
        executors.add(new AlertActionExecutor(driver));
        executors.add(new KeyboardActionExecutor(driver));
        executors.add(new ElementAttributeActionExecutor(driver));
        executors.add(new WaitingActionExecutor(driver));
    }
    
    /**
     * Executes a test step by delegating to the appropriate executor
     * @param step The test step to execute
     */
    public void executeStep(TestStep step) {
        try {
            // Log step start
            ReportManager.logStep(Status.INFO, step.getAction(),
                    "Hedef: " + (step.getTarget() != null ? step.getTarget() : "N/A") + 
                    ", Değer: " + (step.getValue() != null ? step.getValue() : "N/A"));
            
            System.out.println("Action: " + step.getAction());
            System.out.println("Selector: " + (step.getTarget() != null ? step.getTarget() : "N/A"));
            System.out.println("Value: " + (step.getValue() != null ? step.getValue() : "N/A"));
            
            // Check condition
            if (step.getCondition() != null && !checkCondition(step.getCondition())) {
                ReportManager.logStep(Status.SKIP, "Atlanan adım",
                        "Koşul sağlanmadığı için adım atlandı: " + step.getAction());
                return;
            }
            
            // Handle repeat steps
            if ("repeat".equals(step.getAction()) && step.getSteps() != null) {
                ReportManager.logStep(Status.INFO, "Tekrar Başlangıcı",
                        step.getTimes() + " kez tekrarlanacak");
                for (int i = 0; i < step.getTimes(); i++) {
                    ReportManager.logStep(Status.INFO, "Tekrar " + (i + 1), "");
                    for (TestStep inner : step.getSteps()) {
                        executeStep(inner);
                    }
                }
                ReportManager.logStep(Status.PASS, "Tekrar Sonu", "");
                return;
            }
            
            // Find appropriate executor for the action
            for (ActionExecutorBase executor : executors) {
                if (executor.canExecute(step.getAction())) {
                    executor.execute(step);
                    return;
                }
            }
            
            // If we get here, no executor was found for the action
            ReportManager.logStep(Status.FAIL, "Bilinmeyen Aksiyon",
                    "Bilinmeyen aksiyon: " + step.getAction());
            throw new IllegalArgumentException("Unknown action: " + step.getAction());
            
        } catch (Exception e) {
            // Take screenshot on error
            ReportManager.attachScreenshot("Error_" + step.getAction());
            
            // Log error
            ReportManager.logStep(Status.FAIL, "Hata",
                    "Aksiyon: " + step.getAction() + " -> " + e.getMessage());
            
            System.err.println("[FAILED] Action: " + step.getAction() + " -> " + e.getMessage());
            
            if (Boolean.FALSE.equals(step.getContinueOnFailure())) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * Checks if a condition is true
     * @param condition The condition to check
     * @return true if the condition is met, false otherwise
     */
    private boolean checkCondition(TestStep.Condition condition) {
        try {
            if (condition.jsCondition != null) {
                // JavaScript condition
                return (Boolean) jsExecutor.executeScript(condition.jsCondition);
            }
            
            if (condition.target != null) {
                WebElement element = null;
                switch (condition.type) {
                    case "visible":
                        element = driver.findElement(getBy(condition.target));
                        return element.isDisplayed();
                    case "present":
                        element = driver.findElement(getBy(condition.target));
                        return element != null;
                    case "clickable":
                        element = driver.findElement(getBy(condition.target));
                        return element.isDisplayed() && element.isEnabled();
                    case "invisible":
                        try {
                            element = driver.findElement(getBy(condition.target));
                            return !element.isDisplayed();
                        } catch (org.openqa.selenium.NoSuchElementException e) {
                            return true; // Not found means invisible
                        }
                    case "selected":
                        element = driver.findElement(getBy(condition.target));
                        return element.isSelected();
                    case "titleContains":
                        return driver.getTitle().contains(condition.target.value);
                    case "urlContains":
                        return driver.getCurrentUrl().contains(condition.target.value);
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Converts a target specification to a Selenium By locator
     * @param target The target specification
     * @return The corresponding By locator
     */
    private org.openqa.selenium.By getBy(TestStep.Target target) {
        return switch (target.by) {
            case "id" -> org.openqa.selenium.By.id(target.value);
            case "name" -> org.openqa.selenium.By.name(target.value);
            case "css" -> org.openqa.selenium.By.cssSelector(target.value);
            case "xpath" -> org.openqa.selenium.By.xpath(target.value);
            case "className" -> org.openqa.selenium.By.className(target.value);
            case "tagName" -> org.openqa.selenium.By.tagName(target.value);
            case "linkText" -> org.openqa.selenium.By.linkText(target.value);
            case "partialLinkText" -> org.openqa.selenium.By.partialLinkText(target.value);
            default -> throw new IllegalArgumentException("Unknown locator: " + target.by);
        };
    }
}
