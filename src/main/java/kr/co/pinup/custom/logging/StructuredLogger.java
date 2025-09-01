package kr.co.pinup.custom.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pinup.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class StructuredLogger {

    private final Logger logger = LoggerFactory.getLogger(StructuredLogger.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> getBaseFields(String level) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("logLevel", level);
        fields.put("timestamp", Instant.now().toString());
        fields.put("userNickName", MDC.get("userNickName"));
        fields.put("requestId", MDC.get("requestId"));
        fields.put("className", MDC.get("className"));
        fields.put("methodName", MDC.get("methodName"));
        fields.put("targetId", MDC.get("targetId"));
        return fields;
    }

    public void info(String message) {
        Map<String, Object> log = getBaseFields("INFO");
        log.put("message", message);
        logger.info(toJson(log));
    }

    public void warn(String message, Map<String, Object> additional) {
        Map<String, Object> log = getBaseFields("WARN");
        log.put("message", message);
        if (additional != null) log.putAll(additional);
        logger.warn(toJson(log));
    }

    public void error(String message, Throwable e, Map<String, Object> additional) {
        Map<String, Object> log = getBaseFields("ERROR");
        log.put("message", message);
        log.put("exceptionType", e.getClass().getSimpleName());
        log.put("stackTrace", getStackTrace(e));
        if (additional != null) log.putAll(additional);
        logger.error(toJson(log));
    }

    private String getStackTrace(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            sb.append(el.toString()).append("\n");
        }
        return sb.toString();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    public StructuredLogger withTarget(BaseEntity entity) {
        if (entity != null && entity.getId() != null) {
            MDC.put("targetId", String.valueOf(entity.getId()));
        }
        return this;
    }
}