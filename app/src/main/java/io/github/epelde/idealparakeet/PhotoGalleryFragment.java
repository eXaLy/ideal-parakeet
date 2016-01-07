package io.github.epelde.idealparakeet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryFragment extends Fragment {

    private RecyclerView photos;
    private UnsplashClient client = ServiceGenerator.createService(UnsplashClient.class);
    private static final String LOG_TAG = PhotoGalleryFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photos = (RecyclerView) view.findViewById(R.id.photos);
        //photos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(LOG_TAG, "* * * FETCHING!!!!");
            /*Call<List<Photo>> call = client.getPhotos();
            try {
                Response<List<Photo>> response = call.execute();
                Log.i(LOG_TAG, "* * * " + response.code());
                Log.i(LOG_TAG, "* * * " + response.message());
                //Log.i(LOG_TAG, "* * * PHOTOS:" + photos.size());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error fetching photos");
            }*/
            return null;
        }
    }
}
