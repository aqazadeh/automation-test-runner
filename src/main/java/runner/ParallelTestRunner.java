package runner;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import runner.manager.ReportManager;
import runner.manager.ScenarioManager;
import runner.model.step.TestStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ParallelTestRunner {

    private final int threadCount;
    private final List<String> scenarioPaths;


    public ParallelTestRunner(int threadCount, List<String> scenarioPaths) {
        this.threadCount = threadCount;
        this.scenarioPaths = scenarioPaths;
    }

    public void runTests() throws Exception {
        ReportManager.initReports("Parallel Test Suite - " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

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

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        ReportManager.flushReports();
    }


    private void runScenario(String scenarioPath) throws Exception {
        Path path = Paths.get(scenarioPath);
        String testName = path.getFileName().toString().replace(".json", "");

        ReportManager.startTest("Scenario: " + testName + " [Thread: " + Thread.currentThread().getName() + "]");

        try {
            ObjectMapper mapper = new ObjectMapper();
            File scenarioFile = new File(scenarioPath);

            if (scenarioFile.exists()) {
                ReportManager.log(Status.INFO, "Scenario file found: " + scenarioPath);
            } else {
                ReportManager.log(Status.FAIL, "Scenario file not found: " + scenarioPath);
                throw new RuntimeException("Scenario file not found: " + scenarioPath);
            }

            var steps = mapper.readValue(scenarioFile, new TypeReference<List<TestStep>>() {
            });
            ReportManager.log(Status.INFO, "Total step count: " + steps.size());

            WebDriver driver = new ChromeDriver();
            ReportManager.setWebDriver(driver);
            ReportManager.log(Status.INFO, "Starting test case");

            ScenarioManager.start(driver, steps);
            ReportManager.log(Status.PASS, "Test completed successfully.");
            driver.quit();
        } catch (Exception e) {
            ReportManager.log(Status.FAIL, "An error occurred during testing: " + e.getMessage());
            throw e;
        } finally {
            ReportManager.endTest();
        }
    }
}