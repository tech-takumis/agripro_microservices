package com.hashjosh.jwtshareable.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
@Data
public class JwtProperties {
    private String secret;
    private long accessTokenExpirationMs;
    private long accessTokenExpirationRememberMeMs;
    private long refreshTokenExpirationMs;
    private long refreshTokenExpirationRememberMeMs;
    private long webSocketExpirationMs;

}

