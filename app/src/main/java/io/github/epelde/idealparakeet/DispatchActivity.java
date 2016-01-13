package io.github.epelde.idealparakeet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by epelde on 12/01/2016.
 */
public class DispatchActivity extends Activity {

    private static final String LOG_TAG = DispatchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.access_token_file), Context.MODE_PRIVATE);
        String token = sharedPref.getString(getString(R.string.access_token), null);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, PhotoGalleryActivity.class));
        }
    }

}
