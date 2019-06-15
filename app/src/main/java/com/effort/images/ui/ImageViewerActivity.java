package com.effort.images.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.effort.images.R;
import com.effort.images.data.ImageResource;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String KEY_IMAGE_RESOURCE = "KEY_IMAGE_RESOURCE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        supportPostponeEnterTransition();

        ImageResource imageResource = (ImageResource) getIntent().getSerializableExtra(KEY_IMAGE_RESOURCE);
        ImageView imageView = findViewById(R.id.iv_full_image);

        Glide.with(this)
                .load(imageResource.getHdUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        imageView.setImageDrawable(resource);
                        return true;
                    }
                }).submit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
