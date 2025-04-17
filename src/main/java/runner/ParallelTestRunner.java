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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Parallel test runner that can execute multiple test scenarios in parallel.
 */
public class ParallelTestRunner {
    
    private final int threadCount;
    private final List<String> scenarioPaths;
    
    /**
     * Creates a new ParallelTestRunner with the specified thread count and scenario paths.
     * 
     * @param threadCount The number of threads to use for parallel execution
     * @param scenarioPaths The paths to the test scenario files
     */
    public ParallelTestRunner(int threadCount, List<String> scenarioPaths) {
        this.threadCount = threadCount;
        this.scenarioPaths = scenarioPaths;
    }
    
    /**
     * Runs the test scenarios in parallel.
     * 
     * @throws Exception If an error occurs during test execution
     */
    public void runTests() throws Exception {
        // Initialize reports
        ReportManager.initReports("Parallel Test Suite - " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // Create thread pool with the specified number of threads
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // Submit each scenario as a separate task
        for (String scenarioPath : scenarioPaths) {
            executor.submit(() -> {
                try {
                    runScenario(scenarioPath);
                } catch (Exception e) {
                    System.err.println("Error running scenario " + scenarioPath + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        
        // Shutdown the executor and wait for all tasks to complete
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Flush reports
        ReportManager.flushReports();
    }
    
    /**
     * Runs a single test scenario.
     * 
     * @param scenarioPath The path to the test scenario file
     * @throws Exception If an error occurs during test execution
     */
    private void runScenario(String scenarioPath) throws Exception {
        Path path = Paths.get(scenarioPath);
        String testName = path.getFileName().toString().replace(".json", "");
        
        // Start test
        ReportManager.startTest("Senaryo: " + testName + " [Thread: " + Thread.currentThread().getName() + "]");
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            File scenarioFile = new File(scenarioPath);
            
            // Check if file exists
            if (scenarioFile.exists()) {
                ReportManager.log(Status.INFO, "Senaryo dosyası bulundu: " + scenarioPath);
            } else {
                ReportManager.log(Status.FAIL, "Senaryo dosyası bulunamadı: " + scenarioPath);
                throw new RuntimeException("Senaryo dosyası bulunamadı: " + scenarioPath);
            }
            
            // Load test steps
            TestStep[] testSteps = mapper.readValue(scenarioFile, TestStep[].class);
            List<TestStep> steps = Arrays.asList(testSteps);
            ReportManager.log(Status.INFO, "Toplam adım sayısı: " + steps.size());
            
            // Initialize WebDriver
            WebDriver driver = new ChromeDriver();
            ReportManager.setWebDriver(driver);
            ReportManager.log(Status.INFO, "Test başlatılıyor.");
            
            // Execute steps
            ActionExecutor executor = new ActionExecutor(driver);
            for (TestStep step : steps) {
                ReportManager.log(Status.INFO, "Adım çalıştırılıyor: " + step.getAction());
                executor.execute(step);
            }
            
            // Test completed
            ReportManager.log(Status.PASS, "Test başarıyla tamamlandı.");
            driver.quit();
            
        } catch (Exception e) {
            ReportManager.log(Status.FAIL, "Test sırasında hata oluştu: " + e.getMessage());
            throw e;
        } finally {
            // End test
            ReportManager.endTest();
        }
    }
}