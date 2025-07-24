package runner.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import runner.model.Target;
import runner.config.TestConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ElementFinder {
    
    private final WebDriver driver;
    
    public ElementFinder(WebDriver driver) {
        this.driver = driver;
    }
    
    // Static factory method
    public static ElementFinder using(WebDriver driver) {
        return new ElementFinder(driver);
    }
    
    // Basic element finding
    public Optional<WebElement> findElement(Target target) {
        try {
            By locator = getBy(target);
            WebElement element = driver.findElement(locator);
            return Optional.of(element);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
    
    public WebElement findElementOrThrow(Target target) {
        return findElement(target)
                .orElseThrow(() -> new NoSuchElementException("Element not found: " + target));
    }
    
    public List<WebElement> findElements(Target target) {
        By locator = getBy(target);
        return driver.findElements(locator);
    }
    
    // Find with wait
    public Optional<WebElement> findElementWithWait(Target target) {
        TestConfiguration config = TestConfiguration.getInstance();
        return findElementWithWait(target, config.getExplicitWaitSeconds());
    }
    
    public Optional<WebElement> findElementWithWait(Target target, int timeoutSeconds) {
        try {
            WebElement element = WebDriverWaitUtil.waitForElement(driver, target, WebDriverWaitUtil.WaitCondition.PRESENT, timeoutSeconds);
            return Optional.of(element);
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }
    
    public WebElement findElementWithWaitOrThrow(Target target) {
        TestConfiguration config = TestConfiguration.getInstance();
        return findElementWithWaitOrThrow(target, config.getExplicitWaitSeconds());
    }
    
    public WebElement findElementWithWaitOrThrow(Target target, int timeoutSeconds) {
        return findElementWithWait(target, timeoutSeconds)
                .orElseThrow(() -> new TimeoutException("Element not found within " + timeoutSeconds + " seconds: " + target));
    }
    
    // Find visible elements only
    public List<WebElement> findVisibleElements(Target target) {
        return findElements(target).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
    }
    
    public Optional<WebElement> findFirstVisibleElement(Target target) {
        return findVisibleElements(target).stream().findFirst();
    }
    
    // Find enabled elements only
    public List<WebElement> findEnabledElements(Target target) {
        return findElements(target).stream()
                .filter(WebElement::isEnabled)
                .collect(Collectors.toList());
    }
    
    public Optional<WebElement> findFirstEnabledElement(Target target) {
        return findEnabledElements(target).stream().findFirst();
    }
    
    // Find clickable elements (visible and enabled)
    public List<WebElement> findClickableElements(Target target) {
        return findElements(target).stream()
                .filter(element -> element.isDisplayed() && element.isEnabled())
                .collect(Collectors.toList());
    }
    
    public Optional<WebElement> findFirstClickableElement(Target target) {
        return findClickableElements(target).stream().findFirst();
    }
    
    // Find by text content
    public Optional<WebElement> findElementByText(Target target, String text) {
        return findElements(target).stream()
                .filter(element -> text.equals(element.getText()))
                .findFirst();
    }
    
    public Optional<WebElement> findElementByPartialText(Target target, String partialText) {
        return findElements(target).stream()
                .filter(element -> element.getText().contains(partialText))
                .findFirst();
    }
    
    // Find by attribute
    public Optional<WebElement> findElementByAttribute(Target target, String attribute, String value) {
        return findElements(target).stream()
                .filter(element -> value.equals(element.getAttribute(attribute)))
                .findFirst();
    }
    
    public List<WebElement> findElementsByAttribute(Target target, String attribute, String value) {
        return findElements(target).stream()
                .filter(element -> value.equals(element.getAttribute(attribute)))
                .collect(Collectors.toList());
    }
    
    // Find by CSS class
    public List<WebElement> findElementsWithClass(Target target, String className) {
        return findElements(target).stream()
                .filter(element -> {
                    String classes = element.getAttribute("class");
                    return classes != null && classes.contains(className);
                })
                .collect(Collectors.toList());
    }
    
    // Find in specific context (within another element)
    public Optional<WebElement> findElementInContext(WebElement context, Target target) {
        try {
            By locator = getBy(target);
            WebElement element = context.findElement(locator);
            return Optional.of(element);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
    
    public List<WebElement> findElementsInContext(WebElement context, Target target) {
        By locator = getBy(target);
        return context.findElements(locator);
    }
    
    // Find form elements
    public Optional<WebElement> findFormElement(String formName, String elementName) {
        Target formTarget = Target.name(formName);
        Optional<WebElement> form = findElement(formTarget);
        
        if (form.isPresent()) {
            Target elementTarget = Target.name(elementName);
            return findElementInContext(form.get(), elementTarget);
        }
        return Optional.empty();
    }
    
    // Find select element and get Select wrapper
    public Optional<Select> findSelectElement(Target target) {
        return findElement(target)
                .filter(element -> "select".equalsIgnoreCase(element.getTagName()))
                .map(Select::new);
    }
    
    public Select findSelectElementOrThrow(Target target) {
        return findSelectElement(target)
                .orElseThrow(() -> new NoSuchElementException("Select element not found: " + target));
    }
    
    // Find table elements
    public Optional<WebElement> findTableCell(Target tableTarget, int row, int column) {
        Optional<WebElement> table = findElement(tableTarget);
        if (table.isEmpty()) {
            return Optional.empty();
        }
        
        try {
            Target cellTarget = Target.xpath(".//tr[" + (row + 1) + "]/td[" + (column + 1) + "]");
            return findElementInContext(table.get(), cellTarget);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<WebElement> findTableRow(Target tableTarget, int row) {
        Optional<WebElement> table = findElement(tableTarget);
        if (table.isEmpty()) {
            return List.of();
        }
        
        Target rowTarget = Target.xpath(".//tr[" + (row + 1) + "]/td");
        return findElementsInContext(table.get(), rowTarget);
    }
    
    public List<WebElement> findTableColumn(Target tableTarget, int column) {
        Optional<WebElement> table = findElement(tableTarget);
        if (table.isEmpty()) {
            return List.of();
        }
        
        Target columnTarget = Target.xpath(".//tr/td[" + (column + 1) + "]");
        return findElementsInContext(table.get(), columnTarget);
    }
    
    // Find parent/child elements
    public Optional<WebElement> findParent(WebElement element) {
        try {
            return Optional.of(element.findElement(By.xpath("./..")));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }
    
    public List<WebElement> findChildren(WebElement element) {
        return element.findElements(By.xpath("./*"));
    }
    
    public List<WebElement> findSiblings(WebElement element) {
        Optional<WebElement> parent = findParent(element);
        if (parent.isEmpty()) {
            return List.of();
        }
        
        return findChildren(parent.get()).stream()
                .filter(sibling -> !sibling.equals(element))
                .collect(Collectors.toList());
    }
    
    // Find by position
    public Optional<WebElement> findElementByIndex(Target target, int index) {
        List<WebElement> elements = findElements(target);
        if (index >= 0 && index < elements.size()) {
            return Optional.of(elements.get(index));
        }
        return Optional.empty();
    }
    
    public Optional<WebElement> findFirstElement(Target target) {
        return findElementByIndex(target, 0);
    }
    
    public Optional<WebElement> findLastElement(Target target) {
        List<WebElement> elements = findElements(target);
        if (!elements.isEmpty()) {
            return Optional.of(elements.get(elements.size() - 1));
        }
        return Optional.empty();
    }
    
    // Utility methods for element state checking
    public boolean isElementPresent(Target target) {
        return findElement(target).isPresent();
    }
    
    public boolean isElementVisible(Target target) {
        return findElement(target)
                .map(WebElement::isDisplayed)
                .orElse(false);
    }
    
    public boolean isElementEnabled(Target target) {
        return findElement(target)
                .map(WebElement::isEnabled)
                .orElse(false);
    }
    
    public boolean isElementClickable(Target target) {
        return findElement(target)
                .map(element -> element.isDisplayed() && element.isEnabled())
                .orElse(false);
    }
    
    public boolean isElementSelected(Target target) {
        return findElement(target)
                .map(WebElement::isSelected)
                .orElse(false);
    }
    
    // Get element information
    public String getElementText(Target target) {
        return findElement(target)
                .map(WebElement::getText)
                .orElse("");
    }
    
    public String getElementAttribute(Target target, String attributeName) {
        return findElement(target)
                .map(element -> element.getAttribute(attributeName))
                .orElse("");
    }
    
    public String getElementValue(Target target) {
        return getElementAttribute(target, "value");
    }
    
    public String getElementTagName(Target target) {
        return findElement(target)
                .map(WebElement::getTagName)
                .orElse("");
    }
    
    // Helper method to convert Target to By
    private By getBy(Target target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        return switch (target.getBy()) {
            case "id" -> By.id(target.getValue());
            case "name" -> By.name(target.getValue());
            case "css" -> By.cssSelector(target.getValue());
            case "xpath" -> By.xpath(target.getValue());
            case "className" -> By.className(target.getValue());
            case "tagName" -> By.tagName(target.getValue());
            case "linkText" -> By.linkText(target.getValue());
            case "partialLinkText" -> By.partialLinkText(target.getValue());
            default -> throw new IllegalArgumentException("Unknown locator type: " + target.getBy());
        };
    }
}