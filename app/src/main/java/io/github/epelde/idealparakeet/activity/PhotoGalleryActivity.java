package io.github.epelde.idealparakeet.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import io.github.epelde.idealparakeet.App;
import io.github.epelde.idealparakeet.R;
import io.github.epelde.idealparakeet.fragment.PhotoGalleryFragment;

/**
 * Created by epelde on 29/12/2015.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity implements SingleFragmentActivity.ParentListener {
    @Override
    public Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("custom-event-name"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showToastMessage(intent.getIntExtra("message", -1));
        }
    };

    @Override
    public void message(int status) {
        switch (status) {
            case App.ERROR_STATUS:
                showToastMessage(R.string.msg_authorization_error);
                break;
            case App.AUTHORIZATION_REVOKED_STATUS:
                showToastMessage(R.string.msg_authorization_revoked);
                break;
        }
    }
}
