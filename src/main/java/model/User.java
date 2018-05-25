package model;

import oauth2.apifest.AccessToken;

import java.security.Principal;
import java.util.Set;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class User implements Principal {
    private Long id;
    private String username;
    private String password;
    private Set<String> roles;
    private AccessToken token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getName() {
        return username;
    }

    public AccessToken getToken() {
        return token;
    }

    public void setToken(AccessToken token) {
        this.token = token;
    }
}
