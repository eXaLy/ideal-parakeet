package io.github.epelde.idealparakeet;

import android.support.v4.app.Fragment;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }
}
