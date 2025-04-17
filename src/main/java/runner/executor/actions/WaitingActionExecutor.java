package runner.executor.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for waiting and timing operations
 */
public class WaitingActionExecutor extends ActionExecutorBase {
    
    public WaitingActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "sleep", "waitForElementToBeClickable", "waitForElementToBeInvisible" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        switch (step.getAction()) {
            case "sleep" -> {
                try {
                    int sleepTime = step.getTimeout() != null ? step.getTimeout() : 1;
                    Thread.sleep(sleepTime * 1000L);
                    ReportManager.logStep(Status.PASS, "Bekle",
                            sleepTime + " saniye beklendi");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ReportManager.logStep(Status.FAIL, "Bekle",
                            "Bekleme sırasında hata oluştu: " + e.getMessage());
                    throw new RuntimeException("Sleep interrupted", e);
                }
            }
            case "waitForElementToBeClickable" -> {
                getWait(step.getTimeout()).until(ExpectedConditions.elementToBeClickable(getBy(step.getTarget())));
                ReportManager.logStep(Status.PASS, "Tıklanabilir Olana Kadar Bekle",
                        "Eleman tıklanabilir olana kadar beklendi");
            }
            case "waitForElementToBeInvisible" -> {
                getWait(step.getTimeout()).until(ExpectedConditions.invisibilityOfElementLocated(getBy(step.getTarget())));
                ReportManager.logStep(Status.PASS, "Görünmez Olana Kadar Bekle",
                        "Eleman görünmez olana kadar beklendi");
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
