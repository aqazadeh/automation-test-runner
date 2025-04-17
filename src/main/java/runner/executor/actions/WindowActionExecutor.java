package runner.executor.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Executor for window management actions and browser navigation
 */
public class WindowActionExecutor extends ActionExecutorBase {
    
    public WindowActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "switchToWindow", "closeWindow", "getWindowHandles", 
                 "back", "forward", "refresh" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        switch (step.getAction()) {
            case "switchToWindow" -> {
                if (step.getWindowHandle() != null) {
                    // Switch by handle
                    driver.switchTo().window(step.getWindowHandle());
                } else if (step.getWindowTitleOrUrl() != null) {
                    // Switch by title or URL
                    boolean switched = false;
                    Set<String> handles = driver.getWindowHandles();
                    String currentHandle = driver.getWindowHandle();
                    
                    for (String handle : handles) {
                        driver.switchTo().window(handle);
                        if (driver.getTitle().contains(step.getWindowTitleOrUrl()) || 
                            driver.getCurrentUrl().contains(step.getWindowTitleOrUrl())) {
                            switched = true;
                            break;
                        }
                    }
                    
                    if (!switched) {
                        driver.switchTo().window(currentHandle);
                        throw new NoSuchWindowException("Window with specified title or URL not found: " + step.getWindowTitleOrUrl());
                    }
                } else {
                    // Switch to newly opened window
                    String currentHandle = driver.getWindowHandle();
                    Set<String> handles = driver.getWindowHandles();
                    handles.remove(currentHandle);
                    
                    if (handles.isEmpty()) {
                        throw new NoSuchWindowException("No new window found");
                    }
                    
                    driver.switchTo().window(handles.iterator().next());
                }
                ReportManager.logStep(Status.PASS, "Pencereye Geç",
                        "Belirtilen pencereye geçildi: " + driver.getTitle());
            }
            case "closeWindow" -> {
                driver.close();
                // Switch back to the first window
                if (!driver.getWindowHandles().isEmpty()) {
                    driver.switchTo().window(driver.getWindowHandles().iterator().next());
                }
                ReportManager.logStep(Status.PASS, "Pencereyi Kapat",
                        "Aktif pencere kapatıldı");
            }
            case "getWindowHandles" -> {
                Set<String> handles = driver.getWindowHandles();
                List<String> handlesList = new ArrayList<>(handles);
                ReportManager.logStep(Status.PASS, "Pencere Tanımlayıcılarını Al",
                        "Açık pencere sayısı: " + handles.size() + ", Tanımlayıcılar: " + handlesList);
            }
            case "back" -> {
                driver.navigate().back();
                ReportManager.logStep(Status.PASS, "Geri Git",
                        "Tarayıcı geçmişinde bir sayfa geri gidildi");
            }
            case "forward" -> {
                driver.navigate().forward();
                ReportManager.logStep(Status.PASS, "İleri Git",
                        "Tarayıcı geçmişinde bir sayfa ileri gidildi");
            }
            case "refresh" -> {
                driver.navigate().refresh();
                ReportManager.logStep(Status.PASS, "Sayfayı Yenile",
                        "Sayfa yenilendi");
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
