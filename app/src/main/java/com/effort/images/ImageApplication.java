package com.effort.images;

import android.app.Application;

import com.effort.images.di.component.ApplicationComponent;
import com.effort.images.di.component.DaggerApplicationComponent;

public class ImageApplication extends Application {

    static ImageApplication instance;

    private ApplicationComponent applicationComponent;

    public static ImageApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.applicationComponent = DaggerApplicationComponent.builder().application(this).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
