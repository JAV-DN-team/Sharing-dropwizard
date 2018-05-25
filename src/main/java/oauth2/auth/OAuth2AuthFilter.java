package oauth2.auth;

import java.util.Optional;
import com.google.common.base.Preconditions;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.DefaultUnauthorizedHandler;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.UnauthorizedHandler;
import oauth2.apifest.CookieToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

/**
 * Created by FRAMGIA\dinh.thanh on 24/05/2018.
 */
@Priority(Priorities.AUTHENTICATION)
public final class OAuth2AuthFilter<P extends Principal> extends AuthFilter<OAuth2Credentials, P> {
    private final static Logger log = LoggerFactory.getLogger(OAuth2AuthFilter.class);

    public final static String AUTH_COOKIE_NAME = "_dw_auth_cookie";
    private final static String BEARER_TYPE = "Bearer";

    private CookieEncrypter cookieEncrypter;

    private OAuth2AuthFilter() {
    }

    /**
     * Builder for {@link OAuth2AuthFilter}.
     * Mandatory parameters to be set :
     * <p>An {@link Authenticator}</p>
     * <p>An {@link CookieEncrypter}</p>
     *
     * @param <P> the principal
     * @param <T> the filter
     */
    public static class Builder<C, P extends Principal, T extends OAuth2AuthFilter<P>, A extends Authenticator<OAuth2Credentials, P>> {
        private String prefix = BEARER_TYPE;
        private CookieEncrypter cookieEncrypter;
        private A authenticator;
        private Authorizer<P> authorizer = new PermitAllAuthorizer();
        private UnauthorizedHandler unauthorizedHandler = new DefaultUnauthorizedHandler();

        public Builder() {
        }

        public OAuth2AuthFilter.Builder<C, P, T, A> setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public OAuth2AuthFilter.Builder<C, P, T, A> setCookieEncrypter(CookieEncrypter cookieEncrypter) {
            this.cookieEncrypter = cookieEncrypter;
            return this;
        }

        public OAuth2AuthFilter.Builder<C, P, T, A> setAuthorizer(Authorizer<P> authorizer) {
            this.authorizer = authorizer;
            return this;
        }

        public OAuth2AuthFilter.Builder<C, P, T, A> setAuthenticator(A authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        public OAuth2AuthFilter.Builder<C, P, T, A> setUnauthorizedHandler(UnauthorizedHandler unauthorizedHandler) {
            this.unauthorizedHandler = unauthorizedHandler;
            return this;
        }

        public OAuth2AuthFilter<P> build() {
            Preconditions.checkArgument(this.prefix != null, "Prefix is not set");
            Preconditions.checkArgument(this.cookieEncrypter != null, "CookieEncrypter is not set");
            Preconditions.checkArgument(this.authenticator != null, "Authenticator is not set");
            Preconditions.checkArgument(this.authorizer != null, "Authorizer is not set");
            Preconditions.checkArgument(this.unauthorizedHandler != null, "Unauthorized handler is not set");

            OAuth2AuthFilter<P> filter = new OAuth2AuthFilter<P>();
            filter.prefix = this.prefix;
            filter.cookieEncrypter = this.cookieEncrypter;
            filter.authenticator = this.authenticator;
            filter.authorizer = this.authorizer;
            filter.unauthorizedHandler = this.unauthorizedHandler;

            return filter;
        }
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        try {
            OAuth2Credentials creds = null;

            // Extract credentials
            Map<String, Cookie> map = requestContext.getCookies();
            Cookie cookie = map.get(AUTH_COOKIE_NAME);

            if (cookie != null) {
                CookieToken ct = cookieEncrypter.readCookie(cookie);
                creds = new OAuth2CookieCredentials(ct.getUsername(), ct.getToken());
            } else {
                String authString = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

                if (authString != null && authString.startsWith(prefix)) {
                    String authToken = authString.substring(prefix.length()+1);
                    creds = new OAuth2HeaderCredentials(authToken);
                }
            }

            Optional<P> principal = authenticator.authenticate(creds);

            if (principal.isPresent()) {
                final P userPrincipal = principal.get();
                requestContext.setSecurityContext(new SecurityContext() {

                    @Override
                    public Principal getUserPrincipal() {
                        return userPrincipal;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return authorizer.authorize(userPrincipal, role);
                    }

                    @Override
                    public boolean isSecure() {
                        return requestContext.getSecurityContext().isSecure();
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return SecurityContext.BASIC_AUTH;
                    }
                });
            }
        } catch (AuthenticationException e) {
            log.warn("Error authenticating credentials", e);
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (Exception ex) {
            log.error("Exception during cookie extraction", ex);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
