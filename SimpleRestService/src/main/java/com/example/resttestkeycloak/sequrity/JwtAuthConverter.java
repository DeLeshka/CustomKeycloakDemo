package com.example.resttestkeycloak.sequrity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtAuthConverterProperties properties;

    @Override
    public AbstractAuthenticationToken convert(@NonNull final Jwt jwt) {
        Set<GrantedAuthority> grantedAuthorities = Stream.concat(
                        jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                        extractRolesFromResource(jwt).stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, grantedAuthorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(final Jwt jwt) {
        return properties.getPrincipalAttribute() != null ?
                jwt.getClaim(properties.getPrincipalAttribute()) :
                jwt.getClaim(JwtClaimNames.SUB);
    }

    @SuppressWarnings("unchecked")
    private Set<? extends GrantedAuthority> extractRolesFromResource(final Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        if (resourceAccess == null ||
                (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null ||
                (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return Collections.emptySet();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }


}
