package runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import runner.model.TestStep;
import runner.util.ActionExecutor;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File scenarioFile = new File("scenarios/full-selenium-demo.json");

        // 1. Dosya varlığını kontrol et
        System.out.println("Dosya mevcut mu? " + scenarioFile.exists());

        // 2. TestStep'leri yükle
        TestStep[] testSteps = mapper.readValue(scenarioFile, TestStep[].class);
        List<TestStep> steps = Arrays.asList(testSteps);
        System.out.println("Yüklü adım sayısı: " + steps.size());


        WebDriver driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
//
//        ActionExecutor executor = new ActionExecutor(driver);
//
//        for (TestStep step : steps) {
//            System.out.println("Çalıştırılan adım: " + step.getAction()); // Adımı logla
//            try {
//                executor.execute(step);
//            } catch (Exception e) {
//                e.printStackTrace(); // Hataları yakala
//            }
//        }

        driver.quit();
    }
}
