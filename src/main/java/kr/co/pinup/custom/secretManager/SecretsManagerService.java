package kr.co.pinup.custom.secretManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pinup.custom.secretManager.exception.SecretNotFoundException;
import kr.co.pinup.custom.secretManager.exception.SecretParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

import java.io.IOException;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class SecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> getSecretMap(String secretName) {

        GetSecretValueResponse response;
        try {
            response = secretsManagerClient.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretName).build()
            );
        } catch (ResourceNotFoundException e) {
            throw new SecretNotFoundException(" 시크릿 [" + secretName + "] 을 찾을 수 없습니다.");
        }

        try {
            return mapper.readValue(response.secretString(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new SecretParseException(" 시크릿 [" + secretName + "] 파싱 중 오류 발생", e);
        }
    }

    public String getField(String secretName, String key) {
        Map<String, String> secretMap = getSecretMap(secretName);
        String value = secretMap.get(key);

        if (value == null || value.isBlank()) {
            throw new SecretNotFoundException(" 시크릿 [" + secretName + "]에서 키 [" + key + "]를 찾을 수 없습니다.");
        }

        return value;
    }
}

