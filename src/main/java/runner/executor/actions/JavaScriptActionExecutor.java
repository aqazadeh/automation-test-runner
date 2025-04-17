package runner.executor.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.manager.ReportManager;
import runner.model.TestStep;

/**
 * Executor for JavaScript-related actions like executing scripts and scrolling
 */
public class JavaScriptActionExecutor extends ActionExecutorBase {
    
    public JavaScriptActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "executeJS", "scrollTo", "waitForJS" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        switch (step.getAction()) {
            case "executeJS" -> {
                Object result;
                if (step.getTarget() != null) {
                    // Execute JavaScript with element
                    WebElement element = find(step);
                    result = jsExecutor.executeScript(step.getScript(), element);
                } else {
                    // Execute JavaScript without element
                    result = jsExecutor.executeScript(step.getScript());
                }
                ReportManager.logStep(Status.PASS, "JavaScript Çalıştır",
                        "Script: " + step.getScript() + (result != null ? ", Sonuç: " + result : ""));
            }
            case "scrollTo" -> {
                if (step.getTarget() != null) {
                    // Scroll to element
                    WebElement element = find(step);
                    jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                    ReportManager.logStep(Status.PASS, "Sayfayı Kaydır",
                            "Sayfa belirtilen elemana kaydırıldı");
                } else {
                    // Scroll to position
                    jsExecutor.executeScript("window.scrollTo({left: 0, top: " + step.getValue() + ", behavior: 'smooth'});");
                    ReportManager.logStep(Status.PASS, "Sayfayı Kaydır",
                            "Sayfa belirtilen pozisyona kaydırıldı: " + step.getValue());
                }
            }
            case "waitForJS" -> {
                getWait(step.getTimeout()).until((WebDriver d) -> {
                    return (Boolean) jsExecutor.executeScript(step.getCondition().jsCondition);
                });
                ReportManager.logStep(Status.PASS, "JS için Bekle",
                        "JavaScript koşulu sağlandı: " + step.getCondition().jsCondition);
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
