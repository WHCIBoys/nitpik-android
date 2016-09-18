package me.nitpik.nitpik_android.models;

/**
 * Created by maazali on 2016-09-17.
 */
public class Nit {
    private String id;
    private User author;
    private User user;
    private String content;
    private Boolean isAnonymous;

    public Nit(User author, User user, String content, Boolean isAnonymous) {
        this.author = author;
        this.user = user;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }

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

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

}
