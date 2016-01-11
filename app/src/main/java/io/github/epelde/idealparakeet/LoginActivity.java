package io.github.epelde.idealparakeet;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginActivity extends SingleFragmentActivity implements LoginFragment.UserAuthenticatedListener {
    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void authenticationSuccess() {
        startActivity(new Intent(this, PhotoGalleryActivity.class));
    }
}
