package runner.executor.browser;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.browser.ScrollToActionStep;

public class ScrollToActionExecutor extends ActionExecutor<ScrollToActionStep> {
    @Override
    public void execute(WebDriver driver, ScrollToActionStep step) {
        throw new NotImplementedException();
    }
}
