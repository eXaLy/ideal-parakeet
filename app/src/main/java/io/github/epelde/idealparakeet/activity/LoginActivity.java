package io.github.epelde.idealparakeet.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.fragment.LoginFragment;
import io.github.epelde.idealparakeet.R;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginActivity extends SingleFragmentActivity implements LoginFragment.AuthorizationProcessListener {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void message(int status) {
        switch (status) {
            case App.AUTHORIZATION_DENIED_STATUS:
                Toast.makeText(this, R.string.msg_authorization_denied,
                        Toast.LENGTH_LONG).show();
                break;
            case App.AUTHORIZATION_ERROR_STATUS:
                Toast.makeText(this, R.string.msg_authorization_error,
                        Toast.LENGTH_LONG).show();
                break;
            case App.AUTHORIZATION_SUCCESS_STATUS:
                startActivity(new Intent(this, PhotoGalleryActivity.class));
                // README
                // LoginActivity is explicity finished in order remove
                // it from history and the backstack.
                finish();
        }
    }

}