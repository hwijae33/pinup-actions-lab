package kr.co.pinup.api.kakao.exception;

import kr.co.pinup.exception.GlobalCustomException;
import org.springframework.http.HttpStatus;

public class KakaoMapApiException extends GlobalCustomException {

  private static final String DEFAULT_MESSAGE = "카카오맵 API 호출에 실패했습니다.";

  public KakaoMapApiException(String message) {
    super(message);
  }

  public KakaoMapApiException() {
    super(DEFAULT_MESSAGE);
  }
  public KakaoMapApiException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  protected int getHttpStatusCode() {
    return HttpStatus.NOT_FOUND.value();
  }
}
