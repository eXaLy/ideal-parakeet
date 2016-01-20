package io.github.epelde.idealparakeet.networking;

import java.util.List;

import io.github.epelde.idealparakeet.model.Photo;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by epelde on 07/01/2016.
 */
public interface UnsplashClient {

    @GET("/photos?per_page=30")
    Call<List<Photo>> getPhotos(@Query("page") int page);

}
