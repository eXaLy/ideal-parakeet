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
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.model.Photo;
import io.github.epelde.idealparakeet.networking.ServiceGenerator;
import io.github.epelde.idealparakeet.networking.UnsplashClient;
import io.github.epelde.idealparakeet.util.EndlessRecyclerViewScrollListener;
import io.github.epelde.idealparakeet.util.PhotoGridAdapter;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryFragment extends Fragment {

    private static final String LOG_TAG = PhotoGalleryFragment.class.getSimpleName();

    private RecyclerView photosRecyclerView;
    private UnsplashClient restClient;
    private String accessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.access_token_file), Context.MODE_PRIVATE);
        accessToken = sharedPref.getString(getString(R.string.access_token), null);
        restClient = ServiceGenerator.createService(UnsplashClient.class, App.API_BASE_URL);
        new FetchItemsTask().execute(accessToken);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photosRecyclerView = (RecyclerView) view.findViewById(R.id.photos);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), App.SPAN_COUNT);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                Log.i(LOG_TAG, "* * * onLoadMore " + page);
                new FetchItemsTask().execute(accessToken, String.valueOf(page));
            }
        });
        return view;
    }

    private void setAdapter(List<Photo> items) {
        if (photosRecyclerView.getAdapter() == null) {
            Log.i("XXX", "* * * LOAD INITIAL PHOTOS " + items.size());
            photosRecyclerView.setAdapter(new PhotoGridAdapter(items));
        } else {
            Log.i("XXX", "* * * LOAD MORE PHOTOS " + items.size());
            ((PhotoGridAdapter)photosRecyclerView.getAdapter()).addItems(items);
            photosRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private class FetchItemsTask extends AsyncTask<String, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(String... params) {
            Log.i("XXX", "* * * PARAMS:" + params.length);
            Log.i(LOG_TAG, "* * * FETCHING PHOTOS:" + params[0]);
            int page = 1;
            if (params.length > 1) {
                Log.i(LOG_TAG, "* * * PAGE:" + params[1]);
                page = Integer.parseInt(params[1]);
            }
            Call<List<Photo>> call = restClient.getPhotos("Bearer " + params[0],
                   page, App.ITEMS_PER_PAGE);
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
                Log.e(LOG_TAG, "Error fetching photosRecyclerView");
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
