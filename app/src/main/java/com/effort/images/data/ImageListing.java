package com.effort.images.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.effort.images.components.NetworkResourceState;
import com.effort.images.components.RetryRequest;

/**
 * Data class for UI to show images/network state in case of errors
 */
public class ImageListing {

    private final LiveData<PagedList<ImageResource>> imageResourcePagedList;
    private final LiveData<NetworkResourceState> networkResourceState;
    private final LiveData<RetryRequest> retryRequest;

    public ImageListing(LiveData<PagedList<ImageResource>> imageResourcePagedList, LiveData<NetworkResourceState> networkResourceState, LiveData<RetryRequest> retryRequest) {
        this.imageResourcePagedList = imageResourcePagedList;
        this.networkResourceState = networkResourceState;
        this.retryRequest = retryRequest;
    }

    public LiveData<RetryRequest> getRetryRequest() {
        return retryRequest;
    }

    public LiveData<PagedList<ImageResource>> getImageResourcePagedList() {
        return imageResourcePagedList;
    }

    public LiveData<NetworkResourceState> getNetworkResourceState() {
        return networkResourceState;
    }
}
