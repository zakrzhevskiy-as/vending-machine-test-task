package com.sbt.pprb.qa.test_task.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {

    public InternalException(Throwable cause) {
        super(cause);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
