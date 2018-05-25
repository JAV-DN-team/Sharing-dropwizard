package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import javax.validation.constraints.NotNull;

/**
 * Created by FRAMGIA\dinh.thanh on 25/05/2018.
 */
public class OAuth2Config {

    @NotNull
    @JsonProperty
    private String url;

    @NotNull
    @JsonProperty
    private String scope;

    @NotNull
    @JsonProperty
    private String clientId;

    @NotNull
    @JsonProperty
    private String clientSecret;

    @NotNull
    @JsonProperty
    private String cookieSecretKey;

    @JsonProperty
    private CacheBuilderSpec cacheSpec;

    public String getUrl() {
        return url;
    }

    public String getScope() {
        return scope;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCookieSecretKey() {
        return cookieSecretKey;
    }

    public CacheBuilderSpec getCacheSpec() {
        return cacheSpec;
    }

}
