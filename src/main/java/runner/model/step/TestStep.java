package runner.model.step;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.TestAction;
import runner.model.step.basic.*;
import runner.model.step.condition.ConditionActionStep;
import runner.model.step.window.CloseWindowActionStep;
import runner.model.step.window.SwitchToWindowActionStep;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "action",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NavigateActionStep.class, name = "NAVIGATE"),
        @JsonSubTypes.Type(value = ClickActionStep.class, name = "CLICK"),
        @JsonSubTypes.Type(value = SendValueActionStep.class, name = "SEND_VALUE"),
        @JsonSubTypes.Type(value = SelectOptionActionStep.class, name = "SELECT_OPTION"),
        @JsonSubTypes.Type(value = AssertTextActionStep.class, name = "ASSERT_TEXT"),
        @JsonSubTypes.Type(value = ScreenshootActionStep.class, name = "SCREENSHOT"),
        @JsonSubTypes.Type(value = SwitchToWindowActionStep.class, name = "SWITCH_TO_WINDOW"),
        @JsonSubTypes.Type(value = CloseWindowActionStep.class, name = "CLOSE_WINDOW"),
        @JsonSubTypes.Type(value = ConditionActionStep.class, name = "CONDITION"),
})
@Getter
@Setter
@ToString
public abstract class TestStep {
    private String name;
    private TestAction action;
}
