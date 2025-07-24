package runner.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.WebDriver;
import runner.TestBase;
import runner.manager.ScenarioManager;
import runner.model.step.TestStep;
import runner.observer.TestExecutionEventPublisher;
import runner.observer.impl.MetricsObserver;
import runner.strategy.impl.ConditionalExecutionStrategy;
import runner.strategy.impl.RetryExecutionStrategy;
import runner.strategy.impl.SequentialExecutionStrategy;
import runner.util.MockWebDriverFactory;
import runner.util.TestDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Execution Strategy Integration Tests")
class ExecutionStrategyIntegrationTest extends TestBase {
    
    private WebDriver mockDriver;
    private TestExecutionEventPublisher eventPublisher;
    private MetricsObserver metricsObserver;
    
    @BeforeEach
    @Override
    protected void setUpTest() {
        mockDriver = MockWebDriverFactory.createMockWebDriverWithElements();
        eventPublisher = ScenarioManager.getEventPublisher();
        metricsObserver = new MetricsObserver();
        eventPublisher.addObserver(metricsObserver);
    }
    
    @Override
    protected void tearDownTest() {
        eventPublisher.clearObservers();
    }
    
    @Test
    @DisplayName("Sequential strategy should execute all steps in order")
    void sequentialStrategyShouldExecuteAllStepsInOrder() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        SequentialExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        
        // When
        strategy.execute(mockDriver, steps);
        
