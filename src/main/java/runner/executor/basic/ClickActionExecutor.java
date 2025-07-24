package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.ClickActionStep;

import static runner.util.TargetLocatorUtil.validateTarget;

public class ClickActionExecutor extends ActionExecutor<ClickActionStep> {
    @Override
    public void execute(WebDriver driver, ClickActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        validateTarget(step.getTarget());
        
        find(driver, step.getTarget()).click();
    }
}
