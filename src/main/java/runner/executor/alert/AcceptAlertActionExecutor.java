package runner.executor.alert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.alert.AcceptAlertActionStep;

public class AcceptAlertActionExecutor extends ActionExecutor<AcceptAlertActionStep> {
    @Override
    public void execute(WebDriver driver, AcceptAlertActionStep step) {
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }
}
