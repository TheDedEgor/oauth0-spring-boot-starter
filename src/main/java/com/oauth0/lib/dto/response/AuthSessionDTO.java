package com.oauth0.lib.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthSessionDTO {
    private List<AuthLinkDTO> links;
    private String sessionId;
    private LocalDateTime validUntil;
}