        // Then
        var metrics = metricsObserver.getMetrics();
        assertEquals(steps.size(), metrics.getStepsStarted());
        assertEquals(steps.size(), metrics.getStepsCompleted());
        assertEquals(0, metrics.getStepsFailed());
        assertEquals(100.0, metrics.getStepSuccessRate(), 0.01);
    }
    
    @Test
    @DisplayName("Sequential strategy should stop on first failure when configured")
    void sequentialStrategyShouldStopOnFirstFailureWhenConfigured() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createFailingTestSteps();
        SequentialExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        
        // When & Then
        assertThrows(Exception.class, () -> strategy.execute(mockDriver, steps));
        
        var metrics = metricsObserver.getMetrics();
        assertEquals(1, metrics.getStepsStarted());
        assertEquals(0, metrics.getStepsCompleted());
        assertEquals(1, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Sequential strategy should continue on failure when configured")
    void sequentialStrategyShouldContinueOnFailureWhenConfigured() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createFailingTestSteps();
        SequentialExecutionStrategy strategy = new SequentialExecutionStrategy(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> strategy.execute(mockDriver, steps));
        
        var metrics = metricsObserver.getMetrics();
        assertEquals(steps.size(), metrics.getStepsStarted());
        assertEquals(0, metrics.getStepsCompleted());
        assertEquals(steps.size(), metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Retry strategy should retry on failure")
    void retryStrategyShouldRetryOnFailure() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createFailingTestSteps();
        SequentialExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        RetryExecutionStrategy retryStrategy = new RetryExecutionStrategy(baseStrategy, 2, 10);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> retryStrategy.execute(mockDriver, steps));
        
        // Should have attempted multiple times (original + 2 retries = 3 total)
        var metrics = metricsObserver.getMetrics();
        assertEquals(3, metrics.getStepsStarted()); // 3 attempts of first failing step
        assertEquals(0, metrics.getStepsCompleted());
        assertEquals(3, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Retry strategy should succeed on successful retry")
    void retryStrategyShouldSucceedOnSuccessfulRetry() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        SequentialExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        RetryExecutionStrategy retryStrategy = new RetryExecutionStrategy(baseStrategy, 2);
        
        // When
        retryStrategy.execute(mockDriver, steps);
        
        // Then
        var metrics = metricsObserver.getMetrics();
        assertEquals(steps.size(), metrics.getStepsStarted());
        assertEquals(steps.size(), metrics.getStepsCompleted());
        assertEquals(0, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Conditional strategy should skip steps based on predicate")
    void conditionalStrategyShouldSkipStepsBasedOnPredicate() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        SequentialExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        
        // Skip steps with "Click" in the name
        ConditionalExecutionStrategy conditionalStrategy = new ConditionalExecutionStrategy(
            baseStrategy, 
            step -> !step.getName().contains("Click")
        );
        
        // When
        conditionalStrategy.execute(mockDriver, steps);
        
        // Then
        var metrics = metricsObserver.getMetrics();
        
        // Should have executed fewer steps (skipped the click step)
        assertTrue(metrics.getStepsCompleted() < steps.size());
        assertEquals(0, metrics.getStepsFailed());
        assertTrue(metrics.getStepsSkipped() > 0);
    }
    
    @Test
    @DisplayName("ExecutionContext should integrate validation with strategy execution")
    void executionContextShouldIntegrateValidationWithStrategyExecution() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        ExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        ExecutionContext context = new ExecutionContext(strategy);
        
        // When
        context.execute(mockDriver, steps);
        
        // Then
        var metrics = metricsObserver.getMetrics();
        assertEquals(steps.size(), metrics.getStepsStarted());
        assertEquals(steps.size(), metrics.getStepsCompleted());
        assertEquals(0, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("ExecutionContext should fail validation for invalid WebDriver")
    void executionContextShouldFailValidationForInvalidWebDriver() {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        ExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        ExecutionContext context = new ExecutionContext(strategy);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> context.execute(null, steps));
    }
    
    @Test
    @DisplayName("ExecutionContext should fail validation for invalid steps")
    void executionContextShouldFailValidationForInvalidSteps() {
        // Given
        List<TestStep> invalidSteps = TestDataFactory.createStepsWithValidationIssues();
        ExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        ExecutionContext context = new ExecutionContext(strategy);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> context.execute(mockDriver, invalidSteps));
    }
    
    @Test
    @DisplayName("Complex strategy composition should work correctly")
    void complexStrategyCompositionShouldWorkCorrectly() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        
        // Create a complex strategy: Sequential -> Retry -> Conditional
        SequentialExecutionStrategy baseStrategy = new SequentialExecutionStrategy(true);
        RetryExecutionStrategy retryStrategy = new RetryExecutionStrategy(baseStrategy, 1);
        ConditionalExecutionStrategy conditionalStrategy = new ConditionalExecutionStrategy(
            retryStrategy, 
            step -> step.getName() != null && !step.getName().isEmpty()
        );
        
        ExecutionContext context = new ExecutionContext(conditionalStrategy);
        
        // When
        context.execute(mockDriver, steps);
        
        // Then
        var metrics = metricsObserver.getMetrics();
        assertTrue(metrics.getStepsCompleted() > 0);
        assertEquals(0, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Performance test with large number of steps")
    void performanceTestWithLargeNumberOfSteps() throws Exception {
        // Given
        List<TestStep> largeStepList = TestDataFactory.createLargeTestScenario(100);
        SequentialExecutionStrategy strategy = new SequentialExecutionStrategy(true);
        ExecutionContext context = new ExecutionContext(strategy);
        
        long startTime = System.currentTimeMillis();
        
        // When
        context.execute(mockDriver, largeStepList);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // Then
        var metrics = metricsObserver.getMetrics();
        assertEquals(100, metrics.getStepsCompleted());
        assertEquals(0, metrics.getStepsFailed());
        
        // Performance assertion - should complete within reasonable time
        assertTrue(executionTime < 10000, "Execution took too long: " + executionTime + "ms");
    }
    
    @Test
    @DisplayName("Concurrent execution with multiple strategies")
    void concurrentExecutionWithMultipleStrategies() throws Exception {
        // Given
        int threadCount = 3;
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        
        Thread[] threads = new Thread[threadCount];
        Exception[] exceptions = new Exception[threadCount];
        
        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    WebDriver threadDriver = MockWebDriverFactory.createMockWebDriverWithElements();
                    ExecutionStrategy strategy = new SequentialExecutionStrategy(true);
                    ExecutionContext context = new ExecutionContext(strategy);
                    
                    context.execute(threadDriver, steps);
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Then
        for (Exception exception : exceptions) {
            assertNull(exception, "Thread execution failed: " + 
                (exception != null ? exception.getMessage() : ""));
        }
        
        var metrics = metricsObserver.getMetrics();
        assertEquals(threadCount * steps.size(), metrics.getStepsCompleted());
        assertEquals(0, metrics.getStepsFailed());
    }
    
    @Test
    @DisplayName("Strategy factory integration test")
    void strategyFactoryIntegrationTest() throws Exception {
        // Given
        List<TestStep> steps = TestDataFactory.createBasicTestSteps();
        
        // Test different factory-created strategies
        var strategies = List.of(
            runner.factory.ExecutionStrategyFactory.createStrategy(
                runner.factory.ExecutionStrategyFactory.StrategyType.SEQUENTIAL_STOP_ON_FAILURE),
            runner.factory.ExecutionStrategyFactory.createStrategy(
                runner.factory.ExecutionStrategyFactory.StrategyType.SEQUENTIAL_CONTINUE_ON_FAILURE),
            runner.factory.ExecutionStrategyFactory.createStrategy(
                runner.factory.ExecutionStrategyFactory.StrategyType.AUTO)
        );
        
        // When & Then
        for (ExecutionStrategy strategy : strategies) {
            MetricsObserver strategyMetrics = new MetricsObserver();
            eventPublisher.addObserver(strategyMetrics);
            
            ExecutionContext context = new ExecutionContext(strategy);
            context.execute(mockDriver, steps);
            
            var metrics = strategyMetrics.getMetrics();
            assertTrue(metrics.getStepsCompleted() > 0, 
                "Strategy " + strategy.getStrategyName() + " should complete steps");
            
            eventPublisher.removeObserver(strategyMetrics);
        }
    }
}