package runner.performance;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.WebDriver;
import runner.TestBase;
import runner.manager.ScenarioManager;
import runner.model.step.TestStep;
import runner.observer.TestExecutionEventPublisher;
import runner.observer.impl.MetricsObserver;
import runner.strategy.ExecutionContext;
import runner.strategy.ExecutionStrategy;
import runner.strategy.impl.SequentialExecutionStrategy;
import runner.util.MockWebDriverFactory;
import runner.util.TestDataFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Performance Test Suite")
@Execution(ExecutionMode.CONCURRENT)
class PerformanceTestSuite extends TestBase {
    
    private static final int PERFORMANCE_THRESHOLD_MS = 5000;
    private static final int LOAD_TEST_THREADS = 10;
    private static final int LOAD_TEST_ITERATIONS = 5;
    
    @Test
    @DisplayName("Single strategy execution performance")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void singleStrategyExecutionPerformance() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createLargeTestScenario(50);
        WebDriver mockDriver = MockWebDriverFactory.createMockWebDriverWithElements();
        ExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        ExecutionContext context = new ExecutionContext(strategy);
        
        MetricsObserver metrics = new MetricsObserver();
        TestExecutionEventPublisher publisher = ScenarioManager.getEventPublisher();
        publisher.addObserver(metrics);
        
        // When
        long startTime = System.currentTimeMillis();
        context.execute(mockDriver, steps);
        long endTime = System.currentTimeMillis();
        
        // Then
        long executionTime = endTime - startTime;
        assertTrue(executionTime < PERFORMANCE_THRESHOLD_MS, 
            "Execution time " + executionTime + "ms exceeded threshold " + PERFORMANCE_THRESHOLD_MS + "ms");
        
        var metricsData = metrics.getMetrics();
        assertEquals(50, metricsData.getStepsCompleted());
        assertEquals(100.0, metricsData.getStepSuccessRate(), 0.01);
        
