package runner.executor.basic;

import org.openqa.selenium.WebDriver;
import runner.executor.ActionExecutor;
import runner.model.step.basic.NavigateActionStep;

public class NavigateActionExecutor extends ActionExecutor<NavigateActionStep> {

    @Override
    public void execute(WebDriver driver, NavigateActionStep step) {
        // Input validation
        validateWebDriver(driver);
        validateStep(step);
        validateString(step.getUrl(), "URL");
        
        // Additional URL validation
        String url = step.getUrl().trim();
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
            throw new IllegalArgumentException("URL must start with http://, https://, or file://. Got: " + url);
        }
        
        driver.get(url);
    }
}
