package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.Utils.NotificationLEDUtil;
import com.ljmu.andre.snaptools.ModulePack.Utils.NotificationLEDUtil.NotificationColor;

/**
 * Created by ethan on 10/5/2017.
 */

public class LEDNotification extends SaveNotification {
    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        //TODO: passthrough duration as ms to notification
        switch (type) {
            case GOOD:
                NotificationLEDUtil.flashLED(NotificationColor.GREEN);
                break;
            case BAD:
                NotificationLEDUtil.flashLED(NotificationColor.RED);
                break;
            case SKIPPED:
                NotificationLEDUtil.flashLED(NotificationColor.BLUE);
                break;
            case WARNING:
                NotificationLEDUtil.flashLED(NotificationColor.YELLOW);
                break;
            default:
                break;
        }
    }
}
