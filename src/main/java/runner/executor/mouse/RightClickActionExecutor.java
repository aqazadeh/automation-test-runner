package runner.executor.mouse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.executor.ActionExecutor;
import runner.model.step.mouse.RightClickActionStep;

public class RightClickActionExecutor extends ActionExecutor<RightClickActionStep> {
    @Override
    public void execute(WebDriver driver, RightClickActionStep step) {
        Actions actions = new Actions(driver);
        WebElement element = find(driver, step.getTarget());
        actions.contextClick(element).perform();
    }
}
