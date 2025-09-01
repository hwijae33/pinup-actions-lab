package kr.co.pinup.postLikes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.pinup.members.Member;
import kr.co.pinup.members.model.dto.MemberInfo;
import kr.co.pinup.members.repository.MemberRepository;
import kr.co.pinup.oauth.OAuthProvider;
import kr.co.pinup.oauth.OAuthToken;
import kr.co.pinup.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestLoginController {

    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    @PostMapping("/test-login")
    public ResponseEntity<Map<String, String>> internalLogin(
            @RequestParam String nickname,
            @RequestParam OAuthProvider provider,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        long start = System.currentTimeMillis();


        // 1. 회원 조회
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 2. MemberInfo 생성
        MemberInfo memberInfo = new MemberInfo(member.getNickname(), provider, member.getRole());

        // 3. 가짜 토큰 생성
        OAuthToken fakeToken = new OAuthToken() {
            private final String accessToken = "test-access-token-" + UUID.randomUUID();
            private final String refreshToken = "test-refresh-token";

            public String getAccessToken() { return accessToken; }
            public String getRefreshToken() { return refreshToken; }
        };

        // 4. 인증 처리
        securityUtil.setAuthentication(fakeToken, memberInfo);
        securityUtil.setRefreshTokenToCookie(response, fakeToken.getRefreshToken());

        // ✅ SecurityContext를 세션에 저장
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        // 5. 응답 반환
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", fakeToken.getAccessToken());
        result.put("refreshToken", fakeToken.getRefreshToken());
        long duration = System.currentTimeMillis() - start;
        log.info("🧪 [LOGIN] 처리 시간 = {}ms", duration);
        return ResponseEntity.ok(result);
    }

}
