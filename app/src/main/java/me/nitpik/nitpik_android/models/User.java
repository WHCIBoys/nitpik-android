package me.nitpik.nitpik_android.models;

/**
 * Created by maazali on 2016-09-16.
 */
public class User {
    private String name;
    private String facebookId;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public String getAvatarUrl(String size) {
        String maxWidth = "1000";
        switch(size) {
            case "normal":
                return "https://graph.facebook.com/" + facebookId + "/picture?type=large";
            case "large":
                return "https://graph.facebook.com/" + facebookId + "/picture?width=" + maxWidth;
            default:
                return "https://graph.facebook.com/" + facebookId + "/picture?type=large";
        }
    }


    @Override
    public String toString() {
        return name;
    }

}
