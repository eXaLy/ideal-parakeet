package io.github.epelde.idealparakeet.networking;

import java.util.List;

import io.github.epelde.idealparakeet.model.Photo;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by epelde on 07/01/2016.
 */
public interface UnsplashClient {

    @Headers("Accept-Version: v1")
    @GET("/photos")
    Call<List<Photo>> getPhotos(@Header("Authorization") String accessToken,
                                @Query("page") int page,
                                @Query("per_page") int itemsPerPage);

}
