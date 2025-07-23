package runner.examples;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import runner.manager.ScenarioManager;
import runner.model.step.TestStep;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class Example {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("scenarios/advanced-example.json");
        var steps = mapper.readValue(file, new TypeReference<List<TestStep>>() {});
        WebDriver driver = new ChromeDriver();
        ScenarioManager.start(driver, steps);
        driver.quit();
    }
}
