package model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class Pilot {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    private String info;

    private String level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
