package org.integration.camelfoundation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
public class CamelRequestException extends RuntimeException {

    public CamelRequestException(String message) {
        super(message);
    }
}
