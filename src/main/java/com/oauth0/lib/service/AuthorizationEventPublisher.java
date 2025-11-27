package com.oauth0.lib.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Log4j2
public class AuthorizationEventPublisher {
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Void>> completionFutures = new ConcurrentHashMap<>();

    private final OauthService oauthService;

    public AuthorizationEventPublisher(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    public SseEmitter subscribe(String uuid) {
        var timeout = oauthService.getSecondsBeforeExpiredTime(uuid);
        var emitter = new SseEmitter(timeout * 1000);

        var completionFuture = new CompletableFuture<Void>();
        userEmitters.put(uuid, emitter);
        completionFutures.put(uuid, completionFuture);

        emitter.onCompletion(() -> {
            log.debug("SSE completed for uuid: {}", uuid);
            userEmitters.remove(uuid);
            completionFuture.complete(null);
            completionFutures.remove(uuid);
            oauthService.removeValidUntil(uuid);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE timed out for uuid: {}", uuid);
            userEmitters.remove(uuid);
            completionFuture.completeExceptionally(new TimeoutException("SSE connection timed out"));
            completionFutures.remove(uuid);
            oauthService.removeValidUntil(uuid);
        });

        emitter.onError((e) -> {
            log.error("SSE error for uuid: {}", uuid, e);
            userEmitters.remove(uuid);
            completionFuture.completeExceptionally(e);
            completionFutures.remove(uuid);
            oauthService.removeValidUntil(uuid);
        });

        return emitter;
    }

    public CompletableFuture<Void> publish(String uuid) {
        SseEmitter emitter = userEmitters.get(uuid);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("auth-success").data(""));
                return completionFutures.get(uuid);
            } catch (Exception e) {
                log.error("SSE error for uuid: {}", uuid, e);
                // Если отправка не удалась, нужно сразу "провалить" сигнал
                CompletableFuture<Void> future = completionFutures.remove(uuid);
                if (future != null) {
                    future.completeExceptionally(e);
                }
                userEmitters.remove(uuid);
                oauthService.removeValidUntil(uuid);
                return CompletableFuture.failedFuture(e); // Возвращаем уже проваленный Future
            }
        } else {
            log.error("SSE event not found for uuid: {}", uuid);
            return CompletableFuture.failedFuture(new IllegalStateException("No active SSE connection for user: " + uuid));
        }
    }

    public void complete(String uuid) {
        var emitter = userEmitters.get(uuid);
        if (emitter == null) {
            throw new IllegalStateException("No active SSE connection for uuid: " + uuid);
        }
        emitter.complete();
    }
}
