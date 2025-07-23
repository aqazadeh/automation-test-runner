package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.ClearActionStep;

public class ClearActionExecutor extends ActionExecutor<ClearActionStep> {
    @Override
    public void execute(WebDriver driver, ClearActionStep step) {
        find(driver, step.getTarget()).clear();
    }
}
