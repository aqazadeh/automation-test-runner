package runner.executor.window;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.window.RefreshWindowActionStep;

public class RefreshWindowActionExecutor extends ActionExecutor<RefreshWindowActionStep> {
    @Override
    public void execute(WebDriver driver, RefreshWindowActionStep step) {
        driver.navigate().refresh();
    }
}
