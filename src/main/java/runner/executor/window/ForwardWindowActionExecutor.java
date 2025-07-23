package runner.executor.window;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.window.BackWindowActionStep;

public class ForwardWindowActionExecutor extends ActionExecutor<BackWindowActionStep> {
    @Override
    public void execute(WebDriver driver, BackWindowActionStep step) {
        driver.navigate().forward();
    }
}
