package oauth2.auth;

import config.DropwizardConfiguration;
import config.OAuth2Config;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import model.User;
import oauth2.apifest.AccessToken;
import oauth2.apifest.ApifestApiPath;
import oauth2.apifest.TokenValidationResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Takes the credentials extracted from the request by the SecurityProvider
 * and authenticates the principal to the oauth2 server.
 *
 * @author Edouard De Oliveira
 */
public class OAuth2Authenticator implements Authenticator<OAuth2Credentials, User> {

    private static final Logger log = LoggerFactory.getLogger(OAuth2Authenticator.class);
    private Client client;
    private String validationUrl;

    public OAuth2Authenticator(OAuth2Config oauth2Config, Client client) {
        this.validationUrl = oauth2Config.getUrl() + ApifestApiPath.TOKENS_VALIDATION.getPath();
        this.client = client;
    }

    @Override
    public Optional<User> authenticate(OAuth2Credentials credentials) throws AuthenticationException {
        try {
            AccessToken token;
            if (credentials instanceof OAuth2CookieCredentials) {
                token = ((OAuth2CookieCredentials) credentials).getToken();
            } else {
                token = new AccessToken();
                token.setToken(((OAuth2HeaderCredentials) credentials).getToken());
            }

            TokenValidationResponse tvr = validateToken(token);
            if (!tvr.isValid()) {
                return Optional.empty();
            }

            String username = tvr.getUserId();
            if (credentials instanceof OAuth2CookieCredentials) {
                username = ((OAuth2CookieCredentials) credentials).getUsername();
            }

            Set<String> roles = new HashSet<>();
            if ("admin".equals(username)) {
                roles.add("ADMIN");
            }
            roles.add("USER");

            User user = new User();
            user.setUsername(username);
            user.setToken(token);
            user.setRoles(roles);

            return Optional.of(user);
        } catch (Exception ex) {
            log.error("Exception during authentication", ex);
            throw new AuthenticationException("Invalid credentials");
        }
    }

    private TokenValidationResponse validateToken(AccessToken token) throws AuthenticationException {
        // Idea: Check valid token from DB

        TokenValidationResponse tokenValidationResponse = new TokenValidationResponse();
        tokenValidationResponse.setValid(true);
        tokenValidationResponse.setUserId("admin");

        return tokenValidationResponse;
    }

}