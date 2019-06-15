package com.effort.images.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.effort.images.data.ImageResource;

@Entity(tableName = "image_searches", primaryKeys = {"sdUrl"})
public class ImageSearchEntity {

    @NonNull
    @ColumnInfo(name = "keyword")
    private final String keyword;

    @NonNull
    @Embedded()
    private final ImageResource imageResource;

    public ImageSearchEntity(@NonNull String keyword, @NonNull ImageResource imageResource) {
        this.keyword = keyword;
        this.imageResource = imageResource;
    }

    @NonNull
    public String getKeyword() {
        return keyword;
    }

    @NonNull
    public ImageResource getImageResource() {
        return imageResource;
    }
}

