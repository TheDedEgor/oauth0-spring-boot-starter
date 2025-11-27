package com.oauth0.lib.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthSessionTimeDTO {
    private Long lifetimeSeconds = 300L;
}
