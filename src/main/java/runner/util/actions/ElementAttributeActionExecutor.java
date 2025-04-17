package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for element attribute and property operations
 */
public class ElementAttributeActionExecutor extends ActionExecutorBase {
    
    public ElementAttributeActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "getAttribute", "getCssValue", "isElementDisplayed", 
                 "isElementEnabled", "isElementSelected" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        WebElement element = find(step);
        
        switch (step.getAction()) {
            case "getAttribute" -> {
                String attributeValue = element.getAttribute(step.getAttribute());
                ReportManager.logStep(Status.PASS, "Özellik Değeri Al",
                        "Özellik: " + step.getAttribute() + ", Değer: " + attributeValue);
            }
            case "getCssValue" -> {
                String cssValue = element.getCssValue(step.getAttribute());
                ReportManager.logStep(Status.PASS, "CSS Değeri Al",
                        "CSS Özelliği: " + step.getAttribute() + ", Değer: " + cssValue);
            }
            case "isElementDisplayed" -> {
                boolean isDisplayed = element.isDisplayed();
                ReportManager.logStep(Status.PASS, "Eleman Görünür mü?",
                        "Görünürlük durumu: " + isDisplayed);
            }
            case "isElementEnabled" -> {
                boolean isEnabled = element.isEnabled();
                ReportManager.logStep(Status.PASS, "Eleman Etkin mi?",
                        "Etkinlik durumu: " + isEnabled);
            }
            case "isElementSelected" -> {
                boolean isSelected = element.isSelected();
                ReportManager.logStep(Status.PASS, "Eleman Seçili mi?",
                        "Seçim durumu: " + isSelected);
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
