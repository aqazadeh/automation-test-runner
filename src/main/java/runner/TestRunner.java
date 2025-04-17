package runner;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;
import runner.executor.ActionExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        boolean parallelExecution = false;
        int threadCount = Runtime.getRuntime().availableProcessors();
        List<String> scenarioPaths = new ArrayList<>();

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if ("-parallel".equals(args[i])) {
                parallelExecution = true;
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    try {
                        threadCount = Integer.parseInt(args[i + 1]);
                        i++;
                    } catch (NumberFormatException e) {
                        // Use default thread count
                    }
                }
            } else if ("-scenarios".equals(args[i])) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    scenarioPaths.add(args[i + 1]);
                    i++;
                }
            } else if ("-scenarioDir".equals(args[i])) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    String dirPath = args[i + 1];
                    try {
                        List<String> files = Files.list(Paths.get(dirPath))
                                .filter(p -> p.toString().endsWith(".json"))
                                .map(Path::toString)
                                .collect(Collectors.toList());
                        scenarioPaths.addAll(files);
                    } catch (Exception e) {
                        System.err.println("Error reading scenario directory: " + e.getMessage());
                    }
                    i++;
                }
            }
        }

        // If no scenarios specified, use default
        if (scenarioPaths.isEmpty()) {
            scenarioPaths.add("scenarios/full-selenium-demo.json");
        }

        // Run tests in parallel or sequentially
        if (parallelExecution) {
            System.out.println("Running tests in parallel with " + threadCount + " threads");
            System.out.println("Scenarios: " + scenarioPaths);
            ParallelTestRunner runner = new ParallelTestRunner(threadCount, scenarioPaths);
            runner.runTests();
        } else {
            // Run tests sequentially
            for (String scenarioPath : scenarioPaths) {
                runSingleTest(scenarioPath);
            }
        }
    }

    private static void runSingleTest(String scenarioPath) throws Exception {
        // Raporlama başlat
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
