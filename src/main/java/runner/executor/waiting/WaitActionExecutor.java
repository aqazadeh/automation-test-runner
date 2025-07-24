package runner.executor.waiting;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import runner.executor.ActionExecutor;
import runner.model.step.waiting.WaitActionStep;
import runner.model.step.waiting.WaitType;

import java.time.Duration;

import static runner.util.TargetLocatorUtil.getBy;

public class WaitActionExecutor extends ActionExecutor<WaitActionStep> {
    @Override
    public void execute(WebDriver driver, WaitActionStep step) {
        if (step.getTarget() == null) {
            throw new IllegalArgumentException("Target cannot be null for wait action");
        }
        
        int timeout = step.getTimeout() != null ? step.getTimeout() : 10;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        By locator = getBy(step.getTarget());
        
        WaitType waitType = step.getWaitType() != null ? step.getWaitType() : WaitType.VISIBLE;
        
        switch (waitType) {
            case VISIBLE:
                wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                break;
            case INVISIBLE:
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
                break;
            case PRESENT:
                wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                break;
            case CLICKABLE:
                wait.until(ExpectedConditions.elementToBeClickable(locator));
                break;
            default:
                throw new IllegalArgumentException("Unsupported wait type: " + waitType);
        }
    }
}
