package runner.model.step.basic;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ScreenshootActionStep extends TestStep {
    private String name;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public static ScreenshootActionStep create(String name) {
        ScreenshootActionStep step = new ScreenshootActionStep();
        step.setName(name);
        return step;
    }
    
    public static ScreenshootActionStep create() {
        return new ScreenshootActionStep();
    }
}
