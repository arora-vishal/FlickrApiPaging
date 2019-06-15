package com.effort.images.network;

import com.effort.images.data.ImageSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageService {

    @GET("/services/rest?method=flickr.photos.search&extras=url_m,url_l")
    Call<ImageSearchResponse> searchImages(@Query("text") String keyword, @Query("page") int pageNumber);
}
