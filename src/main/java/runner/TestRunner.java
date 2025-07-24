package runner;

import runner.config.TestConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        TestConfiguration config = TestConfiguration.getInstance();
        List<String> scenarioPaths = new ArrayList<>();

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if ("-scenarios".equals(args[i])) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    scenarioPaths.add(args[i + 1]);
                    i++;
                }
            } else if ("-scenarioDir".equals(args[i])) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    String dirPath = args[i + 1];
                    try {
                        List<String> files = Files.list(Paths.get(dirPath))
                                .map(Path::toString)
                                .filter(string -> string.endsWith(".json"))
                                .toList();
                        scenarioPaths.addAll(files);
                    } catch (Exception e) {
                        System.err.println("Error reading scenario directory: " + e.getMessage());
                    }
                    i++;
                }
            }
        }

        // If no scenarios specified, use default
        if (scenarioPaths.isEmpty()) {
            scenarioPaths.add("scenarios/full-selenium-demo.json");
            scenarioPaths.add("scenarios/advanced-example.json");
            scenarioPaths.add("scenarios/test1.json");
        }

        // Run tests in parallel
        int threadCount = config.getThreadCount();
        System.out.println("Running tests in parallel with " + threadCount + " threads");
        System.out.println("Browser: " + config.getBrowserType());
        System.out.println("Environment: " + config.getEnvironment());
        System.out.println("Scenarios: " + scenarioPaths);
        
        ParallelTestRunner runner = new ParallelTestRunner(scenarioPaths);
        runner.runTests();

    }
}
