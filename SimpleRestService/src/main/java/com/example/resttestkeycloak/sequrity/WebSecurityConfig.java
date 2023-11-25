package com.example.resttestkeycloak.sequrity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.resttestkeycloak.constant.Roles.ADMIN;
import static com.example.resttestkeycloak.constant.Roles.USER;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http, final JwtAuthConverter jwtAuthConverter) throws Exception {
        http.authorizeHttpRequests((registry) ->
                registry.requestMatchers(HttpMethod.GET, "/anonymous/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole(ADMIN, USER)
                        .anyRequest().authenticated()
        );
        http.oauth2ResourceServer((security) ->
                security.jwt((jwt) ->
                        jwt.jwtAuthenticationConverter(jwtAuthConverter)
                )
        );
        http.sessionManagement((sessionManagementConfigurer) ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        return http.build();
    }

}
