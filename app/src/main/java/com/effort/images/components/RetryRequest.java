package com.effort.images.components;

public class RetryRequest {

    private final RetryCallback retryCallback;

    public RetryRequest(RetryCallback retryCallback) {
        this.retryCallback = retryCallback;
    }

    public RetryCallback getRetryCallback() {
        return retryCallback;
    }

    public interface RetryCallback {
        void retry();
    }
}
