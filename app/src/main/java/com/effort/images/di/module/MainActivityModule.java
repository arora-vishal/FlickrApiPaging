package com.effort.images.di.module;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;

import com.effort.images.db.ImageSearchDao;
import com.effort.images.db.ImagesDb;
import com.effort.images.di.scope.ActivityScope;
import com.effort.images.lifecycle.ImagesViewModel;
import com.effort.images.network.ImageService;
import com.effort.images.repo.ImageRepo;
import com.effort.images.repo.ImageRepoImpl;
import com.effort.images.ui.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @ActivityScope
    public ImagesViewModel provideImagesViewModel(ImagesViewModel.Factory imagesViewModelFactory) {
        return ViewModelProviders.of(mainActivity, imagesViewModelFactory).get(ImagesViewModel.class);
    }

    @Provides
    @ActivityScope
    public ImagesViewModel.Factory provideImagesViewModelFactory(Application application, ImageRepo imageRepo) {
        return new ImagesViewModel.Factory(application, imageRepo);
    }

    @Provides
    @ActivityScope
    public ImageService provideImageService(Retrofit retrofit) {
        return retrofit.create(ImageService.class);
    }

    @Provides
    @ActivityScope
    public ImageSearchDao providerImageSearchDao() {
        return ImagesDb.getInstance(mainActivity.getApplicationContext())
                .imageSearchDao();
    }

    @Provides
    @ActivityScope
    public ImageRepo provideImageRepo(ExecutorService executorService, ImageService imageService, ImageSearchDao imageSearchDao) {
        return new ImageRepoImpl(executorService, imageService, imageSearchDao);
    }

    @Provides
    @ActivityScope
    public ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }
}
