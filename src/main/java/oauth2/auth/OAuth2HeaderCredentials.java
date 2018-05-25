package oauth2.auth;

/**
 * Created by FRAMGIA\dinh.thanh on 24/05/2018.
 */
public class OAuth2HeaderCredentials implements OAuth2Credentials {
    private String token;

    public OAuth2HeaderCredentials(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((token == null) ? 0 : token.hashCode());
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
        OAuth2HeaderCredentials other = (OAuth2HeaderCredentials) obj;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }

}