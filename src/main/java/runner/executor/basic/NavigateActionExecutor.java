package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.NavigateActionStep;

public class NavigateActionExecutor extends ActionExecutor<NavigateActionStep> {

    @Override
    public void execute(WebDriver driver, NavigateActionStep step) {
        driver.get(step.getUrl());
    }
}
