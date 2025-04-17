package runner.model;

import java.util.List;
import java.util.Map;

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
    // Yeni alanlar
    public Target sourceTarget;     // Sürükle bırak için kaynak hedef
    public Target destinationTarget; // Sürükle bırak için varış hedefi
    public String attribute;        // getAttribute için özellik adı
    public String script;           // JavaScript kodu çalıştırmak için
    public Map<String, Object> scriptArgs; // JavaScript argümanları
    public String frameIdentifier;  // frame ID, name veya index
    public String windowHandle;     // Pencere tanımlayıcısı
    public String windowTitleOrUrl; // Pencere başlığı veya URL'si
    public String alertAction;      // alert aksiyonu: accept, dismiss, sendKeys
    public String key;              // Klavye tuşu aksiyonları için

    public static class Target {
        public String by;
        public String value;

        @Override
        public String toString() {
            return "Target{by='" + by + "', value='" + value + "'}";
        }
    }

    public static class Condition {
        public Target target;
        public String type;
        public String jsCondition; // JavaScript şartı

        @Override
        public String toString() {
            return "Condition{target=" + target + ", type='" + type + "', jsCondition='" + jsCondition + "'}";
        }
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

    public Target getSourceTarget() {
        return sourceTarget;
    }

    public void setSourceTarget(Target sourceTarget) {
        this.sourceTarget = sourceTarget;
    }

    public Target getDestinationTarget() {
        return destinationTarget;
    }

    public void setDestinationTarget(Target destinationTarget) {
        this.destinationTarget = destinationTarget;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, Object> getScriptArgs() {
        return scriptArgs;
    }

    public void setScriptArgs(Map<String, Object> scriptArgs) {
        this.scriptArgs = scriptArgs;
    }

    public String getFrameIdentifier() {
        return frameIdentifier;
    }

    public void setFrameIdentifier(String frameIdentifier) {
        this.frameIdentifier = frameIdentifier;
    }

    public String getWindowHandle() {
        return windowHandle;
    }

    public void setWindowHandle(String windowHandle) {
        this.windowHandle = windowHandle;
    }

    public String getWindowTitleOrUrl() {
        return windowTitleOrUrl;
    }

    public void setWindowTitleOrUrl(String windowTitleOrUrl) {
        this.windowTitleOrUrl = windowTitleOrUrl;
    }

    public String getAlertAction() {
        return alertAction;
    }

    public void setAlertAction(String alertAction) {
        this.alertAction = alertAction;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
                ", sourceTarget=" + sourceTarget +
                ", destinationTarget=" + destinationTarget +
                ", attribute='" + attribute + '\'' +
                ", script='" + script + '\'' +
                ", frameIdentifier='" + frameIdentifier + '\'' +
                ", windowHandle='" + windowHandle + '\'' +
                ", alertAction='" + alertAction + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
