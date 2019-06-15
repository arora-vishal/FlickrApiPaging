package com.effort.images.utils;

import android.view.View;

public class ViewUtils {

    public static void setVisible(View... views) {
        setVisibility(views, View.VISIBLE);
    }

    public static void setGone(View... views) {
        setVisibility(views, View.GONE);
    }

    public static void setInvisible(View... views) {
        setVisibility(views, View.INVISIBLE);
    }

    private static void setVisibility(View[] views, int visibility) {
        if (null == views) {
            return;
        }
        for (View view : views) {
            if (null != view) {
                view.setVisibility(visibility);
            }
        }
    }
}
