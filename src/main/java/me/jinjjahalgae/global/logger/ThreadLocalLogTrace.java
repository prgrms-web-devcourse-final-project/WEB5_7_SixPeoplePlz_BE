package me.jinjjahalgae.global.logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RequiredArgsConstructor
public class ThreadLocalLogTrace implements LogTrace{
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private final LogVersionInfo logVersionInfo;
    private final HttpServletRequest request;

    private final ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        // 서비스 버전, 배포 정보, 요청 정보 등 구조적으로 한 줄에 남기기
        log.info(combineLogs(
                traceId.getId(),
                LogConstants.LOG_VERSION, logVersionInfo.version(),
                LogConstants.LOG_METHOD, message,
                LogConstants.LOG_REQUEST_URI, request != null ? request.getRequestURI() : "N/A",
                LogConstants.LOG_REQUEST_METHOD, request != null ? request.getMethod() : "N/A",
                LogConstants.LOG_USER_SESSION, request != null ? getSessionId(request) : "N/A",
                LogConstants.LOG_INVOKE_TIME, startTimeMs
        ));
        return new TraceStatus(traceId, startTimeMs, message);
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        // 예외 발생 시 스택 트레이스 전체를 ERROR로 남기고, 일반 흐름은 INFO로 남기기
        if (e == null) {
            log.info(combineLogs(
                    traceId.getId(),
                    LogConstants.LOG_VERSION, logVersionInfo.version(),
                    LogConstants.LOG_METHOD, status.getMessage(),
                    LogConstants.LOG_REQUEST_URI, request != null ? request.getRequestURI() : "N/A",
                    LogConstants.LOG_REQUEST_METHOD, request != null ? request.getMethod() : "N/A",
                    LogConstants.LOG_USER_SESSION, request != null ? getSessionId(request) : "N/A",
                    LogConstants.LOG_STATUS, "SUCCESS",
                    LogConstants.LOG_ELAPSED_MS, resultTimeMs,
                    LogConstants.LOG_INVOKE_TIME, status.getStartTimeMs()
            ));
        } else {
            log.error(combineLogs(
                    traceId.getId(),
                    LogConstants.LOG_VERSION, logVersionInfo.version(),
                    LogConstants.LOG_METHOD, status.getMessage(),
                    LogConstants.LOG_REQUEST_URI, request != null ? request.getRequestURI() : "N/A",
                    LogConstants.LOG_REQUEST_METHOD, request != null ? request.getMethod() : "N/A",
                    LogConstants.LOG_USER_SESSION, request != null ? getSessionId(request) : "N/A",
                    LogConstants.LOG_STATUS, "FAILED",
                    LogConstants.LOG_ELAPSED_MS, resultTimeMs,
                    LogConstants.LOG_INVOKE_TIME, status.getStartTimeMs(),
                    LogConstants.LOG_EXCEPTION, getFullStackTrace(e)
            ));
        }
        releaseTraceId();
    }

    private String combineLogs(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i += 2) {
            if (i > 0) sb.append(" ");
            sb.append(args[i]).append("=").append(args[i + 1]);
        }
        return sb.toString();
    }

    private String getFullStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : "N/A";
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
