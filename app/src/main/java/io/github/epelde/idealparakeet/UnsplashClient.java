package io.github.epelde.idealparakeet;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;

/**
 * Created by epelde on 07/01/2016.
 */
public interface UnsplashClient {

    @Headers("Accept-Version: v1")
    @GET("/photos?per_page=24")
    Call<List<Photo>> getPhotos(@Header("Authorization") String accessToken);

}
