package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for alert dialog interactions
 */
public class AlertActionExecutor extends ActionExecutorBase {
    
    public AlertActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return "handleAlert".equals(action);
    }
    
    @Override
    public void execute(TestStep step) {
        if ("handleAlert".equals(step.getAction())) {
            Alert alert = driver.switchTo().alert();
            switch (step.getAlertAction()) {
                case "accept" -> {
                    alert.accept();
                    ReportManager.logStep(Status.PASS, "Alert'ı Kabul Et",
                            "Alert diyaloğu kabul edildi");
                }
                case "dismiss" -> {
                    alert.dismiss();
                    ReportManager.logStep(Status.PASS, "Alert'ı Reddet",
                            "Alert diyaloğu reddedildi");
                }
                case "getText" -> {
                    String alertText = alert.getText();
                    ReportManager.logStep(Status.PASS, "Alert Metnini Al",
                            "Alert metni: " + alertText);
                }
                case "sendKeys" -> {
                    alert.sendKeys(step.getValue());
                    ReportManager.logStep(Status.PASS, "Alert'a Metin Gönder",
                            "Alert'a metin gönderildi: " + step.getValue());
                }
                default -> throw new IllegalArgumentException("Invalid alert action: " + step.getAlertAction());
            }
        } else {
            throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
