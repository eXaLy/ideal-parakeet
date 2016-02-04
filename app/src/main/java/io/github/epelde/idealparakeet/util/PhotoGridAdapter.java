package io.github.epelde.idealparakeet.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.model.Photo;

/**
 * Created by epelde on 11/01/2016.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder> {

    private static final String LOG_TAG = PhotoGridAdapter.class.getSimpleName();

    private List<Photo> photos;
    private LongClickListener listener;

    public PhotoGridAdapter(List<Photo> photos, LongClickListener listener) {
        this.photos = photos;
        this.listener = listener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_photo_gallery_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(this.photos.get(position));
    }

    @Override
    public int getItemCount() {
        return this.photos.size();
    }

    public void addItems(List<Photo> items) {
        this.photos.addAll(items);
        notifyDataSetChanged();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.photo);
        }

        public void bind(final Photo photo) {
            photoImageView.setLongClickable(true);
            photoImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(v, photo);
                    return true;
                }
            });
            photoImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        listener.onLongClickReleased();
                        return true;
                    }
                    return false;
                }
            });
            if (photo.getUrls().getThumb() != null) {
                Picasso.with(photoImageView.getContext())
                        .load(photo.getUrls().getThumb())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fit()
                        .centerCrop()
                        .into(photoImageView);
            }
        }
    }

    public interface LongClickListener {
        public void onLongClick(View v, Photo p);
        public void onLongClickReleased();
    }
}
