package web;

import com.codahale.metrics.annotation.Timed;
import config.OAuth2Config;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import model.User;
import oauth2.apifest.AccessToken;
import oauth2.apifest.ApifestApiPath;
import oauth2.auth.CookieEncrypter;
import oauth2.auth.OAuth2AuthFilter;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import view.DropwizardView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Path(value = "/user")
public class OAuth2Resource extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Resource.class);

    private Client client;
    private OAuth2Config oauth2Config;
    private String tokenUrl;
    private CookieEncrypter engine;
    private UserService userService;

    public OAuth2Resource(Client client, OAuth2Config oauth2Config, CookieEncrypter engine, UserService userService) {
        this.client = client;
        this.oauth2Config = oauth2Config;
        this.tokenUrl = oauth2Config.getUrl() + ApifestApiPath.TOKENS.getPath();
        this.engine = engine;
        this.userService = userService;
    }

    private static final String VIEW_NAME = "login.html";

    @GET
    @Path(value = "/login")
    @Produces(MediaType.TEXT_HTML)
    public DropwizardView index() {
        return getView("login.html", null);
    }

    @POST
    @Timed
    @Path(value = "/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username,
                          @FormParam("password") String password,
                          @Context final HttpServletRequest request) {

        User user = userService.getUser(username, password);
        if (user == null) {
            return Response.ok("{\"message\" : \"User & password invalid.\"}").build(); // redirect login
        }

        AccessToken token = new AccessToken();
        token.setType("password");
        token.setScope(oauth2Config.getScope());
        token.setToken(username + ":abcxyz");
        token.setExpiresIn("180"); //second
        token.setRefreshToken(username + ":xyzabc");
        token.setRefreshExpiresIn("180"); //second

        try {
            NewCookie nc = engine.buildCookie(username, token, request.getServerName());

            StringBuilder sb = new StringBuilder();
            sb.append("{\"name\": \"").append(username).append("\", ");
            sb.append("\"expiresIn\": \"").append(token.getExpiresIn()).append("\"}");

            return Response.ok(sb.toString()).cookie(nc).build();
        } catch (Exception ex) {
            log.error("Error while building login response", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GET
//    @Timed
//    @Path(value = "/refresh")
//    public Response refreshToken(@Auth User user,
//            @Context final HttpServletRequest request) throws AuthenticationException {
//        WebTarget target = client.target(tokenUrl);
//        Invocation.Builder builder = target.request();
//
//        Form form = new Form();
//        form.param("grant_type", "refresh_token");
//        form.param("refresh_token", user.getToken().getRefreshToken());
//        form.param("client_id", oauth2Config.getClientId());
//        form.param("client_secret", oauth2Config.getClientSecret());
//
//        Response response = builder.accept(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
//
//        if (response.getStatus() == HttpStatus.SC_BAD_REQUEST) {
//            log.debug("Error {} : invalid refresh token {}", user.getToken().getRefreshToken(), response.getStatus());
//            response.close();
//            throw new AuthenticationException("Invalid credentials");
//        }
//
//        try {
//            AccessToken newToken = response.readEntity(AccessToken.class);
//            NewCookie nc = engine.buildCookie(user.getName(), newToken, request.getServerName());
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("{\"name\": \"").append(user.getName()).append("\", ");
//            sb.append("\"expiresIn\": \"").append(newToken.getExpiresIn()).append("\"}");
//
//            return Response.ok(sb.toString()).cookkie(nc).build();
//        } catch (Exception ex) {
//            log.error("Error while building login response", ex);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        } finally {
//            response.close();
//        }
//    }

    @GET
    @Timed
    @Path(value = "/whoami")
    public String whoAmI(@Auth User user, @Context final HttpServletRequest request) {
        return "{\"name\": \"" + user.getName() + "\"}";
    }

    @GET
    @Timed
    @Path(value = "/logout")
    public Response logout(@Context final HttpServletRequest request) {

        // invalidate cookie if exists
        ResponseBuilder reply = Response.ok("{\"message\" : \"logged out.\"}");

        for (Cookie c : request.getCookies()) {
            if (OAuth2AuthFilter.AUTH_COOKIE_NAME.equals(c.getName())) {
                reply.cookie(new NewCookie(OAuth2AuthFilter.AUTH_COOKIE_NAME,
                        null, "/", request.getServerName(), null, 0, true));
                break;
            }
        }

        return reply.build();
    }

    private String getExpires(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return String.valueOf(sdf.parse(dateStr).getTime());
        } catch (ParseException e) {
            return "0";
        }
    }
}