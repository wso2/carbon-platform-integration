package org.wso2.carbon.automation.engine.exceptions;

public class DefaultInstanceSelectionException extends AutomationFrameworkException {
    public DefaultInstanceSelectionException(String contextModule, String message) {
        super(contextModule.concat(message));
    }

    public DefaultInstanceSelectionException(StackTraceElement[] message) {
        super(message);
    }

}
