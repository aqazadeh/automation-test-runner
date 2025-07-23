package runner.executor.waiting;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.executor.ActionExecutor;
import runner.model.step.waiting.WaitActionStep;

import java.time.Duration;

public class WaitActionExecutor extends ActionExecutor<WaitActionStep> {
    @Override
    public void execute(WebDriver driver, WaitActionStep step) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(step.getTimeout() != null ? step.getTimeout() : 10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(getBy(step.getTarget())));
    }
}
