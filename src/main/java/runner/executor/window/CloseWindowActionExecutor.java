package runner.executor.window;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.window.CloseWindowActionStep;

/**
 * Executor for closing the current window or tab
 */
public class CloseWindowActionExecutor extends ActionExecutor<CloseWindowActionStep> {
    
    @Override
    public void execute(WebDriver driver, CloseWindowActionStep step) {
        driver.close();
        if (!driver.getWindowHandles().isEmpty()) {
            driver.switchTo().window(driver.getWindowHandles().iterator().next());
        }
    }
}