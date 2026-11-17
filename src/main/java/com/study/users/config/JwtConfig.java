package com.study.users.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Configuration
public class JwtConfig {

    @Bean
    SecretKey jwtSecretKey(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must contain at least 32 bytes");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(SecretKey jwtSecretKey, @Value("${app.jwt.issuer}") String issuer) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer), this::validateClaims));
        return decoder;
    }

    private OAuth2TokenValidatorResult validateClaims(Jwt jwt) {
        Object userId = jwt.getClaims().get("user_id");
        Object roles = jwt.getClaims().get("roles");
        Set<String> knownRoles = Set.of("subscriber", "producer", "administrator");
        try {
            int subjectId = Integer.parseInt(jwt.getSubject());
            if (subjectId > 0 && userId instanceof Number number && number.longValue() == subjectId
                    && jwt.getExpiresAt() != null && roles instanceof List<?> list
                    && list.stream().allMatch(role -> role instanceof String name && knownRoles.contains(name))) {
                return OAuth2TokenValidatorResult.success();
            }
        } catch (NumberFormatException ignored) {
            // Некорректный subject также означает невалидный токен.
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
    }
}
