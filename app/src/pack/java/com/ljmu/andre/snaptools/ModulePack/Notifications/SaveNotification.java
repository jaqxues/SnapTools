package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import timber.log.Timber;

import static android.widget.Toast.LENGTH_LONG;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_NOTIFICATION_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.VIBRATE_ON_SAVE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class SaveNotification {
    private static SaveNotification instance;

    public static void show(Activity activity, ToastType type, int duration) {
        show(activity, type, duration, null);
    }

    public static void show(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        if (getPref(VIBRATE_ON_SAVE)) {
            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(400L);
        }

        SaveNotification notification = getInstance(activity);

        if (notification == null)
            return;

        activity.runOnUiThread(() -> notification.showNotification(activity, type, duration, snap));
    }

    private static SaveNotification getInstance(Activity activity) {
        if (instance != null)
            return instance;

        String notificationName = getPref(SAVE_NOTIFICATION_TYPE);
        NotificationType type = NotificationType.getFromDisplayText(notificationName);

        if (type == null)
            type = NotificationType.DOT;
        try {
            return instance = type.clazz.newInstance();
        } catch (Exception e) {
            Timber.e(e, "Couldn't instantiate SaveNotification: " + type);
        }

        SafeToast.show(activity, "Issue displaying notification... Contact Dev", LENGTH_LONG, true);
        return null;
    }

    protected abstract void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap);

    public enum NotificationType {
        DOT("Dot", DotNotification.class),
        STACKING_DOTS("Stacked Dots", StackingDotNotification.class),
        PREVIEW("Media Preview", PreviewNotification.class),
        LED("Notification LED", LEDNotification.class),
        SCREEN_FLASH("Screen Flash", FlashNotification.class),
        NONE("None", EmptyNotification.class);

        private String displayText;
        private Class<? extends SaveNotification> clazz;

        NotificationType(String displayText, Class<? extends SaveNotification> clazz) {
            this.displayText = displayText;
            this.clazz = clazz;
        }

        public String getDisplayText() {
            return displayText;
        }

        @Nullable
        public static NotificationType getFromDisplayText(String displayText) {
            for (NotificationType type : values()) {
                if (type.displayText.equals(displayText))
                    return type;
            }

            return null;
        }
    }

    public enum ToastType {
        GOOD(Color.rgb(70, 200, 70)),
        WARNING(Color.rgb(255, 140, 0)),
        BAD(Color.rgb(200, 70, 70)),
        SKIPPED(Color.rgb(70, 70, 200));

        private int color;

        ToastType(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
