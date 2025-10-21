package com.hashjosh.identity.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
    private String secret;
    private Long accessTokenExpirationMs;
    private Long accessTokenExpirationRememberMeMs;
    private Long refreshTokenExpirationMs;
    private Long refreshTokenExpirationRememberMeMs;
    private Long webSocketExpirationMs;

}
