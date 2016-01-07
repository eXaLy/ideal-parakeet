package io.github.epelde.idealparakeet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginFragment extends Fragment {

    private static final String clientId = "760d26bbedfb5a838416ba044e0cc2bb4a958909d742a216237b540da8865f3e";
    private static final String clientSecret = "81c082d3953a34c73c8107aa9e4e5e5b1b7f57df23b96a04fadc47b2f8d4821e";
    private static final String redirectUri = "idealparakeet://app";
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(App.AUTHORIZATION_BASE_URL +
                        "?client_id=" + clientId +
                        "&redirect_uri=" + redirectUri +
                        "&scope=public+read_user+write_user+read_photos+write_photos+write_likes" +
                        "&response_type=code"));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "* * * LoginFragment paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "* * * LoginFragment stoped");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "* * * LoginFragment resumed");
        Uri uri = getActivity().getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Log.i(LOG_TAG, "CODE:" + code);
            }
        }
    }

}
