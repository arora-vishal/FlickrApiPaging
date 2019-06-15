package com.effort.images.components;

import android.support.annotation.IntDef;

public class NetworkResourceState {

    public static final int LOADING = 0;
    public static final int LOADED = 1;
    public static final int ERROR = 2;

    @Status
    public final int status;
    public final String message;

    @IntDef({LOADING, LOADED, ERROR})
    @interface Status {

    }

    private NetworkResourceState(@Status int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static NetworkResourceState loading() {
        return new NetworkResourceState(LOADING, "loading");
    }

    public static NetworkResourceState error(String message) {
        return new NetworkResourceState(ERROR, message);
    }

    public static NetworkResourceState loaded() {
        return new NetworkResourceState(LOADED, "loaded");
    }


}
