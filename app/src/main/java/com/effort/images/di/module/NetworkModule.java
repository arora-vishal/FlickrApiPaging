package com.effort.images.di.module;

import com.effort.images.network.FlickrApi;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("www.flickr.com");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }


    @Provides
    @Singleton
    public OkHttpClient provideHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .callTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("api_key", FlickrApi.API_KEY)
                            .addQueryParameter("format", FlickrApi.FORMAT)
                            .addQueryParameter("nojsoncallback", "1")
                            .build();

                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .addInterceptor(logging)
                .build();

        return okHttpClient;
    }
}
