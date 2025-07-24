package runner.model.step.browser;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class WaitForJsActionStep extends TestStep {
    private String script;
    private int timeout = 10;
    private Object[] arguments;
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    public static WaitForJsActionStep create(String script) {
        WaitForJsActionStep step = new WaitForJsActionStep();
        step.setScript(script);
        return step;
    }
    
    public static WaitForJsActionStep create(String script, int timeout) {
        WaitForJsActionStep step = new WaitForJsActionStep();
        step.setScript(script);
        step.setTimeout(timeout);
        return step;
    }
    
    public static WaitForJsActionStep create(String script, int timeout, Object... arguments) {
        WaitForJsActionStep step = new WaitForJsActionStep();
        step.setScript(script);
        step.setTimeout(timeout);
        step.setArguments(arguments);
        return step;
    }
}
