package com.sbt.pprb.qa.test_task.model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Ошибка при обработке запроса")
public class FakeCoinException extends RuntimeException {

}
