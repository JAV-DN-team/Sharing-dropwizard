package model;

import java.security.Principal;
import java.util.Set;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class Role {
    private Long userid;
    private String role;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
