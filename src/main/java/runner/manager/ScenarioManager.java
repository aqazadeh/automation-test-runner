package runner.manager;

import com.aventstack.extentreports.Status;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import runner.model.step.TestStep;

import java.util.List;

@Slf4j
public class ScenarioManager {
    public static void start(WebDriver driver, List<TestStep> steps) {

        for (TestStep step : steps) {
            log.info("current step is: {}", step);
            ReportManager.logStep(Status.PASS, "Started: " + step.getName(),  step.toString());
            //noinspection unchecked
            step.getAction().executor().execute(driver, step);
            ReportManager.logStep(Status.PASS, "End: " + step.getName(),  step.toString());
        }
    }
}
