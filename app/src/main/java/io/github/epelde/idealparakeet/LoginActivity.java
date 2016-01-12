package io.github.epelde.idealparakeet;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginActivity extends SingleFragmentActivity implements LoginFragment.UserAuthenticatedListener {
    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void setStatus(int status) {
        switch (status) {
            case App.AUTHORIZATION_DENIED_STATUS:
                Toast.makeText(this, getString(R.string.authization_msg),
                        Toast.LENGTH_LONG).show();
                break;
            case App.AUTHORIZATION_SUCCESS_STATUS:
                startActivity(new Intent(this, PhotoGalleryActivity.class));
                break;
        }
    }

}
