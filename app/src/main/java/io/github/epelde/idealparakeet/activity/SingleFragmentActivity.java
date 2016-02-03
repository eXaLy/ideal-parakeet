package io.github.epelde.idealparakeet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import io.github.epelde.idealparakeet.R;

/**
 * Created by epelde on 29/12/2015.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, createFragment())
                    .commit();
        }
    }

    public void showToastMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public interface ParentListener {
        public void message(int status);
    }

    public abstract Fragment createFragment();
}
