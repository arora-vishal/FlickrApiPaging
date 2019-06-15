package com.effort.images.db;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.effort.images.data.ImageResource;

@Dao
public interface ImageSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSearchRequest(ImageSearchEntity imageSearchEntity);

    @Query("Select * from image_searches where keyword = :keyword order by datetime() DESC")
    DataSource.Factory<Integer, ImageResource> searchImages(String keyword);

}
