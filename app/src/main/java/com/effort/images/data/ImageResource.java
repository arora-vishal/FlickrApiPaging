package com.effort.images.data;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageResource implements Serializable {

    @SerializedName("url_m")
    @NonNull
    private final String sdUrl;

    @SerializedName("url_l")
    private final String hdUrl;

    @SerializedName("id")
    @ColumnInfo(name = "image_id")
    private final long id;

    public ImageResource(long id, String sdUrl, String hdUrl) {
        this.id = id;
        this.sdUrl = sdUrl;
        this.hdUrl = hdUrl;
    }

    public long getId() {
        return id;
    }

    public String getSdUrl() {
        return sdUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }
}
