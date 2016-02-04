package io.github.epelde.idealparakeet.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import io.github.epelde.idealparakeet.fragment.LoginFragment;

/**
 * Created by epelde on 07/01/2016.
 */
public class LoginActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
