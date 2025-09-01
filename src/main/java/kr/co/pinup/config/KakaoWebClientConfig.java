package kr.co.pinup.config;

import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import kr.co.pinup.custom.secretManager.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KakaoWebClientConfig {

    private final SecretsManagerService secretsManagerService;

    @PostConstruct
    @Profile("local")
    public void printKeys() {
        try {
            String restKey = secretsManagerService.getField("local/api/kakaomap", "kakao.api.key.rest");
            String jsKey = secretsManagerService.getField("local/api/kakaomap", "kakao.api.key.js");
            log.info("🔑 kakaoRestKey = {}", restKey);
            log.info("🔑 kakaoJsKey   = {}", jsKey);
        } catch (Exception e) {
            log.warn("⚠ Kakao REST Key 를 가져오는 데 실패했습니다: {}", e.getMessage());
        }
    }

    @Bean
    public WebClient kakaoWebClient() {
        String restKey = secretsManagerService.getField("local/api/kakaomap", "kakao.api.key.rest");
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader("Authorization", "KakaoAK " + restKey)
                .build();
    }
}