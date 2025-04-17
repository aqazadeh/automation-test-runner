package runner.model;

import java.util.List;

public class TestStep {
    public String action;
    public Target target;
    public String value;
    public String url;
    public Integer timeout;
    public Boolean continueOnFailure;
    public Condition condition;
    public List<TestStep> steps;
    public Integer times;

    public static class Target {
        public String by;
        public String value;
    }

    public static class Condition {
        public Target target;
        public String type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getContinueOnFailure() {
        return continueOnFailure;
    }

    public void setContinueOnFailure(Boolean continueOnFailure) {
        this.continueOnFailure = continueOnFailure;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TestStep> steps) {
        this.steps = steps;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "action='" + action + '\'' +
                ", target=" + target +
                ", value='" + value + '\'' +
                ", url='" + url + '\'' +
                ", timeout=" + timeout +
                ", continueOnFailure=" + continueOnFailure +
                ", condition=" + condition +
                ", steps=" + steps +
                ", times=" + times +
                '}';
    }
}
