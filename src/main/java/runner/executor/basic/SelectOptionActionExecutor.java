package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import runner.executor.ActionExecutor;
import runner.model.step.basic.SelectOptionActionStep;

public class SelectOptionActionExecutor extends ActionExecutor<SelectOptionActionStep> {
    @Override
    public void execute(WebDriver driver, SelectOptionActionStep step) {
        WebElement element = find(driver, step.getTarget());
        new Select(element).selectByValue(step.getValue());
    }
}
