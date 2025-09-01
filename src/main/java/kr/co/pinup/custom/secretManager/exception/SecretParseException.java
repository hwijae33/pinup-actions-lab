package kr.co.pinup.custom.secretManager.exception;

import kr.co.pinup.exception.GlobalCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SecretParseException extends GlobalCustomException {

    private static final String DEFAULT_MESSAGE = "시크릿 값을 JSON으로 파싱하는 데 실패했습니다.";

    public SecretParseException() {
        this(DEFAULT_MESSAGE);
    }

    public SecretParseException(String message) {
        super(message);
    }

    public SecretParseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected int getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}