package kr.co.pinup.custom.secretManager.exception;

import kr.co.pinup.exception.GlobalCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SecretNotFoundException extends GlobalCustomException {

    private static final String DEFAULT_MESSAGE = "요청한 시크릿을 찾을 수 없습니다.";

    public SecretNotFoundException() {
        this(DEFAULT_MESSAGE);
    }

    public SecretNotFoundException(String message) {
        super(message);
    }

    public SecretNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected int getHttpStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}