package com.effort.images.paged;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.effort.images.components.NetworkResourceState;
import com.effort.images.components.Optional;
import com.effort.images.components.ResultCallback;
import com.effort.images.components.RetryRequest;
import com.effort.images.data.ImageResource;
import com.effort.images.data.ImageSearchResponse;
import com.effort.images.db.ImageSearchEntity;
import com.effort.images.network.ImageService;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ImagesDataSource extends PageKeyedDataSource<Integer, ImageResource> {

    private final String keyword;
    private final ImageService imageService;
    private final MutableLiveData<NetworkResourceState> networkResourceState;
    private final MutableLiveData<RetryRequest> retryRequest;
    private Optional<ResultCallback<ImageSearchEntity>> imagesCallback = Optional.empty();

    @Inject
    public ImagesDataSource(ImageService imageService, String keyword) {
        this.imageService = imageService;
        this.keyword = keyword;
        this.networkResourceState = new MutableLiveData<>();
        this.retryRequest = new MutableLiveData<>();
        this.networkResourceState.postValue(NetworkResourceState.loaded());
        this.retryRequest.postValue(null);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
        networkResourceState.postValue(NetworkResourceState.loading());
        imageService.searchImages(keyword, 0).enqueue(new Callback<ImageSearchResponse>() {
            @Override
            public void onResponse(Call<ImageSearchResponse> call, Response<ImageSearchResponse> response) {
                networkResourceState.postValue(NetworkResourceState.loaded());
                if (response.body() != null && !response.body().getPhotos().getImages().isEmpty()) {
                    callback.onResult(response.body().getPhotos().getImages(), 0, 1);
                    for (ImageResource imageResource : response.body().getPhotos().getImages()) {
                        imagesCallback.ifPresent(listResultCallback -> listResultCallback.onResult(new ImageSearchEntity(keyword, imageResource)));
                    }
                } else {
                    dispatchError(new Exception("No results found"));
                }
            }

            @Override
            public void onFailure(Call<ImageSearchResponse> call, Throwable t) {
                retryRequest.postValue(new RetryRequest(() -> loadInitial(params, callback)));
                dispatchError(t);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {
        networkResourceState.postValue(NetworkResourceState.loading());
        imageService.searchImages(keyword, (Integer) params.key).enqueue(new Callback<ImageSearchResponse>() {
            @Override
            public void onResponse(Call<ImageSearchResponse> call, Response<ImageSearchResponse> response) {
                networkResourceState.postValue(NetworkResourceState.loaded());
                if (response.body() != null) {
                    callback.onResult(response.body().getPhotos().getImages(), (int) params.key + 1);
                    for (ImageResource imageResource : response.body().getPhotos().getImages()) {
                        imagesCallback.ifPresent(listResultCallback -> listResultCallback.onResult(new ImageSearchEntity(keyword, imageResource)));
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageSearchResponse> call, Throwable t) {
                retryRequest.postValue(new RetryRequest(() -> loadAfter(params, callback)));
                dispatchError(t);
            }
        });
    }

    private void dispatchError(Throwable t) {
        if (t instanceof IOException) {
            networkResourceState.postValue(NetworkResourceState.error("Internet Connectivity error"));
        } else if (t instanceof HttpException){
            networkResourceState.postValue(NetworkResourceState.error("API error"));
        } else {
            networkResourceState.postValue(NetworkResourceState.error(t.getMessage()));
        }
    }

    public LiveData<RetryRequest> getRetryRequest() {
        return this.retryRequest;
    }

    public LiveData<NetworkResourceState> getNetworkState() {
        return this.networkResourceState;
    }

    public void setImagesCallback(ResultCallback<ImageSearchEntity> imagesCallback) {
        this.imagesCallback = Optional.of(imagesCallback);
    }
}
