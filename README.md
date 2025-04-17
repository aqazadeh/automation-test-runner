# JSON Selenium Runner

JSON Selenium Runner is a flexible and powerful test automation framework that allows you to define and execute Selenium WebDriver tests using JSON configuration files without writing any Java code.

## Project Overview

This project provides a declarative approach to web test automation. Instead of writing Selenium code directly, you define your test scenarios as JSON files specifying what actions to perform and what elements to interact with. The framework handles all the WebDriver management, execution flow, waiting mechanisms, and reporting.

### Key Features

- **JSON-based test definition**: Write test scenarios in JSON format without coding
- **Rich set of actions**: Supports 30+ different actions for comprehensive web testing
- **Conditional execution**: Execute steps based on conditions
- **Detailed reporting**: Generates HTML reports with screenshots and execution details
- **Reusable steps**: Create complex test flows with repeatable steps
- **Cross-browser support**: Works with different browsers via WebDriverManager

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Installation

1. Clone the repository
2. Build the project with Maven:

```
mvn clean install
```

### Running Tests

You can run tests either sequentially or in parallel.

#### Sequential Execution

To run a single test scenario:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar
```

By default, it will run the scenario at `scenarios/full-selenium-demo.json`.

To specify a different scenario:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -scenarios path/to/your/scenario.json
```

You can specify multiple scenarios:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -scenarios scenario1.json -scenarios scenario2.json
```

To run all scenarios in a directory:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -scenarioDir path/to/scenarios/directory
```

#### Parallel Execution

To run tests in parallel:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -parallel
```

By default, it will use the number of available processors as the thread count. You can specify a custom thread count:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -parallel 4
```

You can combine parallel execution with scenario options:

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -parallel 4 -scenarioDir path/to/scenarios/directory
```

```
java -jar target/json-selenium-runner-1.0-SNAPSHOT.jar -parallel 4 -scenarios scenario1.json -scenarios scenario2.json
```

## Creating Test Steps

Test steps are defined in JSON format and represent actions to be performed during test execution. Each step is a JSON object with properties that define what action to perform and how.

### Basic Structure of a Test Step

A test step is a JSON object with the following basic structure:

```json
{
  "action": "actionName",
  "target": { "by": "locatorType", "value": "locatorValue" },
  "value": "optionalValue",
  "timeout": 10,
  "continueOnFailure": false
}
```

### Common Properties

- **action**: (Required) The type of action to perform (e.g., "click", "type", "navigate")
- **target**: (Optional) The element to interact with, defined by a locator strategy
  - **by**: The locator type (e.g., "id", "css", "xpath", "name", "linkText")
  - **value**: The locator value
- **value**: (Optional) Input value or expected text for assertions
- **timeout**: (Optional) Maximum wait time in seconds
- **continueOnFailure**: (Optional) Whether to continue test execution if this step fails

### Element Locator Strategies

Elements can be located using various strategies:

```
// Using ID
"target": { "by": "id", "value": "username" }

// Using name attribute
"target": { "by": "name", "value": "password" }

// Using CSS selector
"target": { "by": "css", "value": ".login-button" }

// Using XPath
"target": { "by": "xpath", "value": "//button[@type='submit']" }

// Using link text
"target": { "by": "linkText", "value": "Forgot Password?" }

// Using tag name
"target": { "by": "tagName", "value": "h1" }

