package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;

import static com.ljmu.andre.snaptools.ModulePack.Notifications.NotificationPrefHelper.getDotLocation;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DotNotification extends SaveNotification {

    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        DotLocation location = getDotLocation();
        Toast toast = new Toast(activity);
        toast.setGravity(location.getGravity(), 20, 20);
        toast.setDuration(duration);
        toast.setView(createCircleView(activity, type));
        toast.show();
    }

    ImageView createCircleView(Activity activity, ToastType type) {
        int notificationDrawableId = getDrawable(getModuleContext(activity), "notification_circle");
        Drawable circle = getModuleResources(activity).getDrawable(notificationDrawableId);
        circle.setColorFilter(new
                PorterDuffColorFilter(type.getColor(), PorterDuff.Mode.MULTIPLY));

        ImageView view = new ImageView(activity);
        view.setImageDrawable(circle);
        view.bringToFront();
        return view;
    }

    @SuppressLint("RtlHardcoded")
    public enum DotLocation {
        TOP_LEFT("Top Left", Gravity.LEFT | Gravity.TOP, -1, -1),
        TOP_RIGHT("Top Right", Gravity.RIGHT | Gravity.TOP, -1, 0),
        BOTTOM_LEFT("Bottom Left", Gravity.LEFT | Gravity.BOTTOM, 0, -1),
        BOTTOM_RIGHT("Bottom Right", Gravity.RIGHT | Gravity.BOTTOM, 0, 0);

        private String displayText;
        private int gravity;
        private int verticalPos;
        private int horizontalPos;

        DotLocation(String displayText, int gravity, int verticalPos, int horizontalPos) {
            this.displayText = displayText;
            this.gravity = gravity;
            this.verticalPos = verticalPos;
            this.horizontalPos = horizontalPos;
        }

        public String getDisplayText() {
            return displayText;
        }

        public int getGravity() {
            return gravity;
        }

        public int getVerticalPos() {
            return verticalPos;
        }

        public int getHorizontalPos() {
            return horizontalPos;
        }

        @Nullable
        public static DotLocation getFromDisplayText(String displayText) {
            for (DotLocation location : values()) {
                if (location.displayText.equals(displayText))
                    return location;
            }

            return null;
        }
    }
}
