package com.community.controller;

import cn.hutool.core.util.StrUtil;
import com.community.ai.dto.AIChatRequest;
import com.community.ai.service.AIService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/ai/chat")
public class AIChatController {

    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";
    private static final long CHAT_TIMEOUT_MS = 180_000L;

    @Autowired
    private AIService aiService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamChat(@RequestBody AIChatRequest request, HttpServletRequest httpRequest) {
        String question = request == null ? null : StrUtil.trim(request.getQuestion());
        if (StrUtil.isBlank(question)) {
            return ResponseEntity.badRequest().body(null);
        }

        String traceId = StrUtil.blankToDefault(httpRequest.getHeader(AI_TRACE_ID_HEADER), "unknown");
        long startTime = System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(CHAT_TIMEOUT_MS);
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();
        AtomicInteger chunkCounter = new AtomicInteger(0);

        Runnable cleanup = () -> {
            Disposable disposable = subscriptionRef.get();
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        };

        emitter.onTimeout(() -> {
            logger.warn("[ai-chat][backend] traceId={} stage=timeout costMs={}",
                    traceId,
                    System.currentTimeMillis() - startTime);
            sendEvent(emitter, "error", "\u804a\u5929\u54cd\u5e94\u8d85\u65f6\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
            cleanup.run();
            emitter.complete();
        });

        emitter.onCompletion(cleanup);
        emitter.onError(error -> cleanup.run());

        logger.info("[ai-chat][backend] traceId={} stage=controller-enter questionChars={}",
                traceId,
                question.length());

        CompletableFuture.runAsync(() -> {
            sendEvent(emitter, "start", "[START]");

            Disposable disposable = aiService.askQuestion(question).subscribe(
                    chunk -> {
                        int chunkIndex = chunkCounter.incrementAndGet();
                        logger.info("[ai-chat][backend] traceId={} stage=chunk index={} chunkChars={} costMs={}",
                                traceId,
                                chunkIndex,
                                chunk == null ? 0 : chunk.length(),
                                System.currentTimeMillis() - startTime);
                        if (!sendEvent(emitter, "message", chunk)) {
                            cleanup.run();
                        }
                    },
                    error -> {
                        logger.error("[ai-chat][backend] traceId={} stage=stream-error costMs={}",
                                traceId,
                                System.currentTimeMillis() - startTime,
                                error);
                        sendEvent(emitter, "error", "\u667a\u80fd\u52a9\u624b\u6682\u65f6\u65e0\u6cd5\u56de\u590d\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
                        cleanup.run();
                        emitter.complete();
                    },
                    () -> {
                        logger.info("[ai-chat][backend] traceId={} stage=controller-exit costMs={} success=true chunkCount={}",
                                traceId,
                                System.currentTimeMillis() - startTime,
                                chunkCounter.get());
                        sendEvent(emitter, "done", "[DONE]");
                        cleanup.run();
                        emitter.complete();
                    }
            );
            subscriptionRef.set(disposable);
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header("X-Accel-Buffering", "no")
                .header("Cache-Control", "no-cache, no-transform")
                .body(emitter);
    }

    private boolean sendEvent(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
            return true;
        } catch (IOException exception) {
            emitter.completeWithError(exception);
            return false;
        }
    }
}
