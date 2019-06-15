package com.effort.images.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.effort.images.R;
import com.effort.images.data.ImageResource;

import java.util.List;
import java.util.Map;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String KEY_IMAGE_RESOURCES = "KEY_IMAGE_RESOURCES";
    public static final String KEY_ITEM_INDEX = "KEY_ITEM_INDEX";
    public static final String KEY_CURRENT_SAVED_POSITION = "KEY_CURRENT_SAVED_POSITION";

    private boolean isReturning;
    private int startingPosition = 0;
    private int currentPosition = 0;
    private FullImageAdapter imagePagerAdapter;

    private final SharedElementCallback sharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            if (isReturning) {
                final View sharedElement = imagePagerAdapter.getView(currentPosition);

                if (startingPosition != currentPosition) {
                    names.clear();
                    names.add(ViewCompat.getTransitionName(sharedElement));

                    sharedElements.clear();
                    sharedElements.put(ViewCompat.getTransitionName(sharedElement), sharedElement);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ActivityCompat.postponeEnterTransition(this);
        ActivityCompat.setEnterSharedElementCallback(this, sharedElementCallback);

        final int index = getIntent().getIntExtra(KEY_ITEM_INDEX, 0);
        final int startingPosition = index > 0 ? index : 0;
        final List<ImageResource> imageResources = (List<ImageResource>) getIntent().getSerializableExtra(KEY_IMAGE_RESOURCES);

        currentPosition = savedInstanceState == null ? startingPosition : savedInstanceState.getInt(KEY_CURRENT_SAVED_POSITION);

        final ViewPager viewPager = findViewById(R.id.image_pager);
        imagePagerAdapter = new FullImageAdapter(imageResources, currentPosition);

        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
            }
        });
    }

    @Override
    public void supportFinishAfterTransition() {
        isReturning = true;
        Intent data = new Intent();
        data.putExtra(MainActivity.KEY_START_ITEM_POSITION, startingPosition);
        data.putExtra(MainActivity.KEY_CURRENT_ITEM_POSITION, currentPosition);
        setResult(Activity.RESULT_OK, data);
        super.supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_SAVED_POSITION, currentPosition);
    }

    class FullImageAdapter extends PagerAdapter {

        private final List<ImageResource> imageResourceList;
        private final int currentPosition;
        private final SparseArray<View> views;

        public FullImageAdapter(List<ImageResource> imageResourceList, int currentPosition) {
            this.imageResourceList = imageResourceList;
            this.currentPosition = currentPosition;
            this.views = new SparseArray<>(this.imageResourceList.size());
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageResource imageResource = imageResourceList.get(position);
            final ImageView imageView = new ImageView(container.getContext());
            ViewCompat.setTransitionName(imageView, "" + imageResource.getId());
            views.put(position, imageView);
            if (position == currentPosition) {
                Glide.with(container.getContext())
                        .load(imageResource.getHdUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                startPostTransition(imageView);
                                return false;
                            }
                        }).into(imageView);
            } else {
                Glide.with(container.getContext())
                        .load(imageResource.getHdUrl())
                        .into(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        private void startPostTransition(final View sharedView) {
            sharedView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                            supportStartPostponedEnterTransition();
                            return true;
                        }
                    });
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            views.remove(position);
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageResourceList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        public View getView(int currentPosition) {
            return views.get(currentPosition);
        }
    }
}
