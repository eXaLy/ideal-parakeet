package io.github.epelde.idealparakeet.model;

/**
 * Created by epelde on 07/01/2016.
 */
public class Photo {

    private String id;
    private int width;
    private int height;
    private String color;
    private User user;
    private PhotoUrls urls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PhotoUrls getUrls() {
        return urls;
    }

    public void setUrls(PhotoUrls urls) {
        this.urls = urls;
    }
}
