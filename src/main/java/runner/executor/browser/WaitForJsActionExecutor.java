package runner.executor.browser;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.browser.WaitForJsActionStep;

public class WaitForJsActionExecutor extends ActionExecutor<WaitForJsActionStep> {
    @Override
    public void execute(WebDriver driver, WaitForJsActionStep step) {
        throw new NotImplementedException();
    }
}
