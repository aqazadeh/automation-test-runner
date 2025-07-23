package runner.executor.mouse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.executor.ActionExecutor;
import runner.model.step.mouse.DoubleClickActionStep;

public class DoubleClickActionExecutor extends ActionExecutor<DoubleClickActionStep> {
    @Override
    public void execute(WebDriver driver, DoubleClickActionStep step) {
        Actions actions = new Actions(driver);
        WebElement element = find(driver, step.getTarget());
        actions.doubleClick(element).perform();
    }
}
