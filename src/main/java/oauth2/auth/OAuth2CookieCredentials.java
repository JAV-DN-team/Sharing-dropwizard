package oauth2.auth;

import oauth2.apifest.AccessToken;

/**
 * Holds OAuth2 credentials when transmitted in the cookie
 *
 * @author Edouard De Oliveira
 */
public class OAuth2CookieCredentials implements OAuth2Credentials {
    private String username;
    private AccessToken token;

    public OAuth2CookieCredentials(String username, AccessToken token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public AccessToken getToken() {
        return token;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OAuth2CookieCredentials other = (OAuth2CookieCredentials) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}