        publisher.removeObserver(metrics);
    }
    
    @Test
    @DisplayName("Memory usage test with large scenarios")
    void memoryUsageTestWithLargeScenarios() throws Exception {
        // Given
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Trigger garbage collection before test
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        List<TestStep> largeScenario = TestDataFactory.createLargeTestScenario(200);
        WebDriver mockDriver = MockWebDriverFactory.createMockWebDriverWithElements();
        ExecutionContext context = new ExecutionContext(new SequentialExecutionStrategy(true));
        
        // When
        context.execute(mockDriver, largeScenario);
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        // Then
        assertTrue(memoryUsed < 100 * 1024 * 1024, // 100MB threshold
            "Memory usage " + (memoryUsed / 1024 / 1024) + "MB exceeded threshold");
    }
    
    @Test
    @DisplayName("Concurrent execution load test")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void concurrentExecutionLoadTest() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(LOAD_TEST_THREADS);
        
        MetricsObserver[] metricsObservers = new MetricsObserver[LOAD_TEST_THREADS];
        Exception[] exceptions = new Exception[LOAD_TEST_THREADS];
        long[] executionTimes = new long[LOAD_TEST_THREADS];
        
        // When
        for (int i = 0; i < LOAD_TEST_THREADS; i++) {
            final int threadIndex = i;
            metricsObservers[i] = new MetricsObserver();
            
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    long threadStart = System.currentTimeMillis();
                    
                    for (int iteration = 0; iteration < LOAD_TEST_ITERATIONS; iteration++) {
                        WebDriver threadDriver = MockWebDriverFactory.createMockWebDriverWithElements();
                        ExecutionContext context = new ExecutionContext(new SequentialExecutionStrategy(true));
                        
                        TestExecutionEventPublisher publisher = new TestExecutionEventPublisher();
                        publisher.addObserver(metricsObservers[threadIndex]);
                        
                        context.execute(threadDriver, steps);
                    }
                    
                    executionTimes[threadIndex] = System.currentTimeMillis() - threadStart;
                    
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                } finally {
                    completionLatch.countDown();
                }
            });
            
            thread.start();
        }
        
        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for completion
        assertTrue(completionLatch.await(25, TimeUnit.SECONDS), 
            "Load test did not complete within timeout");
        
        // Then
        for (int i = 0; i < LOAD_TEST_THREADS; i++) {
            assertNull(exceptions[i], "Thread " + i + " failed: " + 
                (exceptions[i] != null ? exceptions[i].getMessage() : ""));
            
            var metrics = metricsObservers[i].getMetrics();
            assertEquals(LOAD_TEST_ITERATIONS * steps.size(), metrics.getStepsCompleted(),
                "Thread " + i + " did not complete all steps");
            
            assertTrue(executionTimes[i] < PERFORMANCE_THRESHOLD_MS * 2,
                "Thread " + i + " execution time " + executionTimes[i] + "ms exceeded threshold");
        }
        
        // Verify overall performance
        long averageExecutionTime = IntStream.of(0, LOAD_TEST_THREADS - 1)
            .mapToLong(i -> executionTimes[i])
            .sum() / LOAD_TEST_THREADS;
        
        assertTrue(averageExecutionTime < PERFORMANCE_THRESHOLD_MS * 1.5,
            "Average execution time " + averageExecutionTime + "ms exceeded threshold");
    }
    
    @Test
    @DisplayName("Observer notification performance test")
    void observerNotificationPerformanceTest() throws Exception {
        // Given
        int observerCount = 20;
        int eventCount = 1000;
        
        TestExecutionEventPublisher publisher = new TestExecutionEventPublisher();
        MetricsObserver[] observers = new MetricsObserver[observerCount];
        
        for (int i = 0; i < observerCount; i++) {
            observers[i] = new MetricsObserver();
            publisher.addObserver(observers[i]);
        }
        
        // When
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < eventCount; i++) {
            publisher.publishScenarioStarted("Performance test scenario " + i);
            publisher.publishScenarioCompleted("Performance test completed " + i);
        }
        
        long endTime = System.currentTimeMillis();
        long notificationTime = endTime - startTime;
        
        // Then
        assertTrue(notificationTime < 2000, // 2 second threshold
            "Observer notification time " + notificationTime + "ms exceeded threshold");
        
        // Verify all observers received all events
        for (MetricsObserver observer : observers) {
            var metrics = observer.getMetrics();
            assertEquals(eventCount, metrics.getScenariosStarted());
            assertEquals(eventCount, metrics.getScenariosCompleted());
        }
    }
    
    @Test
    @DisplayName("Validation chain performance test")
    void validationChainPerformanceTest() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createLargeTestScenario(100);
        WebDriver mockDriver = MockWebDriverFactory.createMockWebDriverWithElements();
        
        ExecutionContext contextWithValidation = new ExecutionContext(new SequentialExecutionStrategy(true));
        ExecutionContext contextWithoutValidation = new ExecutionContext(
            new SequentialExecutionStrategy(true), null, null);
        
        // When - measure with validation
        long startWithValidation = System.currentTimeMillis();
        contextWithValidation.execute(mockDriver, steps);
        long timeWithValidation = System.currentTimeMillis() - startWithValidation;
        
        // When - measure without validation
        long startWithoutValidation = System.currentTimeMillis();
        contextWithoutValidation.execute(mockDriver, steps);
        long timeWithoutValidation = System.currentTimeMillis() - startWithoutValidation;
        
        // Then
        double validationOverhead = ((double) timeWithValidation - timeWithoutValidation) / timeWithoutValidation * 100;
        
        assertTrue(validationOverhead < 50, // Less than 50% overhead
            "Validation overhead " + validationOverhead + "% is too high");
        
        assertTrue(timeWithValidation < PERFORMANCE_THRESHOLD_MS,
            "Execution with validation took too long: " + timeWithValidation + "ms");
    }
    
    @Test
    @DisplayName("Factory creation performance test")
    void factoryCreationPerformanceTest() {
        // Given
        int creationCount = 1000;
        
        // When - Strategy factory performance
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < creationCount; i++) {
            runner.factory.ExecutionStrategyFactory.createStrategy(
                runner.factory.ExecutionStrategyFactory.StrategyType.AUTO);
        }
        
        long strategyCreationTime = System.currentTimeMillis() - startTime;
        
        // When - Observer factory performance
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < creationCount; i++) {
            runner.factory.ObserverFactory.createObservers(
                runner.factory.ObserverFactory.ObserverType.ALL_DEFAULT);
        }
        
        long observerCreationTime = System.currentTimeMillis() - startTime;
        
        // Then
        assertTrue(strategyCreationTime < 1000, // 1 second threshold
            "Strategy factory creation time " + strategyCreationTime + "ms exceeded threshold");
        
        assertTrue(observerCreationTime < 2000, // 2 second threshold
            "Observer factory creation time " + observerCreationTime + "ms exceeded threshold");
    }
    
    @Test
    @DisplayName("Stress test with maximum concurrent scenarios")
    void stressTestWithMaximumConcurrentScenarios() throws Exception {
        // Given
        int maxConcurrency = Runtime.getRuntime().availableProcessors() * 2;
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        
        CompletableFuture<?>[] futures = new CompletableFuture[maxConcurrency];
        
        // When
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < maxConcurrency; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    WebDriver driver = MockWebDriverFactory.createMockWebDriverWithElements();
                    ExecutionContext context = new ExecutionContext(new SequentialExecutionStrategy(true));
                    context.execute(driver, steps);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        // Wait for all to complete
        CompletableFuture.allOf(futures).get(20, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Then
        assertTrue(totalTime < 15000, // 15 second threshold
            "Stress test execution time " + totalTime + "ms exceeded threshold");
        
        // Verify no futures completed exceptionally
        for (CompletableFuture<?> future : futures) {
            assertFalse(future.isCompletedExceptionally(), "Future completed exceptionally");
        }
    }
    
    @Test
    @DisplayName("Resource cleanup performance test")
    void resourceCleanupPerformanceTest() throws Exception {
        // Given
        int scenarioCount = 50;
        
        // When
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < scenarioCount; i++) {
            TestExecutionEventPublisher publisher = new TestExecutionEventPublisher();
            MetricsObserver observer = new MetricsObserver();
            publisher.addObserver(observer);
            
            List<TestStep> steps = TestDataFactory.createBasicTestSteps();
            WebDriver driver = MockWebDriverFactory.createCloseableMockWebDriver();
            ExecutionContext context = new ExecutionContext(new SequentialExecutionStrategy(true));
            
            context.execute(driver, steps);
            
            // Cleanup
            publisher.clearObservers();
            observer.reset();
        }
        
        long cleanupTime = System.currentTimeMillis() - startTime;
        
        // Then
        assertTrue(cleanupTime < PERFORMANCE_THRESHOLD_MS,
            "Resource cleanup time " + cleanupTime + "ms exceeded threshold");
        
        // Force garbage collection and verify memory doesn't keep growing
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        
        assertTrue(finalMemory < 200 * 1024 * 1024, // 200MB threshold
            "Final memory usage " + (finalMemory / 1024 / 1024) + "MB too high after cleanup");
    }
}