package com.oauth0.lib.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthSessionTimeDTO {
    private Boolean permanent = false;
    private Long lifetimeSeconds = 300L;
}
