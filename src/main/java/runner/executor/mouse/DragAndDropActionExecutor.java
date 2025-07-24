package runner.executor.mouse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.executor.ActionExecutor;
import runner.model.step.mouse.DragAndDropActionStep;

import static runner.util.TargetLocatorUtil.getBy;
import static runner.util.TargetLocatorUtil.validateTarget;

public class DragAndDropActionExecutor extends ActionExecutor<DragAndDropActionStep> {
    @Override
    public void execute(WebDriver driver, DragAndDropActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        validateTarget(step.getSource());
        validateTarget(step.getDestination());
        
        // Validate that source and destination are different
        if (step.getSource().equals(step.getDestination())) {
            throw new IllegalArgumentException("Source and destination targets cannot be the same");
        }
        
        Actions actions = new Actions(driver);
        WebElement source = driver.findElement(getBy(step.getSource()));
        WebElement target = driver.findElement(getBy(step.getDestination()));
        
        // Validate elements are interactable
        if (!source.isDisplayed() || !source.isEnabled()) {
            throw new IllegalStateException("Source element is not interactable (visible: " + source.isDisplayed() + ", enabled: " + source.isEnabled() + ")");
        }
        
        if (!target.isDisplayed()) {
            throw new IllegalStateException("Destination element is not visible");
        }
        
        actions.dragAndDrop(source, target).perform();
    }
}
