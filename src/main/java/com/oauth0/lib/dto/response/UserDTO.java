package com.oauth0.lib.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    // Идентификатор сессии
    private String uuid;
    // Данные пользователя
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}
