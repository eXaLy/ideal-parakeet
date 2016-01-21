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
import java.util.Date;
import java.util.List;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.model.AccessToken;
import io.github.epelde.idealparakeet.model.Photo;
import io.github.epelde.idealparakeet.networking.OAuthClient;
import io.github.epelde.idealparakeet.networking.ServiceGenerator;
import io.github.epelde.idealparakeet.networking.UnsplashClient;
import io.github.epelde.idealparakeet.util.EndlessRecyclerViewScrollListener;
import io.github.epelde.idealparakeet.util.PhotoGridAdapter;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryFragment extends Fragment implements PhotoGridAdapter.LongClickListener {

    private static final String LOG_TAG = PhotoGalleryFragment.class.getSimpleName();

    private RecyclerView photosRecyclerView;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.access_token_file), Context.MODE_PRIVATE);
        new FetchItemsTask().execute();
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
                new FetchItemsTask().execute(page);
            }
        });
        return view;
    }

    private void setAdapter(List<Photo> items) {
        if (photosRecyclerView.getAdapter() == null) {
            photosRecyclerView.setAdapter(new PhotoGridAdapter(items, this));
        } else {
            ((PhotoGridAdapter) photosRecyclerView.getAdapter()).addItems(items);
            photosRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onLongClick(View v, Photo p) {
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        PhotoOverlayDialog dialog = PhotoOverlayDialog.newInstance(p);
        dialog.show(fm, "fragment_photo_overlay");
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Integer... params) {
            String storedAccessToken = sharedPref.getString(getString(R.string.access_token), null);
            UnsplashClient restClient = ServiceGenerator.createService(UnsplashClient.class, App.API_BASE_URL, storedAccessToken);
            int page = 1;

            if (params.length > 0) {
                page = params[0];
            }

            Log.i(LOG_TAG, "* * * FETCHING PHOTOS PAGE:" + page);
            Log.i(LOG_TAG, "* * * ACCESS TOKEN:" + storedAccessToken);
            Log.i(LOG_TAG, "* * * CREATED_AT:" + sharedPref.getInt(getString(R.string.access_token_created), 0));
            if (sharedPref.getInt(getString(R.string.access_token_created), 0) != 0) {
                Date createdAt = new Date(sharedPref.getInt(getString(R.string.access_token_created), 0));
                Log.i(LOG_TAG, "* * * CREATED_AT_DATE:" + createdAt.toString());
            }
            Log.i(LOG_TAG, "* * * EXPIRES_IN:" + sharedPref.getInt(getString(R.string.access_token_expiration), 0));
            Log.i(LOG_TAG, "* * * REFRESH TOKEN:" + sharedPref.getString(getString(R.string.refresh_token), null));

            Call<List<Photo>> call = restClient.getPhotos(page);
            try {
                Response<List<Photo>> response = call.execute();
                if (response.isSuccess()) {
                    /*
                    List<Photo> photos = response.body();
                    if (photos != null) {
                        for (Photo p : photos) {
                            Log.i(LOG_TAG, "* * * PHOTO ID:" + p.getId());
                            Log.i(LOG_TAG, "* * * FULL:" + p.getUrls().getFull());
                            Log.i(LOG_TAG, "* * * REGULAR:" + p.getUrls().getRegular());
                            Log.i(LOG_TAG, "* * * SMALL:" + p.getUrls().getSmall());
                            Log.i(LOG_TAG, "* * * THUMB:" + p.getUrls().getThumb());
                        }
                    }
                    */
                    return response.body();
                }

                Log.e(LOG_TAG, "* * * ERROR FETCHING PHOTOS " + response.code() + "-" + response.message());

                // access_token expires
                if (response.code() == 401) {
                    String refreshToken = sharedPref.getString(getString(R.string.refresh_token), null);
                    Log.e(LOG_TAG, "* * * USING REFRESH TOKEN:" + refreshToken);
                    OAuthClient oauthClient = ServiceGenerator.createService(OAuthClient.class, App.AUTHORIZATION_BASE_URL);
                    Call<AccessToken> oauthCall = oauthClient.refreshToken(App.CLIENT_ID, App.CLIENT_SECRET,
                            refreshToken, "refresh_token");
                    Response<AccessToken> oauthRefreshResponse = oauthCall.execute();
                    Log.e(LOG_TAG, "* * * REFRESH RESPONSE:" + oauthRefreshResponse.code() + "-" + oauthRefreshResponse.message());
                    if (oauthRefreshResponse.isSuccess()) {
                        Log.i(LOG_TAG, "* * * REFRESH SUCCESS");
                        AccessToken accessToken = oauthRefreshResponse.body();
                        // Saving access token to Shared Preferences
                        Log.i(LOG_TAG, "* * * NEW ACCESS TOKEN:" + accessToken.getAccessToken());
                        Log.i(LOG_TAG, "* * * NEW ACCESS TOKEN CREATED AT:" + accessToken.getCreatedAt());
                        Date tmp = new Date(accessToken.getCreatedAt());
                        Log.i(LOG_TAG, "* * * CREATED AT:" + tmp.toString());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.access_token), accessToken.getAccessToken());
                        editor.putInt(getString(R.string.access_token_expiration), accessToken.getExpiresIn());
                        editor.putString(getString(R.string.refresh_token), accessToken.getRefreshToken());
                        editor.putInt(getString(R.string.access_token_created), accessToken.getCreatedAt());
                        editor.commit();
                        restClient = ServiceGenerator.createService(UnsplashClient.class, App.API_BASE_URL, accessToken.getAccessToken());
                        call = restClient.getPhotos(page);
                        response = call.execute();
                        Log.i(LOG_TAG, "* * * REQUESTING PHOTOS WITH NEW ACCESS TOKEN " + response.code() + "-" + response.message());
                        if (response.isSuccess()) {
                            Log.i(LOG_TAG, "* * * " + response.body());
                            return response.body();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error fetching Unsplash photos: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            super.onPostExecute(photos);
            if (photos != null && !photos.isEmpty()) {
                setAdapter(photos);
            }
        }
    }
}
