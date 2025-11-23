package com.oauth0.lib.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oauth_sessions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sessionId", "providerId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthSession {

    @Id
    private String sessionId;

    private Long providerId;
}
