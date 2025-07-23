package runner.executor.mouse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.executor.ActionExecutor;
import runner.model.step.mouse.DragAndDropActionStep;

public class DragAndDropActionExecutor extends ActionExecutor<DragAndDropActionStep> {
    @Override
    public void execute(WebDriver driver, DragAndDropActionStep step) {
        Actions actions = new Actions(driver);
        WebElement source = driver.findElement(getBy(step.getSource()));
        WebElement target = driver.findElement(getBy(step.getDestination()));
        actions.dragAndDrop(source, target).perform();
    }
}
