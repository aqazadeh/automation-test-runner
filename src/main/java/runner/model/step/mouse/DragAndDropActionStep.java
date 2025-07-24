package runner.model.step.mouse;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class DragAndDropActionStep extends TestStep {
    private Target source;
    private Target destination;
    
    public void setSource(Target source) {
        this.source = source;
    }
    
    public void setDestination(Target destination) {
        this.destination = destination;
    }
    
    public static DragAndDropActionStep create(Target source, Target destination) {
        DragAndDropActionStep step = new DragAndDropActionStep();
        step.setSource(source);
        step.setDestination(destination);
        return step;
    }
}
