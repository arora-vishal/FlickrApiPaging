package com.effort.images.di.component;

import android.app.Application;

import com.effort.images.di.module.NetworkModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = NetworkModule.class)
public interface ApplicationComponent {
    Application application();

    Retrofit baseRetrofit();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }
}
