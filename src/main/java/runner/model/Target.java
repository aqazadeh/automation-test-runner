package runner.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Target {
    private String by;
    private String value;
    
    public static Target create(String by, String value) {
        Target target = new Target();
        target.setBy(by);
        target.setValue(value);
        return target;
    }
    
    public static Target id(String value) {
        return create("id", value);
    }
    
    public static Target name(String value) {
        return create("name", value);
    }
    
    public static Target css(String value) {
        return create("css", value);
    }
    
    public static Target xpath(String value) {
        return create("xpath", value);
    }
    
    public static Target className(String value) {
        return create("className", value);
    }
    
    public static Target tagName(String value) {
        return create("tagName", value);
    }
    
    public static Target linkText(String value) {
        return create("linkText", value);
    }
    
    public static Target partialLinkText(String value) {
        return create("partialLinkText", value);
    }
}
