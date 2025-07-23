package runner.executor.alert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.alert.SendValueToAlertActionStep;

public class SendValueToAlertActionExecutor extends ActionExecutor<SendValueToAlertActionStep> {
    @Override
    public void execute(WebDriver driver, SendValueToAlertActionStep step) {
        Alert alert = driver.switchTo().alert();
        alert.sendKeys(step.getValue());
    }
}
