package runner.executor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import runner.model.Target;

public abstract class ActionExecutor<T>{

    protected WebElement find(WebDriver driver, Target target) {
        return driver.findElement(getBy(target));
    }

    protected By getBy(Target target) {
        return switch (target.by) {
            case "id" -> By.id(target.value);
            case "name" -> By.name(target.value);
            case "css" -> By.cssSelector(target.value);
            case "xpath" -> By.xpath(target.value);
            case "className" -> By.className(target.value);
            case "tagName" -> By.tagName(target.value);
            case "linkText" -> By.linkText(target.value);
            case "partialLinkText" -> By.partialLinkText(target.value);
            default -> throw new IllegalArgumentException("Unknown locator: " + target.by);
        };
    }

    public abstract void execute(WebDriver driver, T step);
}
