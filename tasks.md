# Automation Test Runner - Improvement Tasks

## 🔴 Critical Priority (Must Fix)

### 1. Missing ActionExecutor Implementations
- [ ] **ExecuteScriptActionExecutor** - `src/main/java/runner/executor/browser/ExecuteScriptActionExecutor.java:11`
  - Currently throws `NotImplementedException`
  - Needs to execute JavaScript in browser context
- [ ] **ScrollToActionExecutor** - `src/main/java/runner/executor/browser/ScrollToActionExecutor.java:11`
  - Currently throws `NotImplementedException`
  - Needs to scroll to element or coordinates
- [ ] **WaitForJsActionExecutor** - `src/main/java/runner/executor/browser/WaitForJsActionExecutor.java:11`
  - Currently throws `NotImplementedException`
  - Needs to wait for JavaScript condition to be true

### 2. TestStep Registration Issues
- [ ] **Register all missing TestStep classes in TestStep.java JsonSubTypes**
  - Add 12+ missing action types (ALERT, MOUSE, BROWSER, WINDOW actions)
- [ ] **Add corresponding TestAction enum values**
  - Update TestAction.java with all missing action types and their executors

### 3. Critical Bug Fixes
- [ ] **Fix WaitActionExecutor logic error** - `src/main/java/runner/executor/waiting/WaitActionExecutor.java:15`
  - Currently uses `invisibilityOfElementLocated` which is likely wrong
  - Should be configurable or use `visibilityOfElementLocated`
- [ ] **Add null checks in ConditionActionExecutor** - `src/main/java/runner/executor/condition/ConditionActionExecutor.java:42`
  - Prevent NullPointerException in condition comparison

## 🟡 High Priority (Important Improvements)

### 4. Architecture Standardization
- [ ] **Standardize TestStep constructor patterns**
  - Implement static factory methods for all TestStep classes
  - Make setters private and consistent across all classes
- [ ] **Add proper Lombok annotations**
  - Fix Target.java to use Lombok instead of manual toString()
  - Standardize @Getter, @Setter, @ToString usage

### 5. Error Handling & Validation
- [ ] **Add comprehensive error handling in ScenarioManager**
  - Wrap step execution in try-catch blocks
  - Handle WebDriver exceptions gracefully
- [ ] **Add input validation to all ActionExecutors**
  - Validate step parameters before execution
  - Add meaningful error messages

### 6. Missing Model Implementations
- [ ] **Complete ExecuteScriptActionStep implementation**
  - Add script field and getter/setter
  - Add factory method
- [ ] **Fix DragAndDropActionStep**
  - Add proper setter methods and factory pattern
  - Ensure consistency with other action steps

## 🟢 Medium Priority (Quality Improvements)

### 7. Utility Classes
- [ ] **Create WebDriverFactory class**
  - Support multiple browser types (Chrome, Firefox, Edge, Safari)
  - Centralized driver configuration management
- [ ] **Create WebDriverWaitUtil class**
  - Centralized wait conditions
  - Consistent timeout handling across executors
- [ ] **Create ElementFinder utility**
  - Support for finding multiple elements
  - Custom wait conditions

### 8. Configuration Management
- [ ] **Create Configuration class**
  - Centralized timeout settings
  - Environment-specific configurations
  - Browser settings management
- [ ] **Add properties file support**
  - External configuration for different environments
  - Runtime parameter overrides

### 9. Clean Up Legacy Code
- [ ] **Remove or modernize commented code**
  - FrameActionExecutor.java (fully commented out)
  - WaitingActionExecutor.java (fully commented out)
- [ ] **Decide on frame support**
  - Either implement frame switching or remove related files

## 🔵 Low Priority (Nice to Have)

### 10. Design Pattern Improvements
- [ ] **Implement Builder pattern for complex TestSteps**
  - DragAndDropActionStep would benefit from builder
  - Mouse action combinations
- [ ] **Create ActionExecutorFactory**
  - Centralized executor creation
  - Common validation logic

### 11. Testing Infrastructure
- [ ] **Add unit tests for all classes**
  - Test all ActionExecutors
  - Test TestStep serialization/deserialization
  - Test ScenarioManager execution flow
- [ ] **Add integration tests**
  - End-to-end test scenarios
  - Browser compatibility tests

### 12. Advanced Features
- [ ] **Enhanced reporting**
  - Detailed step execution logs
  - Screenshot capture on failures
  - Performance metrics

### 13. Documentation
- [ ] **Add JavaDoc comments**
  - Document all public methods and classes
  - Add usage examples
- [ ] **Create usage documentation**
  - How to create test scenarios
  - Available actions and their parameters

## Implementation Order Recommendation

1. **Start with Critical Priority items 1-3** (blocking issues)
2. **Address TestStep registration and standardization** (items 4-6)
3. **Add utility classes and configuration** (items 7-8)
4. **Clean up and enhance** (items 9-13)

## Estimated Effort

- **Critical Priority**: 2-3 days
- **High Priority**: 3-4 days  
- **Medium Priority**: 4-5 days
- **Low Priority**: 5-7 days

**Total estimated effort**: 2-3 weeks for full implementation

---

*Generated by analyzing the automation-test-runner codebase for architecture improvements and missing implementations.*