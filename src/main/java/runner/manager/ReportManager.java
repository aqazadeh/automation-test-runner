package runner.manager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import runner.config.TestConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String REPORT_DIRECTORY = "test-reports";

    public static void initReports(String testSuiteName) {
        if (extent == null) {
            // Rapor dizinini oluştur
            createReportDirectory();

            // Raporun tarihli formatını oluştur
            TestConfiguration config = TestConfiguration.getInstance();
            String reportsDir = config.getReportsDirectory();
            if (reportsDir == null || reportsDir.trim().isEmpty()) {
                reportsDir = REPORT_DIRECTORY;
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = reportsDir + "/report_" + timestamp + ".html";

            // ExtentReports nesnesini yapılandır
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setDocumentTitle("Test Automation Report");
            reporter.config().setReportName(testSuiteName);
            reporter.config().setTheme(Theme.STANDARD);
            reporter.config().setEncoding("utf-8");

            // ExtentReports'u başlat
            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Operation System", System.getProperty("os.name"));
            extent.setSystemInfo("Java version", System.getProperty("java.version"));
            extent.setSystemInfo("Test scenario", testSuiteName);
        }
    }

    private static void createReportDirectory() {
        TestConfiguration config = TestConfiguration.getInstance();
        String reportsDir = config.getReportsDirectory();
        if (reportsDir == null || reportsDir.trim().isEmpty()) {
            reportsDir = REPORT_DIRECTORY;
        }
        
        Path dirPath = Paths.get(reportsDir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.err.println("Failed to create report directory: " + e.getMessage());
            }
        }
    }

    public static void setWebDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }

    public static void startTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
    }

    public static void endTest() {
        test.remove();
    }

    public static void log(Status status, String message) {
        if (test.get() != null) {
            test.get().log(status, message);
        }
    }

    public static void logStep(Status status, String stepName, String description) {
        if (test.get() != null) {
            ExtentTest step = test.get().createNode(stepName);
            step.log(status, description);
        }
    }

    public static String takeScreenshot(String screenshotName) {
        if (driver.get() == null) {
            log(Status.WARNING, "Failed to capture screenshot: WebDriver instance not found.");
            return null;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver.get();
            File source = ts.getScreenshotAs(OutputType.FILE);

            // Ekran görüntüsü dizinini oluştur
            TestConfiguration config = TestConfiguration.getInstance();
            String baseReportsDir = config.getReportsDirectory();
            if (baseReportsDir == null || baseReportsDir.trim().isEmpty()) {
                baseReportsDir = REPORT_DIRECTORY;
            }
            
            String screenshotDir = baseReportsDir + "/screenshots";
            Path dirPath = Paths.get(screenshotDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            String destination = screenshotDir + "/" + fileName;

            Files.copy(source.toPath(), Paths.get(destination));

            // Göreceli yolu döndür
            return "screenshots/" + fileName;
        } catch (Exception e) {
            log(Status.WARNING, "Could not take screenshot: " + e.getMessage());
            return null;
        }
    }

    public static void attachScreenshot(String screenshotName) {
        String screenshotPath = takeScreenshot(screenshotName);
        if (screenshotPath != null && test.get() != null) {
            test.get().addScreenCaptureFromPath(screenshotPath);
        }
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
