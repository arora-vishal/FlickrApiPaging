package com.effort.images.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.effort.images.R;
import com.effort.images.components.Callback;
import com.effort.images.components.NetworkResourceState;
import com.effort.images.components.Optional;
import com.effort.images.data.ImageResource;
import com.effort.images.utils.ViewUtils;

import static com.effort.images.components.NetworkResourceState.ERROR;
import static com.effort.images.components.NetworkResourceState.LOADED;
import static com.effort.images.components.NetworkResourceState.LOADING;

public class ImagesAdapter extends PagedListAdapter<ImageResource, RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_NETWORK_STATE = 1;

    private static DiffUtil.ItemCallback<ImageResource> DIFF_CALLBACK = new DiffUtil.ItemCallback<ImageResource>() {
        @Override
        public boolean areItemsTheSame(ImageResource oldItem, ImageResource newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(ImageResource oldItem, ImageResource newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    private Optional<Callback> retryCallback = Optional.empty();
    private Optional<ItemClickListener<ImageResource>> itemClickListener = Optional.empty();
    private NetworkResourceState networkResourceState = NetworkResourceState.loaded();

    public ImagesAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case VIEW_TYPE_NETWORK_STATE:
                return new NetworkStateViewHolder(inflater.inflate(R.layout.item_network_state, viewGroup, false), retryCallback);
            default:
                ImageHolder imageHolder = new ImageHolder(inflater.inflate(R.layout.item_image, viewGroup, false));
                imageHolder.itemView.setOnClickListener(v -> itemClickListener.ifPresent(clickListener -> clickListener.onItemClick(getItem(imageHolder.getAdapterPosition()),
                        imageHolder.getAdapterPosition(), imageHolder.itemView.findViewById(R.id.iv_image))));
                return imageHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_NETWORK_STATE:
                ((NetworkStateViewHolder) viewHolder).bindState(this.networkResourceState);
                return;
            default:
                ((ImageHolder) viewHolder).bind(getItem(position));

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return VIEW_TYPE_NETWORK_STATE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    public boolean hasExtraRow() {
        return networkResourceState.status != LOADED;
    }

    public void setNetworkResourceState(NetworkResourceState networkResourceState) {
        if (this.networkResourceState.status == networkResourceState.status) {
            return;
        }

        if (hasExtraRow()) {
            this.networkResourceState = networkResourceState;
            if (this.networkResourceState.status == LOADED) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemChanged(super.getItemCount());
            }
        } else {
            this.networkResourceState = networkResourceState;
            notifyItemInserted(super.getItemCount());
        }
    }

    public void setRetryCallback(Callback callback) {
        this.retryCallback = Optional.of(callback);
    }

    public void setItemClickListener(ItemClickListener<ImageResource> itemClickListener) {
        this.itemClickListener = Optional.of(itemClickListener);
    }

    static class ImageHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
        }

        public void bind(ImageResource item) {
            ViewCompat.setTransitionName(ivImage, "" + item.getId());
            Glide.with(ivImage)
                    .load(item.getSdUrl())
                    .into(ivImage);
        }

    }

    static class NetworkStateViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar pbLoader;

        private Button btnRetry;

        public NetworkStateViewHolder(@NonNull View itemView, Optional<Callback> retryCallback) {
            super(itemView);
            pbLoader = itemView.findViewById(R.id.pb_loader);
            btnRetry = itemView.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(v -> retryCallback.ifPresent(object -> object.invoke()));
        }

        public void bindState(NetworkResourceState networkState) {
            switch (networkState.status) {
                case LOADING:
                    ViewUtils.setGone(btnRetry);
                    ViewUtils.setVisible(pbLoader);
                    return;
                case ERROR:
                    ViewUtils.setGone(pbLoader);
                    ViewUtils.setVisible(btnRetry);
                    btnRetry.setText(networkState.getMessage());
            }
        }

    }

    interface ItemClickListener<T> {
        void onItemClick(T item, int position, View itemView);
    }
}
