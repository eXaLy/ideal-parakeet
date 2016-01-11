package io.github.epelde.idealparakeet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by epelde on 11/01/2016.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder> {

    private List<Photo> photos;

    public PhotoGridAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_photo_item, parent, false);
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

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.photo);
        }

        public void bind(Photo photo) {
            if (photo.getUrls().getRegular() != null) {
                Picasso.with(photoImageView.getContext())
                        .load(photo.getUrls().getRegular())
                        .into(photoImageView);
            }
        }
    }
}
