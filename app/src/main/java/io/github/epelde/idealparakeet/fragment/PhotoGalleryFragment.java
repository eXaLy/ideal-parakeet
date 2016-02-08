package io.github.epelde.idealparakeet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.okhttp.Headers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.activity.LoginActivity;
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
    private PhotoOverlayDialog dialog;

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
        photosRecyclerView = (RecyclerView) view.findViewById(R.id.photos);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), App.SPAN_COUNT);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                new FetchItemsTask().execute(page);
            }

            @Override
            public void displayLoadingIndicator() {
                Toast.makeText(getContext(), "Loading images", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void setAdapter(List<Photo> items) {
        if (photosRecyclerView.getAdapter() == null) {
            photosRecyclerView.setAdapter(new PhotoGridAdapter(items, this));
        } else {
            ((PhotoGridAdapter) photosRecyclerView.getAdapter()).addItems(items);
        }
    }

    @Override
    public void onLongClick(View v, Photo p) {
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        dialog = PhotoOverlayDialog.newInstance(p);
        dialog.show(fm, "fragment_photo_overlay");
    }

    @Override
    public void onLongClickReleased() {
        if (dialog.isVisible()) {
            dialog.dismiss();
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Integer... params) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.ACCESS_TOKEN_FILE), Context.MODE_PRIVATE);
            String storedAccessToken = sharedPref.getString(getString(R.string.ACCESS_TOKEN_FILE_ACCESS_TOKEN), null);
            UnsplashClient restClient = ServiceGenerator.createService(UnsplashClient.class, App.API_BASE_URL, storedAccessToken);
            // default page value
            int page = 1;
            if (params.length > 0) {
                page = params[0];
            }

            Log.i(LOG_TAG, "* * * FETCHING PHOTOS PAGE:" + page);
            Log.i(LOG_TAG, "* * * ACCESS TOKEN:" + storedAccessToken);
            Log.i(LOG_TAG, "* * * REFRESH TOKEN:" + sharedPref.getString(getString(R.string.ACCESS_TOKEN_FILE_REFRESH_TOKEN), null));

            Call<List<Photo>> call = restClient.getPhotos(page);
            try {
                Response<List<Photo>> response = call.execute();
                if (response.isSuccess()) {
                    Headers headers = response.headers();
                    Set<String> names = headers.names();
                    Log.i(LOG_TAG, "NAMES:" + headers.names().size());
                    for (String name : names) {
                        Log.i(LOG_TAG, "NAME:" + name);
                    }
                    //Log.i("* * *", "X-Ratelimit-Limit:" + headers.get("X-Ratelimit-Limit"));
                    //Log.i("* * *", "X-Ratelimit-Remaining:" + headers.get("X-Ratelimit-Remaining"));
                    Log.i(LOG_TAG, "* * * END");
                    return response.body();
                }

                Log.e(LOG_TAG, "* * * ERROR FETCHING PHOTOS " + response.code() + "-" + response.message());
                Headers headers = response.headers();
                Log.i(LOG_TAG, "X-Ratelimit-Limit:" + headers.get("X-Ratelimit-Limit"));
                Log.i(LOG_TAG, "X-Ratelimit-Remaining:" + headers.get("X-Ratelimit-Remaining"));

                // access_token expires or access revoked!!!!
                if (response.code() == 401) {
                    // access_token has expired
                    String refreshToken = sharedPref.getString(getString(R.string.ACCESS_TOKEN_FILE_REFRESH_TOKEN), null);
                    Log.e(LOG_TAG, "* * * USING REFRESH TOKEN:" + refreshToken);
                    OAuthClient oauthClient = ServiceGenerator.createService(OAuthClient.class, App.AUTHORIZATION_BASE_URL);
                    Call<AccessToken> oauthCall = oauthClient.refreshToken(App.CLIENT_ID, App.CLIENT_SECRET,
                            refreshToken, "refresh_token");
                    Response<AccessToken> oauthRefreshResponse = oauthCall.execute();
                    if (oauthRefreshResponse.isSuccess()) {
                        AccessToken accessToken = oauthRefreshResponse.body();
                        // Saving access token to Shared Preferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.ACCESS_TOKEN_FILE_ACCESS_TOKEN), accessToken.getAccessToken());
                        editor.putString(getString(R.string.ACCESS_TOKEN_FILE_REFRESH_TOKEN), accessToken.getRefreshToken());
                        editor.commit();
                        restClient = ServiceGenerator.createService(UnsplashClient.class,
                                App.API_BASE_URL, accessToken.getAccessToken());
                        call = restClient.getPhotos(page);
                        response = call.execute();
                        if (response.isSuccess()) {
                            return response.body();
                        }
                    } else {
                        // access has been revoked
                        Log.e(LOG_TAG, "Seems like access to Unsplah has been revoked");
                        LocalBroadcastManager.getInstance(getContext())
                                .sendBroadcast(new Intent(getResources()
                                        .getString(R.string.INTENT_SHOW_TOAST_MESSAGE))
                                        .putExtra("message", R.string.msg_authorization_revoked));
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error requesting Unsplash API: " + e.getMessage());
                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(new Intent(getResources()
                                .getString(R.string.INTENT_SHOW_TOAST_MESSAGE))
                                .putExtra("message", R.string.msg_authorization_error));
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
