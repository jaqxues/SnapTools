package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class EmptyNotification extends SaveNotification {
    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        // Do Nothing
    }
}
