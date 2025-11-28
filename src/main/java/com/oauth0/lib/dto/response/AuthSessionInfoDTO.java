package com.oauth0.lib.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class AuthSessionInfoDTO {
    private String uuid;
    private ZonedDateTime expiredAt;
}
