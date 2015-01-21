package org.wso2.carbon.automation.engine.exceptions;

public class AutomationFrameworkException extends Exception {
    public AutomationFrameworkException(String message) {
        super(message);
    }

    public AutomationFrameworkException(String message, Throwable e) {
        super(message, e);
    }

    public AutomationFrameworkException(Throwable e) {
        super(e);
    }

    public AutomationFrameworkException(StackTraceElement[] message) {
        super(message.toString());
    }
}
