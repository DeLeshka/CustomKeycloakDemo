package ru.akondrikov.endpoint;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class MyEndpointResourceProviderFactory implements RealmResourceProviderFactory {

    private static final String PROVIDER_ID = "my-rest-provider";

    @Override
    public RealmResourceProvider create(final KeycloakSession keycloakSession) {
        return new MyEndpointResourceProvider(keycloakSession);
    }

    @Override
    public void init(final Config.Scope scope) {

    }

    @Override
    public void postInit(final KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
