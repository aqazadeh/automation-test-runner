package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for mouse-related actions like hover, double-click, and drag-and-drop
 */
public class MouseActionExecutor extends ActionExecutorBase {
    
    public MouseActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "hover", "doubleClick", "rightClick", "dragAndDrop" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        Actions actions = new Actions(driver);
        
        switch (step.getAction()) {
            case "hover" -> {
                WebElement element = find(step);
                actions.moveToElement(element).perform();
                ReportManager.logStep(Status.PASS, "Fare Üzerinde Gezdirme",
                        "Fare elementin üzerine getirildi");
            }
            case "doubleClick" -> {
                WebElement element = find(step);
                actions.doubleClick(element).perform();
                ReportManager.logStep(Status.PASS, "Çift Tıklama",
                        "Elemana çift tıklandı");
            }
            case "rightClick" -> {
                WebElement element = find(step);
                actions.contextClick(element).perform();
                ReportManager.logStep(Status.PASS, "Sağ Tıklama",
                        "Elemana sağ tıklandı");
            }
            case "dragAndDrop" -> {
                WebElement source = driver.findElement(getBy(step.getSourceTarget()));
                WebElement target = driver.findElement(getBy(step.getDestinationTarget()));
                actions.dragAndDrop(source, target).perform();
                ReportManager.logStep(Status.PASS, "Sürükle Bırak",
                        "Eleman sürüklendi ve bırakıldı");
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
