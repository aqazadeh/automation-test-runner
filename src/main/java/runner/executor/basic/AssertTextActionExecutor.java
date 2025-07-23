package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.manager.ReportManager;
import runner.model.step.basic.AssertTextActionStep;

public class AssertTextActionExecutor extends ActionExecutor<AssertTextActionStep> {
    @Override
    public void execute(WebDriver driver, AssertTextActionStep step) {
        String actual = find(driver, step.getTarget()).getText();
        if (!actual.contains(step.getValue())) {
            ReportManager.attachScreenshot("AssertionFail");
            throw new AssertionError("Expected text: " + step.getValue() + ", but found: " + actual);
        }
    }
}
