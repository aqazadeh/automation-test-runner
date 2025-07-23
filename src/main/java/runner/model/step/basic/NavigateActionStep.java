package runner.model.step.basic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString
public class NavigateActionStep extends TestStep {
    private String url;
}
