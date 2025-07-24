package runner.model.step.alert;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class AcceptAlertActionStep extends TestStep {
    private Integer timeout; // Optional timeout in seconds
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public static AcceptAlertActionStep create() {
        return new AcceptAlertActionStep();
    }
    
    public static AcceptAlertActionStep create(Integer timeout) {
        AcceptAlertActionStep step = new AcceptAlertActionStep();
        step.setTimeout(timeout);
        return step;
    }
}
