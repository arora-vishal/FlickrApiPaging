package com.effort.images.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(version = 1, entities = {ImageSearchEntity.class}, exportSchema = false)
public abstract class ImagesDb extends RoomDatabase {

    private static final String DB_NAME = "images_db";

    private static volatile ImagesDb instance;

    abstract public ImageSearchDao imageSearchDao();

    public static ImagesDb getInstance(Context context) {
        if (instance == null) {
            synchronized (ImagesDb.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            ImagesDb.class, DB_NAME)
                            .build();
                }
            }
        }
        return instance;
    }
}

