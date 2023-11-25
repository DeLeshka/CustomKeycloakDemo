package ru.akondrikov.endpoint;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager.BearerTokenAuthenticator;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;
import org.keycloak.services.resource.RealmResourceProvider;

@RequiredArgsConstructor
public class MyEndpointResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    @Override
    public Object getResource() {
        return this;
    }

    /**
     * NOT RECOMMENDED TO USE UNSECURED METHODS, AS WE HAVE ACCESS TO SESSION AND CAN EXPOSE SENSITIVE INFORMATION FROM IT
     */
    @GET
    @Path("anon")
    @Produces(MediaType.APPLICATION_JSON)
    public Response anonymousMethod() {
        return Response.ok("You called an anonymous method, your token is not checked. Realm name: " +
                session.getContext().getRealm().getName()).build();
    }

    @GET
    @Path("auth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticatedMethod() {
        final AuthResult auth = getAuth();
        return Response.ok("You called authenticated method. Your username: " +
                auth.getUser().getUsername()).build();
    }

    private AuthResult getAuth() {
        final AuthResult authResult = new BearerTokenAuthenticator(session).authenticate();
        if (authResult == null) {
            throw new NotAuthorizedException("no token");
        } else if (authResult.getToken().getIssuedFor() == null) {
            throw new ForbiddenException("Token is not properly issued");
        }
        return authResult;
    }

    @Override

    public void close() {

    }
}
