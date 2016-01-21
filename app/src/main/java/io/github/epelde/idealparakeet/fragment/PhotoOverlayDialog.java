package io.github.epelde.idealparakeet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.model.Photo;

/**
 * Created by epelde on 21/01/2016.
 */
public class PhotoOverlayDialog extends DialogFragment {

    private static final String LOG_TAG = PhotoOverlayDialog.class.getSimpleName();

    private Photo photo;
    private ImageView photoImageView;
    private TextView userTextView;

    public PhotoOverlayDialog() {
    }

    public static PhotoOverlayDialog newInstance(Photo p) {
        PhotoOverlayDialog dialog = new PhotoOverlayDialog();
        dialog.photo = p;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_photo_overlay, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        photoImageView = (ImageView) view.findViewById(R.id.photo_image_view);
        userTextView = (TextView) view.findViewById(R.id.username_text_view);
        userTextView.setText(photo.getUser().getName());
        if (photo.getUrls().getRegular() != null) {
            Picasso.with(photoImageView.getContext())
                    .load(photo.getUrls().getRegular())
                    .placeholder(R.drawable.placeholder_medium)
                    .error(R.drawable.placeholder_medium)
                    .fit()
                    .centerCrop()
                    .into(photoImageView);
        }
    }
}
