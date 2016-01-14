package io.github.epelde.idealparakeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    private AuthorizationProcessListener listener;

    public interface AuthorizationProcessListener {
        public void message(int status);
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
                // request access token after user authorization
                new GetAccessTokenTask().execute(code);
            } else {
                // authorization denied by user (error=access_denied)
                String error = uri.getQueryParameter("error");
                if (error != null && error.equals("access_denied")) {
                    listener.message(App.AUTHORIZATION_DENIED_STATUS);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthorizationProcessListener) {
            listener = (AuthorizationProcessListener) context;
        }
    }

    private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            OAuthClient client = ServiceGenerator.createService(OAuthClient.class, App.AUTHORIZATION_BASE_URL);
            Call<AccessToken> call = client.getAccessToken(App.CLIENT_ID, App.CLIENT_SECRET,
                    App.REDIRECT_URI, params[0], "authorization_code");
            Response<AccessToken> response = null;
            try {
                response = call.execute();
                if (response.isSuccess()) {
                    AccessToken accessToken = response.body();
                    if (accessToken != null) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.access_token_file),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.access_token), accessToken.getAccessToken());
                        editor.putInt(getString(R.string.access_token_expiration), accessToken.getExpiresIn());
                        editor.putString(getString(R.string.refresh_token), accessToken.getRefreshToken());
                        editor.putInt(getString(R.string.access_token_created), accessToken.getCreatedAt());
                        editor.commit();
                        listener.message(App.AUTHORIZATION_SUCCESS_STATUS);
                    } else {
                        listener.message(App.AUTHORIZATION_ERROR_STATUS);
                    }
                } else {
                    listener.message(App.AUTHORIZATION_ERROR_STATUS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
