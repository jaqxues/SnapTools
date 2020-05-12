package com.ljmu.andre.GsonPreferences;

import android.os.FileObserver;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class PreferenceObserver extends FileObserver {
    private static AtomicBoolean isLocalChange = new AtomicBoolean(false);

    PreferenceObserver(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        Timber.d("Preference Observer Event: " + event);

        if (isLocalChange.get()) {
            Timber.d("Local event occurred... Skipping reload");

            if (event == FileObserver.CLOSE_NOWRITE || event == FileObserver.CLOSE_WRITE) {
                Timber.d("Finished local events");
                isLocalChange.set(false);
            }

            return;
        }

        switch (event) {
            case FileObserver.CLOSE_WRITE:
                Timber.d("Preference closed with potential changes");
                Preferences.loadPreferenceMap();
                break;
            case FileObserver.CLOSE_NOWRITE:
                Timber.d("Preference closed with no changes");
                break;
        }
    }

    void notifyLocalChange() {
        isLocalChange.set(true);
    }
}
