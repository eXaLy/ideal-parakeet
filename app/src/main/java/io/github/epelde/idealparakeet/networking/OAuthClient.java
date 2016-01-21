package io.github.epelde.idealparakeet.networking;

import io.github.epelde.idealparakeet.model.AccessToken;
import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by epelde on 11/01/2016.
 */
public interface OAuthClient {

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("code") String code,
            @Field("grant_type") String grantType
    );

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> refreshToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType
    );

}
