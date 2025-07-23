package runner.model.step.mouse;

import lombok.Getter;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
public class DragAndDropActionStep extends TestStep {
    private Target source;
    private Target destination;
}
