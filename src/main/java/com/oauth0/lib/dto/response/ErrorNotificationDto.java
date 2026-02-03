package com.oauth0.lib.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter @NoArgsConstructor
public class ErrorNotificationDto {
    private String sessionId;
    private String type;
    private String message;
    private ZonedDateTime timestamp;
}
