package io.github.epelde.idealparakeet.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.model.Photo;
import io.github.epelde.idealparakeet.util.PhotoGridAdapter;
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.networking.ServiceGenerator;
import io.github.epelde.idealparakeet.networking.UnsplashClient;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryFragment extends Fragment {

    private RecyclerView photos;
    private int currentPage = 1;
    private UnsplashClient restClient;
    private static final String LOG_TAG = PhotoGalleryFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.access_token_file), Context.MODE_PRIVATE);
        String accessToken = sharedPref.getString(getString(R.string.access_token), null);
        Log.i(LOG_TAG, "***AT:" + accessToken);
        restClient = ServiceGenerator.createService(UnsplashClient.class, App.API_BASE_URL);
        new FetchItemsTask().execute(accessToken);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photos = (RecyclerView) view.findViewById(R.id.photos);
        photos.setLayoutManager(new GridLayoutManager(getActivity(), App.SPAN_COUNT));
        return view;
    }

    private void setAdapter(List<Photo> items) {
        photos.setAdapter(new PhotoGridAdapter(items));
    }

    private class FetchItemsTask extends AsyncTask<String, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(String... params) {
            Log.i(LOG_TAG, "* * * FETCHING PHOTOS:" + params[0]);
            Call<List<Photo>> call = restClient.getPhotos("Bearer " + params[0], currentPage, App.ITEMS_PER_PAGE);
            try {
                Response<List<Photo>> response = call.execute();
                Log.i(LOG_TAG, "* * * " + response.code());
                Log.i(LOG_TAG, "* * * " + response.message());
                List<Photo> photos = response.body();
                if (photos != null) {
                    Log.i(LOG_TAG, "* * * PHOTOS:" + photos.size());
                    for (Photo p : photos) {
                        Log.i(LOG_TAG, "* * * PHOTO ID:" + p.getId());
                        Log.i(LOG_TAG, "* * * FULL:" + p.getUrls().getFull());
                        Log.i(LOG_TAG, "* * * REGULAR:" + p.getUrls().getRegular());
                        Log.i(LOG_TAG, "* * * SMALL:" + p.getUrls().getSmall());
                        Log.i(LOG_TAG, "* * * THUMB:" + p.getUrls().getThumb());
                    }
                }
                return photos;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error fetching photos");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            super.onPostExecute(photos);
            setAdapter(photos);
        }
    }
}
