package io.github.epelde.idealparakeet.networking;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by epelde on 07/01/2016.
 */
public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass, String baseURL) {
        return createService(serviceClass, baseURL, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseURL, final String accessToken) {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder()
                        .header("Accept-Version", "v1");

                if (accessToken != null) {
                    builder.header("Authorization", "Bearer " + accessToken);
                }
                builder.method(original.method(), original.body());
                return chain.proceed(builder.build());
            }
        });

        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build().create(serviceClass);
    }

}
