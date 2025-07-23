package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.SendValueActionStep;

public class SendValueActionExecutor extends ActionExecutor<SendValueActionStep> {
    @Override
    public void execute(WebDriver driver, SendValueActionStep step) {
        find(driver, step.getTarget()).sendKeys(step.getValue());
    }
}
