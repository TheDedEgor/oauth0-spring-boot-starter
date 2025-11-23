package com.oauth0.lib.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuthSessionInfoDTO {
    private String uuid;
    private LocalDateTime expiredAt;
}
