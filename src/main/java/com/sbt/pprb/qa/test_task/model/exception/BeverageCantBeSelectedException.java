package com.sbt.pprb.qa.test_task.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Объема выбранного напитка недостаточно.")
public class BeverageCantBeSelectedException extends RuntimeException {

}
