package runner.util.actions;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import runner.manager.ReportManager;
import runner.model.TestStep;

import java.io.File;
import java.nio.file.Files;

/**
 * Executor for basic web interactions like navigation, clicking, typing, etc.
 */
public class BasicActionExecutor extends ActionExecutorBase {
    
    public BasicActionExecutor(WebDriver driver) {
        super(driver);
    }
    
    @Override
    public boolean canExecute(String action) {
        return switch (action) {
            case "navigate", "click", "type", "clear", "selectOption", 
                 "waitFor", "assertText", "screenshot" -> true;
            default -> false;
        };
    }
    
    @Override
    public void execute(TestStep step) {
        switch (step.getAction()) {
            case "navigate" -> {
                driver.get(step.getUrl());
                ReportManager.logStep(Status.PASS, "Sayfa Aç", "URL: " + step.getUrl());
            }
            case "click" -> {
                find(step).click();
                ReportManager.logStep(Status.PASS, "Tıkla", "Elemana tıklandı");
            }
            case "type" -> {
                find(step).sendKeys(step.getValue());
                ReportManager.logStep(Status.PASS, "Yazı Yaz", "Değer: " + step.getValue());
            }
            case "clear" -> {
                find(step).clear();
                ReportManager.logStep(Status.PASS, "Temizle", "Eleman temizlendi");
            }
            case "selectOption" -> {
                WebElement el = find(step);
                new Select(el).selectByVisibleText(step.getValue());
                ReportManager.logStep(Status.PASS, "Seçim Yap", "Seçilen değer: " + step.getValue());
            }
            case "waitFor" -> {
                getWait(step.getTimeout()).until(ExpectedConditions.visibilityOfElementLocated(getBy(step.getTarget())));
                ReportManager.logStep(Status.PASS, "Bekle",
                        "Eleman için " + (step.getTimeout() != null ? step.getTimeout() : 10) + " saniye beklendi");
            }
            case "assertText" -> {
                String actual = find(step).getText();
                if (!actual.contains(step.getValue())) {
                    ReportManager.logStep(Status.FAIL, "Metin Kontrolü",
                            "Beklenen: " + step.getValue() + ", Bulunan: " + actual);
                    ReportManager.attachScreenshot("AssertionFail");
                    throw new AssertionError("Expected text: " + step.getValue() + ", but found: " + actual);
                }
                ReportManager.logStep(Status.PASS, "Metin Kontrolü",
                        "Metin doğrulama başarılı: " + step.getValue());
            }
            case "screenshot" -> {
                try {
                    File src = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                    File dest = new File("screenshots/" + step.getValue());
                    dest.getParentFile().mkdirs();
                    Files.copy(src.toPath(), dest.toPath());
                    ReportManager.attachScreenshot(step.getValue());
                    ReportManager.logStep(Status.PASS, "Ekran Görüntüsü",
                            "Ekran görüntüsü alındı: " + step.getValue());
                } catch (Exception e) {
                    ReportManager.logStep(Status.FAIL, "Ekran Görüntüsü",
                            "Ekran görüntüsü alınamadı: " + e.getMessage());
                    throw new RuntimeException("Screenshot failure", e);
                }
            }
            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
        }
    }
}
