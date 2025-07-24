package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import runner.executor.ActionExecutor;
import runner.model.step.basic.SelectOptionActionStep;

import static runner.util.TargetLocatorUtil.validateTarget;

public class SelectOptionActionExecutor extends ActionExecutor<SelectOptionActionStep> {
    @Override
    public void execute(WebDriver driver, SelectOptionActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        validateTarget(step.getTarget());
        validateString(step.getValue(), "Select option value");
        
        WebElement element = find(driver, step.getTarget());
        
        // Validate that element is a select element
        String tagName = element.getTagName().toLowerCase();
        if (!"select".equals(tagName)) {
            throw new IllegalArgumentException("Element must be a <select> element, but found: <" + tagName + ">");
        }
        
        Select select = new Select(element);
        try {
            select.selectByValue(step.getValue());
        } catch (Exception e) {
            // Try alternative selection methods if value selection fails
            try {
                select.selectByVisibleText(step.getValue());
            } catch (Exception e2) {
                throw new IllegalArgumentException("Could not select option '" + step.getValue() + "' by value or visible text. Available options: " + 
                    select.getOptions().stream().map(opt -> opt.getAttribute("value") + "(" + opt.getText() + ")").toList(), e);
            }
        }
    }
}
