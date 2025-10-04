package com.hashjosh.jwtshareable.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessTokenExpirationMs;
    private long accessTokenExpirationRememberMeMs;
    private long refreshTokenExpirationMs;
    private long refreshTokenExpirationRememberMeMs;
}

