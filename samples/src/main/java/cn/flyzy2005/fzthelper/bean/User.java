package cn.flyzy2005.fzthelper.bean;

import java.io.Serializable;

/**
 * Created by Fly on 2017/5/3.
 */

public class User implements Serializable{
    private String id;
    private String username;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public String toString() {
        return "ID:" + id + ", username:" + username + ", password:" + password;
    }
}
