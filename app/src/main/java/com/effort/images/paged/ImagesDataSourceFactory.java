package com.effort.images.paged;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.effort.images.data.ImageResource;
import com.effort.images.network.ImageService;

public class ImagesDataSourceFactory extends DataSource.Factory<Integer, ImageResource> {

    private final ImageService imageService;
    private final String keyword;

    private MutableLiveData<ImagesDataSource> sourceLiveData = new MutableLiveData<>();
    private ImagesDataSource latestSource;

    public ImagesDataSourceFactory(ImageService imageService, String keyword) {
        this.imageService = imageService;
        this.keyword = keyword;
    }

    @Override
    public DataSource<Integer, ImageResource> create() {
        latestSource = new ImagesDataSource(imageService, keyword);
        sourceLiveData.postValue(latestSource);
        return latestSource;
    }

    public LiveData<ImagesDataSource> getLatestSource() {
        return sourceLiveData;
    }
}
