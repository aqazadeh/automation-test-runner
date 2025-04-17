package runner;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;
import runner.executor.ActionExecutor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        // Raporlama başlat
        String scenarioPath = "scenarios/full-selenium-demo.json";
        Path path = Paths.get(scenarioPath);
        String testName = path.getFileName().toString().replace(".json", "");

        ReportManager.initReports("Test Suite - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        ReportManager.startTest("Senaryo: " + testName);

        try {
            ObjectMapper mapper = new ObjectMapper();
            File scenarioFile = new File(scenarioPath);

            // 1. Dosya varlığını kontrol et
            if (scenarioFile.exists()) {
                ReportManager.log(Status.INFO, "Senaryo dosyası bulundu: " + scenarioPath);
            } else {
                ReportManager.log(Status.FAIL, "Senaryo dosyası bulunamadı: " + scenarioPath);
                throw new RuntimeException("Senaryo dosyası bulunamadı: " + scenarioPath);
            }

            // 2. TestStep'leri yükle
            TestStep[] testSteps = mapper.readValue(scenarioFile, TestStep[].class);
            List<TestStep> steps = Arrays.asList(testSteps);
            ReportManager.log(Status.INFO, "Toplam adım sayısı: " + steps.size());

            WebDriver driver = new ChromeDriver();
            ReportManager.setWebDriver(driver);
            ReportManager.log(Status.INFO, "Test başlatılıyor.");

            ActionExecutor executor = new ActionExecutor(driver);

            // Adımları çalıştır
            for (TestStep step : steps) {
                ReportManager.log(Status.INFO, "Adım çalıştırılıyor: " + step.getAction());
                executor.execute(step);
            }

            // Test tamamlandı
            ReportManager.log(Status.PASS, "Test başarıyla tamamlandı.");
            driver.quit();

        } catch (Exception e) {
            ReportManager.log(Status.FAIL, "Test sırasında hata oluştu: " + e.getMessage());
            throw e;
        } finally {
            // Raporlama sonlandır
            ReportManager.endTest();
            ReportManager.flushReports();
        }
    }
}