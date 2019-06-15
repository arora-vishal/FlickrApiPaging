package com.effort.images.di.component;

import com.effort.images.di.module.MainActivityModule;
import com.effort.images.di.scope.ActivityScope;
import com.effort.images.ui.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MainActivityModule.class, dependencies = ApplicationComponent.class)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}

