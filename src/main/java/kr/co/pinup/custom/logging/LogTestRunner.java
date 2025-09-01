package kr.co.pinup.custom.logging;

import lombok.RequiredArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LogTestRunner implements CommandLineRunner {

    private final StructuredLogger structuredLogger;

    @Override
    public void run(String... args) {
        // ✅ 수동으로 MDC 필드 설정 (테스트용)
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("userId", "test-user");
        MDC.put("timestamp", Instant.now().toString());

        try {
            // INFO 로그 테스트
            structuredLogger.info("✅ 테스트 로그 - INFO");

            // WARN 로그 테스트
            structuredLogger.warn("⚠️ 테스트 로그 - WARN", Map.of(
                    "warningType", "InvalidRequest",
                    "reason", "필수값 누락"
            ));

            // ERROR 로그 테스트 (예외 발생)
            throw new RuntimeException("테스트 예외 발생");

        } catch (Exception e) {
            structuredLogger.error("❌ 테스트 로그 - ERROR", e, Map.of(
                    "query", "SELECT * FROM test",
                    "responseTime", "123ms"
            ));
        } finally {
            MDC.clear(); // 필수
        }
    }
}