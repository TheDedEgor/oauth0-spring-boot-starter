package com.oauth0.lib.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationEventPublisher {
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String uuid) {
        // Таймаут 30 секунд
        var emitter = new SseEmitter(30_000L);

        this.userEmitters.put(uuid, emitter);

        emitter.onCompletion(() -> {
            System.out.println("SSE completed for user: " + uuid);
            userEmitters.remove(uuid);
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE timeout for user: " + uuid);
            userEmitters.remove(uuid);
        });

        emitter.onError((e) -> {
            System.out.println("SSE error for user: " + uuid + ": " + e.getMessage());
            userEmitters.remove(uuid);
        });

        return emitter;
    }

    public void publish(String uuid) {
        SseEmitter emitter = userEmitters.get(uuid);
        if (emitter != null) {
            try {
                // Отправляем событие клиенту
                emitter.send(SseEmitter.event()
                    .name("auth-success"));

                // Завершаем соединение после отправки
                emitter.complete();

                // Удаляем из мапы
                userEmitters.remove(uuid);
            } catch (Exception e) {
                System.err.println("Error sending SSE event: " + e.getMessage());
                userEmitters.remove(uuid);
            }
        } else {
            System.out.println("No active SSE connection for user: " + uuid);
        }
    }
}
