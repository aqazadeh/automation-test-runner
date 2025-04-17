package runner.util;

import org.openqa.selenium.WebDriver;
import runner.manager.ReportManager;
import runner.model.TestStep;
import runner.util.actions.ActionExecutorFactory;

/**
 * Main action executor class that delegates test step execution to specialized executors.
 * This class uses the Factory pattern to route execution requests to the appropriate
 * specialized action executor.
 */
public class ActionExecutor {

    private final WebDriver driver;
    private final ActionExecutorFactory executorFactory;

    /**
     * Creates a new ActionExecutor with the specified WebDriver
     * @param driver The WebDriver instance to use
     */
    public ActionExecutor(WebDriver driver) {
        this.driver = driver;
        this.executorFactory = new ActionExecutorFactory(driver);
        ReportManager.setWebDriver(driver);
    }

    /**
     * Executes a test step by delegating to the appropriate specialized executor
     * @param step The test step to execute
     */
    public void execute(TestStep step) {
        executorFactory.executeStep(step);
    }
}