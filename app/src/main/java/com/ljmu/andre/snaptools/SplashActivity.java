package com.ljmu.andre.snaptools;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("SPLASH");

        openMainActivity();
    }

    private void openMainActivity() {
        // Unnecessary, FirebaseRemoteConfig not available
//        if (!STApplication.DEBUG)
//            Constants.initConstants();

        Intent intent;

        if (getIntent().getExtras() != null) {
            intent = new Intent(getIntent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else
            intent = new Intent();

        intent.setComponent(new ComponentName(this, MainActivity.class));
        startActivity(intent);
        finish();
    }
}