// Using class name
"target": { "by": "className", "value": "error-message" }
```

### Examples of Common Test Steps

#### Navigation

```json
{
  "action": "navigate",
  "url": "https://example.com"
}
```

#### Clicking Elements

```json
{
  "action": "click",
  "target": { "by": "id", "value": "login-button" }
}
```

#### Typing Text

```json
{
  "action": "type",
  "target": { "by": "name", "value": "username" },
  "value": "testuser@example.com"
}
```

#### Clearing Input Fields

```json
{
  "action": "clear",
  "target": { "by": "name", "value": "username" }
}
```

#### Waiting for Elements

```json
{
  "action": "waitFor",
  "target": { "by": "id", "value": "dashboard" },
  "timeout": 10
}
```

#### Assertions

```json
{
  "action": "assertText",
  "target": { "by": "css", "value": ".welcome-message" },
  "value": "Welcome to your account"
}
```

#### Taking Screenshots

```json
{
  "action": "screenshot",
  "value": "login-page.png"
}
```

### Advanced Test Steps

#### Conditional Execution

Execute a step only if a condition is met:

```json
{
  "action": "click",
  "target": { "by": "id", "value": "notification-dismiss" },
  "condition": {
    "target": { "by": "id", "value": "notification-dismiss" },
    "type": "visible"
  }
}
```

#### Repeating Steps

Execute a sequence of steps multiple times:

```json
{
  "action": "repeat",
  "times": 3,
  "steps": [
    {
      "action": "click",
      "target": { "by": "css", "value": ".load-more" }
    },
    {
      "action": "waitFor",
      "target": { "by": "css", "value": ".new-items" },
      "timeout": 5
    }
  ]
}
```

#### Working with Alerts

Accept an alert:
```json
{
  "action": "handleAlert",
  "alertAction": "accept"
}
```

Dismiss an alert:
```json
{
  "action": "handleAlert",
  "alertAction": "dismiss"
}
```

Enter text in a prompt:
```json
{
  "action": "handleAlert",
  "alertAction": "sendKeys",
  "value": "Text to enter in prompt"
}
```

#### Drag and Drop

```json
{
  "action": "dragAndDrop",
  "sourceTarget": { "by": "id", "value": "draggable-item" },
  "destinationTarget": { "by": "id", "value": "drop-zone" }
}
```

#### Working with iFrames

Switch to an iframe:
```json
{
  "action": "switchToFrame",
  "target": { "by": "id", "value": "iframe-id" }
}
```

Switch back to the main document:
```json
{
  "action": "switchToDefaultContent"
}
```

#### Executing JavaScript

Execute JavaScript without targeting an element:
```json
{
  "action": "executeJS",
  "script": "return document.title;"
}
```

Execute JavaScript on a specific element:
```json
{
  "action": "executeJS",
  "target": { "by": "id", "value": "element-id" },
  "script": "arguments[0].style.backgroundColor = 'yellow'; return true;"
}
```

#### Mouse Hover

```json
{
  "action": "hover",
  "target": { "by": "css", "value": ".dropdown-menu" }
}
```

#### Getting Element Attributes

```json
{
  "action": "getAttribute",
  "target": { "by": "id", "value": "link-element" },
  "attribute": "href"
}
```

#### Keyboard Actions

```json
{
  "action": "pressKey",
  "target": { "by": "id", "value": "input-field" },
  "key": "Keys.ENTER"
}
```

#### Browser Navigation

Navigate back:
```json
{
  "action": "back"
}
```

Navigate forward:
```json
{
  "action": "forward"
}
```

Refresh the page:
```json
{
  "action": "refresh"
}
```

#### Specialized Wait Conditions

Wait for an element to be clickable:
```json
{
  "action": "waitForElementToBeClickable",
  "target": { "by": "id", "value": "button-id" },
  "timeout": 10
}
```

Wait for an element to disappear:
```json
{
  "action": "waitForElementToBeInvisible",
  "target": { "by": "id", "value": "loading-spinner" },
  "timeout": 15
}
```

### Complete Test Scenario Example

Here's a complete example of a login test scenario:

```json
[
  {
    "action": "navigate",
    "url": "https://example.com/login"
  },
  {
    "action": "waitFor",
    "target": { "by": "id", "value": "login-form" },
    "timeout": 10
  },
  {
    "action": "type",
    "target": { "by": "name", "value": "email" },
    "value": "test@example.com"
  },
  {
    "action": "type",
    "target": { "by": "name", "value": "password" },
    "value": "password123"
  },
  {
    "action": "click",
    "target": { "by": "id", "value": "login-button" }
  },
  {
    "action": "waitFor",
    "target": { "by": "id", "value": "dashboard" },
    "timeout": 10
  },
  {
    "action": "assertText",
    "target": { "by": "css", "value": ".welcome-message" },
    "value": "Welcome, Test User"
  },
  {
    "action": "screenshot",
    "value": "successful-login.png"
  }
]
```
