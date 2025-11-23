package com.oauth0.lib.dto.response;

import com.oauth0.lib.enums.AuthLinkType;
import lombok.*;

@Data
@AllArgsConstructor
public class AuthLinkDTO {
    private String link;
    private AuthLinkType type;
}
