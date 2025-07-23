package runner.executor.browser;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.browser.ExecuteScriptActionStep;

public class ExecuteScriptActionExecutor extends ActionExecutor<ExecuteScriptActionStep> {
    @Override
    public void execute(WebDriver driver, ExecuteScriptActionStep step) {
        throw new NotImplementedException();
    }
}
