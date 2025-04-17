package runner.executor.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for keyboard interactions
 */
public class KeyboardActionExecutor extends ActionExecutorBase {
    
    public KeyboardActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return "pressKey".equals(action);
    }
    
    @Override
    public void execute(TestStep step) {
        if ("pressKey".equals(step.getAction())) {
            if (step.getTarget() != null) {
                // Send key to specified element
                WebElement element = find(step);
                if (step.getKey().startsWith("Keys.")) {
                    String keyName = step.getKey().substring(5); // Remove "Keys." prefix
                    Keys key = Keys.valueOf(keyName);
                    element.sendKeys(key);
                } else {
                    element.sendKeys(step.getKey());
                }
            } else {
                // Send key to active element
                Actions actions = new Actions(driver);
                if (step.getKey().startsWith("Keys.")) {
                    String keyName = step.getKey().substring(5); // Remove "Keys." prefix
                    Keys key = Keys.valueOf(keyName);
                    actions.sendKeys(key).perform();
                } else {
                    actions.sendKeys(step.getKey()).perform();
                }
            }
            ReportManager.logStep(Status.PASS, "Tuşa Bas",
                    "Tuş basıldı: " + step.getKey());
        } else {
            throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
