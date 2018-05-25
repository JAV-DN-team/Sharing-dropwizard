package app;

import com.google.common.collect.ImmutableList;
import config.DropwizardConfiguration;
import config.OAuth2Config;
import health.OAuth2HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.jetty.HttpsConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import model.User;
import oauth2.auth.*;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;
import service.PilotService;
import service.UserService;
import thymeleaf.ThymeleafViewRenderer;
import web.OAuth2Resource;
import web.PilotResource;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import java.util.Map;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class DropwizardApplication extends Application<DropwizardConfiguration> {

    public static void main(String[] args) throws Exception {
        new DropwizardApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<DropwizardConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<DropwizardConfiguration>(ImmutableList.of(new ThymeleafViewRenderer())) {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(final DropwizardConfiguration cfg) {
                return cfg.getViewConfiguration();
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/img", "/img", null, "img"));
    }

    @Override
    public void run(DropwizardConfiguration cfg, Environment env) throws Exception {

        // Datasource cfg
        final DataSource dataSource = cfg.getDataSourceFactory().build(env.metrics(), "sql");
        DBI dbi = new DBI(dataSource);

        final Client client = new RestClientBuilder(env, cfg).build(getName());

        // Health check for oauth2 server presence
        final OAuth2HealthCheck healthCheck = new OAuth2HealthCheck(cfg.getOauth2Config(), client);
        env.healthChecks().register("Oauth2 server", healthCheck);

        // Register the oauth2 resource that handles client authentication
        registerOAuth2(env, cfg, client, dbi);

        // Register resources (note: resource like action/controller)
        registerResouces(env, dbi);
    }

    private void registerResouces(Environment env, DBI dbi) {
        env.jersey().register(new PilotResource(dbi.onDemand(PilotService.class)));
    }

    private void registerOAuth2(Environment env, DropwizardConfiguration cfg, Client client, DBI dbi) throws Exception {

        OAuth2Config oauth2Config = cfg.getOauth2Config();

        // Setting up the oauth2 authenticator
        CookieEncrypter cookieEncrypter = new CookieEncrypter(oauth2Config.getCookieSecretKey());
        Boolean https = ((DefaultServerFactory) cfg.getServerFactory()).getApplicationConnectors().get(0) instanceof HttpsConnectorFactory;
        cookieEncrypter.setSecureFlag(https);

        OAuth2Authenticator authenticator = new OAuth2Authenticator(oauth2Config, client);

        // Using cache authenticator
        CachingAuthenticator<OAuth2Credentials, User> cachingAuthenticator =
                new CachingAuthenticator<>(env.metrics(), authenticator, oauth2Config.getCacheSpec());

        final OAuth2AuthFilter<User> oAuth2AuthFilter =
                new OAuth2AuthFilter.Builder<OAuth2Credentials, User, OAuth2AuthFilter<User>, CachingAuthenticator<OAuth2Credentials, User>>()
                        .setAuthenticator(cachingAuthenticator)
                        .setAuthorizer(new OAuth2Authorizer())
                        .setCookieEncrypter(cookieEncrypter)
                        .setPrefix("Bearer")
                        .build();

        env.jersey().register(new AuthDynamicFeature(oAuth2AuthFilter));
        env.jersey().register(RolesAllowedDynamicFeature.class);
        env.jersey().register(new OAuth2Resource(client, oauth2Config, cookieEncrypter, dbi.onDemand(UserService.class)));

        // If you want to use @Auth to inject a custom Principal type into your resource
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }
}
