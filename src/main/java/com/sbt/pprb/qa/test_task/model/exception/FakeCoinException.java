package com.sbt.pprb.qa.test_task.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "Вы использовали поддельную монету. Заявление составлено и передано в отдел полиции."
)
public class FakeCoinException extends RuntimeException {

}
