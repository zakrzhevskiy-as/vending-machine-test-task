package com.sbt.pprb.qa.test_task.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Lack of selected beverage")
public class BeverageCantBeSelectedException extends RuntimeException {

    public BeverageCantBeSelectedException() {
        super();
    }

    public BeverageCantBeSelectedException(String message) {
        super(message);
    }

    public BeverageCantBeSelectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeverageCantBeSelectedException(Throwable cause) {
        super(cause);
    }

    protected BeverageCantBeSelectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
