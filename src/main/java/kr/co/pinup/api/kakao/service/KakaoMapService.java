package kr.co.pinup.api.kakao.service;


import kr.co.pinup.api.kakao.exception.KakaoMapApiException;

import kr.co.pinup.api.kakao.model.dto.KakaoAddressDto;
import kr.co.pinup.api.kakao.model.dto.KakaoAddressSearchResponse;
import kr.co.pinup.locations.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Qualifier("kakaoWebClient")
    private final WebClient kakaoWebClient;

    public KakaoAddressDto getLocationFromAddress(String address) {
        try {
            KakaoAddressSearchResponse response = kakaoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            clientResponse -> Mono.error(new KakaoMapApiException("카카오 주소 API 응답 오류")))
                    .bodyToMono(KakaoAddressSearchResponse.class)
                    .block();

            if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
                throw new LocationNotFoundException("주소에 해당하는 좌표를 찾을 수 없습니다.");
            }

            return response.getDocuments().get(0);

        } catch (WebClientResponseException e) {
            log.error("카카오맵 API 호출 실패", e);
            throw new KakaoMapApiException("카카오맵 API 호출 중 오류 발생", e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생", e);
            throw new KakaoMapApiException("카카오맵 서비스 오류", e);
        }
    }
}
