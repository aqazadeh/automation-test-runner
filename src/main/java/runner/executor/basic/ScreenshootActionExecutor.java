package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.ScreenshootActionStep;

import java.io.File;
import java.nio.file.Files;

public class ScreenshootActionExecutor extends ActionExecutor<ScreenshootActionStep> {
    @Override
    public void execute(WebDriver driver, ScreenshootActionStep step) {
        try {
            File src = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            File dest = new File("screenshots/" + step.getName());
            Files.copy(src.toPath(), dest.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Screenshot failure", e);
        }
    }
}
