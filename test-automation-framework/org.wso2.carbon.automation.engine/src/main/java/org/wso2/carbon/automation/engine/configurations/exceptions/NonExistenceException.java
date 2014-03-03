package org.wso2.carbon.automation.engine.configurations.exceptions;

public class NonExistenceException extends Exception {
    public NonExistenceException() {
        super();
    }

    public NonExistenceException(String contextModule, String message) {
        super(contextModule.concat(message));
    }

    public NonExistenceException(String message) {
        super(message);
    }
}