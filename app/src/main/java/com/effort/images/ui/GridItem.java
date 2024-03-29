package com.effort.images.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class GridItem extends CardView {

    public GridItem(@NonNull Context context) {
        super(context);
    }

    public GridItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
