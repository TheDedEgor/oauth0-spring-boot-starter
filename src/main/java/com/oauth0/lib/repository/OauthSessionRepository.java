package com.oauth0.lib.repository;

import com.oauth0.lib.model.OauthSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthSessionRepository extends JpaRepository<OauthSession, String> {
}
