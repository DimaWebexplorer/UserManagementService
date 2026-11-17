package com.study.users.config;

import com.study.users.exceptions.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthenticatedReactiveAuthorizationManager;
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, SecurityResponseWriter writer,
                                                 UrlBasedCorsConfigurationSource corsSource) {
        var authenticated = AuthenticatedReactiveAuthorizationManager.authenticated();
        var administrator = AuthorityReactiveAuthorizationManager.hasRole("administrator");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .requestCache(cache -> cache.requestCache(NoOpServerRequestCache.getInstance()))
                .cors(cors -> cors.configurationSource(corsSource))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((exchange, e) -> writer.write(exchange, ErrorCode.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> writer.write(exchange, ErrorCode.ADMIN_ONLY)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/register", "/api/v1/users/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/users").access((authentication, context) ->
                                context.getExchange().getRequest().getQueryParams().containsKey("ids")
                                        ? authenticated.check(authentication, context)
                                        : administrator.check(authentication, context))
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/*/roles").hasRole("administrator")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/*/roles/*").hasRole("administrator")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((exchange, e) -> writer.write(exchange, ErrorCode.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> writer.write(exchange, ErrorCode.ADMIN_ONLY)))
                .build();
    }

    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        var hierarchy = RoleHierarchyImpl.fromHierarchy("""
                ROLE_administrator > ROLE_producer
                ROLE_producer > ROLE_subscriber
                """);
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                hierarchy.getReachableGrantedAuthorities(authorities.convert(jwt)));
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        configuration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
