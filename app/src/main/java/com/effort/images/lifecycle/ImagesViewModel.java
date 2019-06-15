package com.effort.images.lifecycle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.effort.images.components.NetworkResourceState;
import com.effort.images.components.RetryRequest;
import com.effort.images.data.ImageListing;
import com.effort.images.data.ImageResource;
import com.effort.images.repo.ImageRepo;
import com.effort.images.utils.NetworkUtils;

import javax.inject.Inject;

import static android.arch.lifecycle.Transformations.map;
import static android.arch.lifecycle.Transformations.switchMap;

public class ImagesViewModel extends AndroidViewModel {

    private final LiveData<ImageListing> imageListing;
    private final LiveData<PagedList<ImageResource>> imagesList;
    private final LiveData<NetworkResourceState> networkResourceState;
    private final LiveData<RetryRequest> retryRequest;
    private final MutableLiveData<String> keyword;
    private final ImageRepo imageRepo;
    private final Observer<RetryRequest> retryRequestObserver;
    private RetryRequest latestRetryRequest;

    @Inject
    public ImagesViewModel(Application application, ImageRepo imageRepo) {
        super(application);
        this.imageRepo = imageRepo;
        this.keyword = new MutableLiveData<>();
        this.imageListing = map(this.keyword, input -> this.imageRepo.requestImages(input, !NetworkUtils.isNetworkConnected(application)));
        this.imagesList = switchMap(this.imageListing, ImageListing::getImageResourcePagedList);
        this.networkResourceState = switchMap(this.imageListing, ImageListing::getNetworkResourceState);
        this.retryRequest = switchMap(this.imageListing, ImageListing::getRetryRequest);
        this.retryRequestObserver = retryRequest1 -> latestRetryRequest = retryRequest1;
        this.retryRequest.observeForever(retryRequestObserver);
    }

    public void requestImages(String keyword) {
        this.keyword.setValue(keyword);
    }

    public LiveData<PagedList<ImageResource>> getImages() {
        return imagesList;
    }

    public LiveData<NetworkResourceState> getNetworkState() {
        return networkResourceState;
    }

    public void retryLatestRequest() {
        if (latestRetryRequest.getRetryCallback() != null) {
            latestRetryRequest.getRetryCallback().retry();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.retryRequest.removeObserver(retryRequestObserver);
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final Application application;
        private final ImageRepo imageRepo;

        public Factory(Application application, ImageRepo imageRepo) {
            this.application = application;
            this.imageRepo = imageRepo;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ImagesViewModel.class)) {
                return (T) new ImagesViewModel(application, imageRepo);
            }

            throw new IllegalStateException("Illegal Model class");
        }
    }
}
