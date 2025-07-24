package runner;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Master test suite that organizes and runs all tests
 */
@Suite
@SuiteDisplayName("Automation Test Runner - Complete Test Suite")
@SelectPackages({
    "runner.observer",
    "runner.validation", 
    "runner.command",
    "runner.strategy",
    "runner.config",
    "runner.factory",
    "runner.util"
})
@IncludePackages({
    "runner.observer",
    "runner.validation",
    "runner.command", 
    "runner.strategy",
    "runner.performance"
})
@IncludeClassNamePatterns(".*Test.*")
public class TestSuite {
    
    // This class serves as a test suite configuration
    // Actual test execution is handled by the JUnit Platform
    
    /**
     * Smoke test suite - runs essential tests quickly
     */
    @Suite
    @SuiteDisplayName("Smoke Tests")
    @Tag("smoke")
    public static class SmokeTestSuite {
        // Include basic functionality tests
    }
    
    /**
     * Unit test suite - runs all unit tests
     */
    @Suite
    @SuiteDisplayName("Unit Tests")
    @Tag("unit")
    public static class UnitTestSuite {
        // Include all unit tests
    }
    
    /**
     * Integration test suite - runs integration tests
     */
    @Suite
    @SuiteDisplayName("Integration Tests")
    @Tag("integration")
    public static class IntegrationTestSuite {
        // Include integration tests
    }
    
    /**
     * Performance test suite - runs performance tests
     */
    @Suite
    @SuiteDisplayName("Performance Tests")
    @Tag("performance")
    public static class PerformanceTestSuite {
        // Include performance tests
    }
}