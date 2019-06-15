package com.effort.images.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.effort.images.components.NetworkResourceState;
import com.effort.images.data.ImageListing;
import com.effort.images.data.ImageResource;
import com.effort.images.db.ImageSearchDao;
import com.effort.images.network.ImageService;
import com.effort.images.paged.ImagesDataSource;
import com.effort.images.paged.ImagesDataSourceFactory;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import static android.arch.lifecycle.Transformations.switchMap;

public class ImageRepoImpl implements ImageRepo {

    private static final int PAGE_ITEM_COUNT = 10;
    private static final int PREFETCH_DISTANCE = 10;
    private static final int INITIAL_LOAD_SIZE_HINT = 20;

    private final ImageService imageService;
    private final ImageSearchDao imageSearchDao;
    private final ExecutorService executorService;

    @Inject
    public ImageRepoImpl(ExecutorService executorService, ImageService imageService, ImageSearchDao imageSearchDao) {
        this.executorService = executorService;
        this.imageService = imageService;
        this.imageSearchDao = imageSearchDao;
    }

    /**
     * Use it to request images from server
     *
     * @param keyword - query
     * @return
     */
    @Override
    public ImageListing requestImages(String keyword, boolean loadFromCache) {
        return loadFromCache ? requestImagesFromDb(keyword) : requestImagesFromServer(keyword);
    }

    private ImageListing requestImagesFromServer(String keyword) {
        final ImagesDataSourceFactory imagesDataSourceFactory = new ImagesDataSourceFactory(imageService, keyword);
        final LiveData<PagedList<ImageResource>> imageResourcePagedList = new LivePagedListBuilder<>(imagesDataSourceFactory, pagedListConfig()).build();
        final LiveData<ImagesDataSource> latestDataSource = imagesDataSourceFactory.getLatestSource();

        imagesDataSourceFactory.getLatestSource().observeForever(imagesDataSource -> imagesDataSource.setImagesCallback(imageSearchEntity -> executorService.submit(() -> imageSearchDao.addSearchRequest(imageSearchEntity))));
        return new ImageListing(imageResourcePagedList, switchMap(latestDataSource, ImagesDataSource::getNetworkState), switchMap(latestDataSource, ImagesDataSource::getRetryRequest));
    }

    private ImageListing requestImagesFromDb(String keyword) {
        final DataSource.Factory<Integer, ImageResource> imagesDataSourceFactory = imageSearchDao.searchImages(keyword);
        final MutableLiveData<NetworkResourceState> networkResourceState = new MutableLiveData<>();
        final LiveData<PagedList<ImageResource>> imageResourcePagedList = new LivePagedListBuilder<>(imagesDataSourceFactory, pagedListConfig())
                .setBoundaryCallback(new PagedList.BoundaryCallback<ImageResource>() {
                    public void onZeroItemsLoaded() {
                        super.onZeroItemsLoaded();
                        networkResourceState.postValue(NetworkResourceState.error("No cached results. Enable internet and try again."));
                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull ImageResource itemAtEnd) {
                        super.onItemAtEndLoaded(itemAtEnd);
                        networkResourceState.postValue(NetworkResourceState.loaded());
                    }
                })
                .build();
        return new ImageListing(imageResourcePagedList, networkResourceState, null);
    }

    private PagedList.Config pagedListConfig() {
        return new PagedList.Config.Builder()
                .setPageSize(PAGE_ITEM_COUNT)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE_HINT)
                .build();
    }
}
