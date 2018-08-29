package com.ljmu.andre.snaptools;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Repackaging.RepackageManager;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_PREF;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.d("SPLASH");

        initPreferences();

        // For re-installations, avoiding conflicts
        String pkgName = getPackageName();
        boolean equals = pkgName.equals(SplashActivity.class.getPackage().getName());
        if (getPref(REPACKAGE_NAME) != null == equals)
            putPref(REPACKAGE_NAME, (equals ? null : pkgName));

        if (checkAndRepackage()) {
            Timber.d("Repackaging is recommended... Asking User");
            return;
        }
        openMainActivity();
    }

    /**
     * ===========================================================================
     * Preference System Initialisation
     * ===========================================================================
     */
    private void initPreferences() {
        try {
            Preferences.init(
                    Preferences.getExternalPath() + "/" + STApplication.MODULE_TAG + "/"
            );
        } catch (Exception e) {
            Timber.e(e);

            DialogFactory.createErrorDialog(
                    this,
                    "Error Initialising Preferences",
                    "Preference system not loaded. The reason is likely to be permission issues. The application will terminate to preserve data integrity"
                            + "\n\n"
                            + "Reason: " + e.getMessage(),
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            finish();
                        }
                    }
            ).setDismissable(false).show();
        }
    }

    private boolean checkAndRepackage() {
        if (!Preferences.getIsInitialised().get()) {
            return false;
        }

        String storedRepackageName = getPref(REPACKAGE_NAME);

        if (storedRepackageName != null && storedRepackageName.equals(getPackageName())) {
            Timber.d("Application already repackaged... Allowing progress to MainActivity");
            return false;
        }

        // Check if user has already been asked to repackage
        if (getPref(REPACKAGE_PREF)) {
            DialogFactory.createConfirmation(
                    this,
                    "Repackage SnapTools?",
                    "Do you want to repackage SnapTools to circumvent Snapchat's malicious app detection?",
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            repackage();
                        }
                    }, new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            putPref(REPACKAGE_PREF, false);
                            openMainActivity();
                        }
                    }).show();
            return true;
        }
        return false;
    }

    private void repackage() {
        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                this,
                "Repackaging SnapTools",
                "Repackaging is required to circumvent Snapchat malicious app discovery",
                false
        );

        progressDialog.show();

        // ===========================================================================
        Observable.<String>create(e ->
                RepackageManager.repackageApplication(SplashActivity.this, e))
                // ===========================================================================
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        progressDialog.<Progress>getExtension()
                                .setMessage(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressDialog.dismiss();

                        DialogFactory.createErrorDialog(
                                SplashActivity.this,
                                "Error Repackaging Application",
                                e.getMessage()
                        ).show();
                    }
                });
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
