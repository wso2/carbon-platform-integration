package org.wso2.carbon.automation.engine.exceptions;

public class AutomationFrameworkException extends Exception {
    public AutomationFrameworkException(String concat) {
        super();
    }

    public AutomationFrameworkException(StackTraceElement[] message) {
        super(message.toString());
    }
}
