package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.SendValueActionStep;

import static runner.util.TargetLocatorUtil.validateTarget;

public class SendValueActionExecutor extends ActionExecutor<SendValueActionStep> {
    @Override
    public void execute(WebDriver driver, SendValueActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        validateTarget(step.getTarget());
        validateString(step.getValue(), "Value");
        
        find(driver, step.getTarget()).sendKeys(step.getValue());
    }
}
