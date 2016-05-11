package org.wso2.carbon.automation.engine.exceptions;

/**
 * DefaultInstanceSelectionException.
 */
public class DefaultInstanceSelectionException extends AutomationFrameworkException {
    public DefaultInstanceSelectionException(String contextModule, String message) {
        super(contextModule.concat(message));
    }

    public DefaultInstanceSelectionException(String message) {
        super(message);
    }
}
