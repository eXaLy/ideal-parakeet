package io.github.epelde.idealparakeet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.activity.PhotoGalleryActivity;
import io.github.epelde.idealparakeet.model.AccessToken;
import io.github.epelde.idealparakeet.networking.OAuthClient;
import io.github.epelde.idealparakeet.networking.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginFragment extends Fragment {

    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // README
                // Setting intent's data to null in order to not displaying
                // error toast messages when back button is pressed.
                // 1. Request authorization
                // 2. Deny authorization
                // 3. "Authorization denied" toast is displayed in LoginActivity
                // 4. Request authorization again
                // 5. Push back button
                // 6. "Authorization denied" toast is displayed AGAIN in LoginActivity
                // This behaviour is what we are trying to avoid setting data to null.
                getActivity().getIntent().setData(null);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(App.AUTHORIZATION_BASE_URL +
                        "authorize?client_id=" + App.CLIENT_ID +
                        "&redirect_uri=" + App.REDIRECT_URI +
                        "&scope=public+read_user+write_user+read_photos+write_photos+write_likes" +
                        "&response_type=code"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Uri uri = getActivity().getIntent().getData();
        if (uri != null && uri.toString().startsWith(App.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(new Intent(getResources()
                                .getString(R.string.INTENT_SHOW_TOAST_MESSAGE))
                                .putExtra("message", R.string.msg_authorization_in_progress));
                // request access token after user authorization
                new GetAccessTokenTask().execute(code);
            } else {
                // check if authorization was denied by user (error=access_denied)
                String error = uri.getQueryParameter("error");
                if (error != null && error.equals("access_denied")) {
                    Log.e(LOG_TAG, error);
                    LocalBroadcastManager.getInstance(getContext())
                            .sendBroadcast(new Intent(getResources()
                                    .getString(R.string.INTENT_SHOW_TOAST_MESSAGE))
                                    .putExtra("message", R.string.msg_authorization_denied));
                }
            }
        }
    }

    private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... code) {
            OAuthClient client = ServiceGenerator.createService(OAuthClient.class, App.AUTHORIZATION_BASE_URL);
            Call<AccessToken> call = client.getAccessToken(App.CLIENT_ID, App.CLIENT_SECRET,
                    App.REDIRECT_URI, code[0], "authorization_code");
            Response<AccessToken> response = null;
            try {
                response = call.execute();
                if (response.isSuccess()) {
                    AccessToken accessToken = response.body();
                    if (accessToken != null) {
                        // Saving access token to Shared Preferences
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.ACCESS_TOKEN_FILE),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.ACCESS_TOKEN_FILE_ACCESS_TOKEN), accessToken.getAccessToken());
                        editor.putString(getString(R.string.ACCESS_TOKEN_FILE_REFRESH_TOKEN), accessToken.getRefreshToken());
                        editor.commit();
                        startActivity(new Intent(getContext(), PhotoGalleryActivity.class));
                        // README
                        // Authorization success
                        // LoginActivity is explicity finished in order
                        // to remove it from history and the backstack.
                        // LoginActivity doesn`t need to be displayed again anymore!
                        getActivity().finish();
                    }
                } else {
                    LocalBroadcastManager.getInstance(getContext())
                            .sendBroadcast(new Intent(getResources()
                                    .getString(R.string.INTENT_SHOW_TOAST_MESSAGE))
                                    .putExtra("message", R.string.msg_authorization_error));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
