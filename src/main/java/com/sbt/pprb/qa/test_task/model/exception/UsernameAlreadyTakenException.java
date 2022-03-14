package com.sbt.pprb.qa.test_task.model.exception;

public class UsernameAlreadyTakenException extends RuntimeException {

    public UsernameAlreadyTakenException() {
        super();
    }

    public UsernameAlreadyTakenException(String username) {
        super("Username '" + username + "' already taken");
    }
}
