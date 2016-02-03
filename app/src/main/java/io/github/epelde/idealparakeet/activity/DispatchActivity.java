package io.github.epelde.idealparakeet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import io.github.epelde.idealparakeet.R;

/**
 * Created by epelde on 12/01/2016.
 */
public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.ACCESS_TOKEN_FILE), Context.MODE_PRIVATE);
        String token = sharedPref.getString(getString(R.string.ACCESS_TOKEN_FILE_ACCESS_TOKEN), null);
        if (token == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, PhotoGalleryActivity.class));
        }
    }

}
