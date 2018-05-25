package health;


import com.codahale.metrics.health.HealthCheck;
import config.DropwizardConfiguration;
import config.OAuth2Config;
import oauth2.apifest.ApifestApiPath;
import org.apache.http.HttpStatus;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OAuth2HealthCheck extends HealthCheck {

    private Client client;
    private String validationUrl;

    public OAuth2HealthCheck(OAuth2Config oauth2Config, Client client) {
        this.client = client;
        this.validationUrl = oauth2Config.getUrl() + ApifestApiPath.TOKENS_VALIDATION.getPath();
    }

    @Override
    protected Result check() throws Exception {
        WebTarget target = client.target(validationUrl);
        Response response = null;
        try {
            response = target.request().accept(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() != HttpStatus.SC_BAD_REQUEST)
                return Result.unhealthy("OAuth2 server bad response (err code: " + response.getStatus() + ")");
        } catch (Exception ex) {
            return Result.unhealthy("OAuth2 server is unreachable");
        } finally {
            if (response != null)
                response.close();
        }

        return Result.healthy();
    }
}