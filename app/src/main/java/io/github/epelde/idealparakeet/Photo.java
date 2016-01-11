package io.github.epelde.idealparakeet;

/**
 * Created by epelde on 07/01/2016.
 */
public class Photo {

    private String id;
    private PhotoUrls urls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PhotoUrls getUrls() {
        return urls;
    }

    public void setUrls(PhotoUrls urls) {
        this.urls = urls;
    }
}
