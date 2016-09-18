package me.nitpik.nitpik_android.models;

/**
 * Created by maazali on 2016-09-17.
 */
public class Token {
    private String jwtToken;
    private String userId;

    public String getJWTToken() {
        return jwtToken;
    }

    public void setJWTToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
