package io.github.epelde.idealparakeet.activity;

import android.support.v4.app.Fragment;

import io.github.epelde.idealparakeet.fragment.PhotoGalleryFragment;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
