package com.effort.images.db;

import android.arch.persistence.room.TypeConverter;

import com.effort.images.data.ImageResource;
import com.effort.images.data.ImageSearchResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataConverters {

    @TypeConverter
    public String fromPhotos(ImageSearchResponse.Photos photos) {
        Gson gson = new Gson();
        Type type = new TypeToken<ImageSearchResponse.Photos>() {
        }.getType();
        return gson.toJson(photos, type);
    }

    @TypeConverter
    public ImageSearchResponse.Photos toPhotos(String photos) {
        Gson gson = new Gson();
        Type type = new TypeToken<ImageSearchResponse.Photos>() {
        }.getType();
        return gson.fromJson(photos, type);
    }

    @TypeConverter
    public String fromListImages(List<ImageResource> imageResourceList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ImageResource>>() {
        }.getType();
        return gson.toJson(imageResourceList, type);
    }


    @TypeConverter
    public List<ImageResource> toListImages(String images) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ImageResource>>() {
        }.getType();
        return gson.fromJson(images, type);
    }
}
