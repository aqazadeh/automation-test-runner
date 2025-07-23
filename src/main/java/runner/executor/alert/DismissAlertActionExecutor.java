package runner.executor.alert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.alert.DismissAlertActionStep;

public class DismissAlertActionExecutor extends ActionExecutor<DismissAlertActionStep> {
    @Override
    public void execute(WebDriver driver, DismissAlertActionStep step) {
        Alert alert = driver.switchTo().alert();
        alert.dismiss();
    }
}
