package runner.model.step.waiting;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class WaitActionStep extends TestStep {
    private Target target;
    private Integer timeout;
    private WaitType waitType = WaitType.VISIBLE; // Default to VISIBLE
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public void setWaitType(WaitType waitType) {
        this.waitType = waitType;
    }
    
    public static WaitActionStep create(Target target) {
        WaitActionStep step = new WaitActionStep();
        step.setTarget(target);
        return step;
    }
    
    public static WaitActionStep create(Target target, WaitType waitType) {
        WaitActionStep step = new WaitActionStep();
        step.setTarget(target);
        step.setWaitType(waitType);
        return step;
    }
    
    public static WaitActionStep create(Target target, WaitType waitType, Integer timeout) {
        WaitActionStep step = new WaitActionStep();
        step.setTarget(target);
        step.setWaitType(waitType);
        step.setTimeout(timeout);
        return step;
    }
}
