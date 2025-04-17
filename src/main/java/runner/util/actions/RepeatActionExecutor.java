package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for repeat/loop actions
 */
public class RepeatActionExecutor extends ActionExecutorBase {
    
    private final ActionExecutorFactory factory;
    
    public RepeatActionExecutor(WebDriver driver, ActionExecutorFactory factory) {
        super(driver);
        this.factory = factory;
    }
    
    @Override
    public boolean canExecute(String action) {
        return "repeat".equals(action);
    }
    
    @Override
    public void execute(TestStep step) {
        if ("repeat".equals(step.getAction()) && step.getSteps() != null) {
            ReportManager.logStep(Status.INFO, "Tekrar Başlangıcı",
                    step.getTimes() + " kez tekrarlanacak");
            
            for (int i = 0; i < step.getTimes(); i++) {
                ReportManager.logStep(Status.INFO, "Tekrar " + (i + 1), "");
                
                for (TestStep innerStep : step.getSteps()) {
                    factory.executeStep(innerStep);
                }
            }
            
            ReportManager.logStep(Status.PASS, "Tekrar Sonu", "");
        } else {
            throw new IllegalArgumentException("Invalid repeat step: " + step.getAction());
        }
    }
}
