package com.effort.images.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.effort.images.ImageApplication;
import com.effort.images.R;
import com.effort.images.data.ImageResource;
import com.effort.images.di.component.DaggerMainActivityComponent;
import com.effort.images.di.component.MainActivityComponent;
import com.effort.images.di.module.MainActivityModule;
import com.effort.images.lifecycle.ImagesViewModel;
import com.effort.images.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    // keys
    private static final String KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY";
    public static final String KEY_START_ITEM_POSITION = "KEY_START_ITEM_POSITION";
    public static final String KEY_CURRENT_ITEM_POSITION = "KEY_CURRENT_ITEM_POSITION";

    // dependencies
    @Inject
    ImagesViewModel imagesViewModel;
    MainActivityComponent mainActivityComponent;

    // views
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvImages;
    private int imagesLayoutSpanCount = 2;

    private ImagesAdapter imagesAdapter;
    private GridLayoutManager layoutManager;
    private Bundle reenterState;

    private final SharedElementCallback exitElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            if (reenterState != null) {
                final int startingPosition = reenterState.getInt(KEY_START_ITEM_POSITION);
                final int currentPosition = reenterState.getInt(KEY_CURRENT_ITEM_POSITION);

                if (startingPosition != currentPosition) {
                    final String newTransitionName = "" + imagesAdapter.getCurrentList().snapshot().get(currentPosition).getId();
                    final View newSharedElement = rvImages.findViewHolderForAdapterPosition(currentPosition).itemView.findViewById(R.id.iv_image);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newTransitionName);

                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedElement);
                    }
                }
                reenterState = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback);

        mainActivityComponent = DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule(this))
                .applicationComponent(ImageApplication.getInstance().getApplicationComponent())
                .build();

        mainActivityComponent.inject(this);

        initViews();

        if (savedInstanceState != null) {
            String lastQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            etSearch.setText(lastQuery);
        }

        subscribeImages();

    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSearch = findViewById(R.id.btn_search);
        etSearch = findViewById(R.id.et_search);
        rvImages = findViewById(R.id.rv_images);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSearch.setEnabled(s.length() > 0);
            }
        });

        btnSearch.setOnClickListener(v -> {
            AppUtils.hideSoftKeyboard(this);
            requestImages(etSearch.getText().toString());
        });

        this.imagesAdapter = new ImagesAdapter();
        this.layoutManager = new GridLayoutManager(this, imagesLayoutSpanCount);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return imagesAdapter.getItemViewType(position) == ImagesAdapter.VIEW_TYPE_NETWORK_STATE ? imagesLayoutSpanCount : 1;
            }
        });
        rvImages.setLayoutManager(this.layoutManager);
        rvImages.setAdapter(this.imagesAdapter);

        this.imagesAdapter.setItemClickListener((item, position, itemView) -> {
            final View imageView = rvImages.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.iv_image);
            ArrayList<ImageResource> imageResources = new ArrayList<>(imagesAdapter.getCurrentList().snapshot());
            Intent intent = new Intent(this, ImageViewerActivity.class);
            intent.putExtra(ImageViewerActivity.KEY_IMAGE_RESOURCES, imageResources);
            intent.putExtra(ImageViewerActivity.KEY_ITEM_INDEX, position);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, imageView, ViewCompat.getTransitionName(imageView));
            startActivity(intent, options.toBundle());
        });

        this.imagesAdapter.setRetryCallback(() -> imagesViewModel.retryLatestRequest());
    }

    private void subscribeImages() {
        imagesViewModel.getImages().observe(this,
                imageResources -> this.imagesAdapter.submitList(imageResources));

        imagesViewModel.getNetworkState().observe(this,
                networkResourceState -> this.imagesAdapter.setNetworkResourceState(networkResourceState));
    }

    private void requestImages(String keyword) {
        imagesViewModel.requestImages(keyword);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        reenterState = data.getExtras();
        final int startingPosition = data.getIntExtra(KEY_START_ITEM_POSITION, 0);
        final int currentPosition = data.getIntExtra(KEY_CURRENT_ITEM_POSITION, 0);
        if (startingPosition != currentPosition) rvImages.scrollToPosition(currentPosition);
        ActivityCompat.postponeEnterTransition(this);

        rvImages.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rvImages.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(MainActivity.this);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int spanCount = 0;
        item.setChecked(true);
        switch (item.getItemId()) {
            default:
            case R.id.item_size_2:
                spanCount = 2;
                break;
            case R.id.item_size_3:
                spanCount = 3;
                break;
            case R.id.item_size_4:
                spanCount = 4;
                break;
        }
        updateSpanCount(spanCount);
        return true;
    }

    private void updateSpanCount(int spanCount) {
        if (this.imagesLayoutSpanCount != spanCount) {
            this.imagesLayoutSpanCount = spanCount;
        }

        layoutManager.setSpanCount(this.imagesLayoutSpanCount);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_QUERY, etSearch.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityComponent = null;
    }
}
