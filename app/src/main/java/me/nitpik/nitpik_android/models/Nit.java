package me.nitpik.nitpik_android.models;

/**
 * Created by maazali on 2016-09-17.
 */
public class Nit {
    private User author;
    private User user;
    private String content;

    public User getUser() {return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAuthor() {return author; }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
