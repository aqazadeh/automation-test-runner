package runner.executor.window;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.window.SwitchToWindowActionStep;

import java.util.Set;

/**
 * Executor for switching to a different window or tab
 */
public class SwitchToWindowActionExecutor extends ActionExecutor<SwitchToWindowActionStep> {
    
    @Override
    public void execute(WebDriver driver, SwitchToWindowActionStep step) {
        if (step.getWindowHandle() != null) {
            // Switch by handle
            driver.switchTo().window(step.getWindowHandle());
        } else if (step.getWindowTitleOrUrl() != null) {
            // Switch by title or URL
            switchTitleOrUrl(driver, step);
        } else {
            String currentHandle = driver.getWindowHandle();
            Set<String> handles = driver.getWindowHandles();
            handles.remove(currentHandle);
            
            if (handles.isEmpty()) {
                throw new NoSuchWindowException("No new window found");
            }
            
            driver.switchTo().window(handles.iterator().next());
        }
    }

    private void switchTitleOrUrl(WebDriver driver, SwitchToWindowActionStep step) {
        boolean switched = false;
        Set<String> handles = driver.getWindowHandles();
        String currentHandle = driver.getWindowHandle();

        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contains(step.getWindowTitleOrUrl()) ||
                driver.getCurrentUrl().contains(step.getWindowTitleOrUrl())) {
                switched = true;
                break;
            }
        }

        if (!switched) {
            driver.switchTo().window(currentHandle);
            throw new NoSuchWindowException("Window with specified title or URL not found: " + step.getWindowTitleOrUrl());
        }
    }
}