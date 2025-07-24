package runner.model.step;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.step.alert.*;
import runner.model.step.basic.*;
import runner.model.step.browser.*;
import runner.model.step.condition.ConditionActionStep;
import runner.model.step.mouse.*;
import runner.model.step.waiting.WaitActionStep;
import runner.model.step.window.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "action",
        visible = true
)
@JsonSubTypes({
        // Basic Actions
        @JsonSubTypes.Type(value = NavigateActionStep.class, name = "NAVIGATE"),
        @JsonSubTypes.Type(value = ClickActionStep.class, name = "CLICK"),
        @JsonSubTypes.Type(value = SendValueActionStep.class, name = "SEND_VALUE"),
        @JsonSubTypes.Type(value = SelectOptionActionStep.class, name = "SELECT_OPTION"),
        @JsonSubTypes.Type(value = AssertTextActionStep.class, name = "ASSERT_TEXT"),
        @JsonSubTypes.Type(value = ScreenshootActionStep.class, name = "SCREENSHOT"),
        @JsonSubTypes.Type(value = ClearActionStep.class, name = "CLEAR"),
        
        // Browser Actions
        @JsonSubTypes.Type(value = ExecuteScriptActionStep.class, name = "EXECUTE_SCRIPT"),
        @JsonSubTypes.Type(value = ScrollToActionStep.class, name = "SCROLL_TO"),
        @JsonSubTypes.Type(value = WaitForJsActionStep.class, name = "WAIT_FOR_JS"),
        
        // Window Actions
        @JsonSubTypes.Type(value = SwitchToWindowActionStep.class, name = "SWITCH_TO_WINDOW"),
        @JsonSubTypes.Type(value = CloseWindowActionStep.class, name = "CLOSE_WINDOW"),
        @JsonSubTypes.Type(value = BackWindowActionStep.class, name = "BACK_WINDOW"),
        @JsonSubTypes.Type(value = ForwardWindowActionStep.class, name = "FORWARD_WINDOW"),
        @JsonSubTypes.Type(value = RefreshWindowActionStep.class, name = "REFRESH_WINDOW"),
        
        // Alert Actions
        @JsonSubTypes.Type(value = AcceptAlertActionStep.class, name = "ACCEPT_ALERT"),
        @JsonSubTypes.Type(value = DismissAlertActionStep.class, name = "DISMISS_ALERT"),
        @JsonSubTypes.Type(value = SendValueToAlertActionStep.class, name = "SEND_VALUE_TO_ALERT"),
        
        // Mouse Actions
        @JsonSubTypes.Type(value = HoverActionStep.class, name = "HOVER"),
        @JsonSubTypes.Type(value = RightClickActionStep.class, name = "RIGHT_CLICK"),
        @JsonSubTypes.Type(value = DoubleClickActionStep.class, name = "DOUBLE_CLICK"),
        @JsonSubTypes.Type(value = DragAndDropActionStep.class, name = "DRAG_AND_DROP"),
        
        // Waiting & Condition Actions
        @JsonSubTypes.Type(value = WaitActionStep.class, name = "WAIT"),
        @JsonSubTypes.Type(value = ConditionActionStep.class, name = "CONDITION"),
})
@Getter
@Setter
@ToString
public abstract class TestStep {
    private String name;
    private TestAction action;
}
