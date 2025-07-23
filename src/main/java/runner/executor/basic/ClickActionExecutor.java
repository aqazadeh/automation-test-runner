package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.ClickActionStep;

public class ClickActionExecutor extends ActionExecutor<ClickActionStep> {
    @Override
    public void execute(WebDriver driver, ClickActionStep step) {
        find(driver, step.getTarget()).click();
    }
}
