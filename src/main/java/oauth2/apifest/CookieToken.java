package oauth2.apifest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Holds the data which is stored in the client cookie in JSON format.
 */
@JsonPropertyOrder({"token", "username"})
public class CookieToken {

    @JsonProperty("token")
    private AccessToken token;

    @JsonProperty("username")
    private String username;

    public CookieToken() {
    }

    public CookieToken(AccessToken token, String username) {
        this.username = username;
        this.token = token;
    }

    public AccessToken getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}