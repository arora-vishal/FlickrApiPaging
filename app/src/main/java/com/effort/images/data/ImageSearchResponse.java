package com.effort.images.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.effort.images.db.DataConverters;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ImageSearchResponse implements Serializable {

    @NonNull
    @TypeConverters(DataConverters.class)
    @SerializedName("photos")
    private final Photos photos;

    public ImageSearchResponse(Photos photos) {
        this.photos = photos;
    }

    public Photos getPhotos() {
        return photos;
    }

    public static class Photos implements Serializable {

        @SerializedName("photo")
        private final List<ImageResource> images;

        @NonNull
        @SerializedName("page")
        private final long page;

        public Photos(List<ImageResource> images, long page) {
            this.images = images;
            this.page = page;
        }

        public long getPage() {
            return page;
        }

        public List<ImageResource> getImages() {
            return images;
        }
    }
}
