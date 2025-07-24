package runner.executor.basic;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.ScreenshootActionStep;
import runner.config.TestConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshootActionExecutor extends ActionExecutor<ScreenshootActionStep> {
    private static final String DEFAULT_SCREENSHOTS_DIR = "screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    @Override
    public void execute(WebDriver driver, ScreenshootActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        
        if (!(driver instanceof TakesScreenshot)) {
            throw new IllegalStateException("WebDriver does not support taking screenshots. Driver type: " + driver.getClass().getSimpleName());
        }
        
        try {
            // Generate filename
            String filename = generateScreenshotFilename(step.getName());
            
            // Get screenshot directory from configuration
            TestConfiguration config = TestConfiguration.getInstance();
            String screenshotDir = config.getScreenshotDirectory();
            if (screenshotDir == null || screenshotDir.trim().isEmpty()) {
                screenshotDir = DEFAULT_SCREENSHOTS_DIR;
            }
            
            Path screenshotsDir = Paths.get(screenshotDir);
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }
            
            // Take screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = screenshotsDir.resolve(filename);
            
            Files.copy(src.toPath(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save screenshot: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to take screenshot: " + e.getMessage(), e);
        }
    }
    
    private String generateScreenshotFilename(String stepName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        if (stepName != null && !stepName.trim().isEmpty()) {
            // Sanitize filename
            String sanitized = stepName.trim().replaceAll("[^a-zA-Z0-9.-]", "_");
            return sanitized + "_" + timestamp + ".png";
        } else {
            return "screenshot_" + timestamp + ".png";
        }
    }
}
