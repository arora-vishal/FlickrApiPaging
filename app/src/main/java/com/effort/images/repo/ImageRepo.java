package com.effort.images.repo;

import com.effort.images.data.ImageListing;

public interface ImageRepo {
    ImageListing requestImages(String keyword, boolean loadFromCache);
}
