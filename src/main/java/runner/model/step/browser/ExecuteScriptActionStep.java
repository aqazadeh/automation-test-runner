package runner.model.step.browser;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ExecuteScriptActionStep extends TestStep {
    private String script;
    private Object[] arguments;
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    public static ExecuteScriptActionStep create(String script) {
        ExecuteScriptActionStep step = new ExecuteScriptActionStep();
        step.setScript(script);
        return step;
    }
    
    public static ExecuteScriptActionStep create(String script, Object... arguments) {
        ExecuteScriptActionStep step = new ExecuteScriptActionStep();
        step.setScript(script);
        step.setArguments(arguments);
        return step;
    }
}
