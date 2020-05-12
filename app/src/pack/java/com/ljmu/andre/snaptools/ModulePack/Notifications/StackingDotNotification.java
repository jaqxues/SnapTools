package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;

import java.lang.ref.WeakReference;

import timber.log.Timber;

import static android.widget.Toast.LENGTH_LONG;
import static com.ljmu.andre.snaptools.ModulePack.Notifications.NotificationPrefHelper.getDotLocation;
import static com.ljmu.andre.snaptools.ModulePack.Notifications.NotificationPrefHelper.getStackOrientation;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StackingDotNotification extends DotNotification {
    private final int MESSAGE_TOAST_DESTROYED = 0;
    private final int MESSAGE_ICON_TIMEOUT = 1;
    private final int MESSAGE_ICON_FORCE_REMOVE = 2;
    private WeakReference<LinearLayout> holderReference;
    private WeakReference<Toast> toastReference;
    private Handler deathPoller;

    // ===========================================================================

    /**
     * ===========================================================================
     * Display or add to the Status notification
     * ===========================================================================
     *
     * @param activity - The activity to run on
     * @param type     - The colour the Status should be {@link ToastType}
     * @param duration - The duration to display the status for LONG/SHORT
     */
    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        DotLocation location = getDotLocation();
        StackingOrientation orientation = getStackOrientation();
        Toast toast = getToast(activity, location, orientation);
        toast.setDuration(duration);

        //noinspection deprecation
        int notificationDrawableId = getDrawable(getModuleContext(activity), "notification_circle");
        Drawable circle = getModuleResources(activity).getDrawable(notificationDrawableId);
        circle.setColorFilter(new
                PorterDuffColorFilter(type.getColor(), PorterDuff.Mode.MULTIPLY));

        ImageView circleView = createCircleView(activity, type);

        int layoutPos;
        if (orientation == StackingOrientation.HORIZONTAL)
            layoutPos = location.getHorizontalPos();
        else
            layoutPos = location.getVerticalPos();

        ((LinearLayout) toast.getView()).addView(circleView, layoutPos);

        toast.show();

        if (deathPoller == null)
            deathPoller = new ToastDeathPoller(Looper.getMainLooper());

        deathPoller.sendMessageDelayed(
                Message.obtain(deathPoller, MESSAGE_ICON_TIMEOUT, circleView),
                duration == LENGTH_LONG ? 3000 : 1500
        );

        if (!deathPoller.hasMessages(MESSAGE_TOAST_DESTROYED))
            deathPoller.sendMessageDelayed(Message.obtain(deathPoller, MESSAGE_TOAST_DESTROYED, toast), 1000);
        else
            Timber.d("Already polling");
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * Get the currently displayed Status or create if null
     * ===========================================================================
     */
    private Toast getToast(Context context, DotLocation location,
                           StackingOrientation orientation) {
        Toast toast;

        Timber.d("Getting Toast");

        if (toastReference == null || (toast = toastReference.get()) == null) {
            Timber.d("ToastRefStart: " + toastReference);

            toast = buildToast(context, location, orientation);
            toastReference = new WeakReference<>(toast);
            Timber.d("ToastRefEnd: " + toastReference);
            return toast;
        }

        Timber.d("BuiltLayout : " + toast);

        return toast;
    }

    /**
     * ===========================================================================
     * Build the Status toast and bind a holder to it
     * ===========================================================================
     */
    private Toast buildToast(Context context, DotLocation location,
                             StackingOrientation orientation) {
        Toast statusToast = new Toast(context);
        statusToast.setGravity(location.getGravity(), 20, 20);
        statusToast.setView(getHolder(context, orientation));

        return statusToast;
    }

    /**
     * ===========================================================================
     * Get the currently bound Holder or create if null
     * ===========================================================================
     */
    private LinearLayout getHolder(Context context, StackingOrientation orientation) {
        LinearLayout holderLayout;

        Timber.d("Getting Holder");

        if (holderReference == null || (holderLayout = holderReference.get()) == null) {
            Timber.d("ReferenceStart: " + holderReference);

            holderLayout = buildHolder(context, orientation);
            holderReference = new WeakReference<>(holderLayout);
            Timber.d("ReferenceEnd: " + holderReference);
            return holderLayout;
        }

        Timber.d("BuiltLayout : " + holderLayout);
        return holderLayout;
    }

    /**
     * ===========================================================================
     * Build the Holder layout
     * ===========================================================================
     */
    private static LinearLayout buildHolder(Context context, StackingOrientation orientation) {
        LinearLayout holderLayout = new LinearLayout(context);
        holderLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        holderLayout.setOrientation(orientation.orientation);

        //holderLayout.setBackgroundColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        return holderLayout;
    }

    // ===========================================================================

    public enum StackingOrientation {
        HORIZONTAL("Horizontal", LinearLayout.HORIZONTAL),
        VERTICAL("Vertical", LinearLayout.VERTICAL);

        private String displayText;
        private int orientation;

        StackingOrientation(String displayText, int orientation) {
            this.displayText = displayText;
            this.orientation = orientation;
        }

        public String getDisplayText() {
            return displayText;
        }

        @Nullable
        public static StackingOrientation getFromDisplayText(String displayText) {
            for (StackingOrientation orientation : values()) {
                if (orientation.displayText.equals(displayText))
                    return orientation;
            }

            return null;
        }
    }

    /**
     * ===========================================================================
     * Poller for destroying the Status and animating child views
     * ===========================================================================
     */
    private class ToastDeathPoller extends Handler {
        ToastDeathPoller(Looper looper) {
            super(looper);
        }

        // ===========================================================================

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_TOAST_DESTROYED:
                    handleToastDestroyed(msg);
                    break;
                case MESSAGE_ICON_TIMEOUT:
                    handleIconAnim(msg);
                    break;
                case MESSAGE_ICON_FORCE_REMOVE:
                    handleForceIconRemove(msg);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown message id:" + msg.what);
            }
        }

        // ===========================================================================

        private void handleToastDestroyed(Message msg) {
            Toast toast = (Toast) msg.obj;

            if (toast.getView() == null || !toast.getView().isShown()) {
                holderReference.clear();
                toastReference.clear();
                return;
            }

            Message repeatMessage = Message.obtain();
            repeatMessage.copyFrom(msg);
            deathPoller.sendMessageDelayed(repeatMessage, 500);
        }

        // ===========================================================================

        private void handleIconAnim(Message msg) {
            ImageView icon = (ImageView) msg.obj;
            AnimationUtils.fadeOutWRemove(icon, true);

            Message forceRemoveMessage = Message.obtain(deathPoller, MESSAGE_ICON_FORCE_REMOVE, icon);
            deathPoller.sendMessageDelayed(forceRemoveMessage, 500);
        }

        // ===========================================================================

        private void handleForceIconRemove(Message msg) {
            ImageView icon = (ImageView) msg.obj;

            if (icon == null) {
                Timber.w("Null icon when forcing removal");
                return;
            }

            ViewGroup parent = (ViewGroup) icon.getParent();

            if (parent == null) {
                Timber.w("Null parent when forcing removal");
                return;
            }

            parent.removeView(icon);
            Timber.d("Forced removal of status toast icon");
        }
    }
}
