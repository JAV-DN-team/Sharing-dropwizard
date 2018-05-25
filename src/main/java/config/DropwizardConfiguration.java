package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jetty.ConnectorFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class DropwizardConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty("view")
    private Map<String, Map<String, String>> viewConfiguration = new HashMap<>();

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @Valid
    @NotNull
    @JsonProperty
    private ConnectorFactory clientConfig;

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private OAuth2Config oauth2Config = new OAuth2Config();


    //-------------------------------------------------------------------------------------------

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public Map<String, Map<String, String>> getViewConfiguration() {
        return viewConfiguration;
    }

    public ConnectorFactory getClientConfig() {
        return clientConfig;
    }

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    public OAuth2Config getOauth2Config() {
        return oauth2Config;
    }
}
