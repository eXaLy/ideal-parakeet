package io.github.epelde.idealparakeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginFragment extends Fragment {

    private UserAuthenticatedListener listener;
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    public interface UserAuthenticatedListener {
        public void authenticationSuccess();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(App.AUTHORIZATION_BASE_URL +
                        "authorize?client_id=" + App.CLIENT_ID +
                        "&redirect_uri=" + App.REDIRECT_URI +
                        "&scope=public+read_user+write_user+read_photos+write_photos+write_likes" +
                        "&response_type=code"));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "* * * LoginFragment STARTED");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "* * * LoginFragment PAUSED");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "* * * LoginFragment STOPPED");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "* * * LoginFragment RESUMED");
        Uri uri = getActivity().getIntent().getData();
        if (uri != null && uri.toString().startsWith(App.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                new GetAccessTokenTask().execute(code);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserAuthenticatedListener) {
            listener = (UserAuthenticatedListener) context;
        }
    }

    private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * *");
            Log.i(LOG_TAG, "* * * R U N N I N G T A S K * * *");
            Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * *");
            OAuthClient client = ServiceGenerator.createService(OAuthClient.class, App.AUTHORIZATION_BASE_URL);
            Call<AccessToken> call = client.getAccessToken(App.CLIENT_ID, App.CLIENT_SECRET,
                    App.REDIRECT_URI, params[0], "authorization_code");
            Response<AccessToken> response = null;
            try {
                response = call.execute();
                if (response.isSuccess()) {
                    AccessToken atoken = response.body();
                    if (atoken != null) {
                        Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                        Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                        Log.i(LOG_TAG, "* * * ACCESS TOKEN:" + atoken.getAccessToken());
                        Log.i(LOG_TAG, "* * * TOKEN TYPE:" + atoken.getTokenType());
                        Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                        Log.i(LOG_TAG, "* * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.access_token_file),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.access_token), atoken.getAccessToken());
                        editor.commit();
                        listener.authenticationSuccess();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
