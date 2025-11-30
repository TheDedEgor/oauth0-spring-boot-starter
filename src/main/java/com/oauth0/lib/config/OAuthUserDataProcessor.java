package com.oauth0.lib.config;

import com.oauth0.lib.dto.response.UserDTO;

public interface OAuthUserDataProcessor {
    void save(UserDTO userDTO);
}
