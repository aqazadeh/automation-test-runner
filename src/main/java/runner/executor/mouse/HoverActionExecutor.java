package runner.executor.mouse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.executor.ActionExecutor;
import runner.model.step.mouse.HoverActionStep;

public class HoverActionExecutor extends ActionExecutor<HoverActionStep> {
    @Override
    public void execute(WebDriver driver, HoverActionStep step) {
        Actions actions = new Actions(driver);
        WebElement element = find(driver, step.getTarget());
        actions.moveToElement(element).perform();
    }
}
