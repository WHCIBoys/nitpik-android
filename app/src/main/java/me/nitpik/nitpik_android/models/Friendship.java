package me.nitpik.nitpik_android.models;

/**
 * Created by maazali on 2016-09-17.
 */
public class Friendship {
    private User user;
    private User friend;

    public User getUser() {return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {return friend; }

    public void setFriend(User friend) {
        this.friend = friend;
    }

}
