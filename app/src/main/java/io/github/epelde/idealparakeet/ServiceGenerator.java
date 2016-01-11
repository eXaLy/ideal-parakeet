package io.github.epelde.idealparakeet;

import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by epelde on 07/01/2016.
 */
public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass, String baseURL) {
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build().create(serviceClass);
    }

}
