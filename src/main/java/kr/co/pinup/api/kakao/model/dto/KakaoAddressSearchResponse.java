package kr.co.pinup.api.kakao.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class KakaoAddressSearchResponse {
    private List<KakaoAddressDto> documents;
}