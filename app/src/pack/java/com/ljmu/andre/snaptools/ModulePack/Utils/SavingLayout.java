package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification.ToastType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SaveState;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.Utils.GestureEvent.ReturnType;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode.FLING_TO_SAVE;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDimen;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingLayout extends RelativeLayout {
    private static SavingMode lastSavingMode;
    private static SnapType lastSnapType;
    private static SavingTrigger lastBoundTrigger;
    private Rect chatTouchBounds;
    private GestureEvent gestureEvent;
    private SavingMode savingMode;
    private SnapType snapType;
    private SavingTrigger boundTrigger;

    SavingLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Timber.d("onTouchEvent: " + ev.toString());

        if (savingMode != FLING_TO_SAVE)
            return super.dispatchTouchEvent(ev);

        if (ev.getActionMasked() == 0 && chatTouchBounds != null
                && chatTouchBounds.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            return false;
        }


        ReturnType gestureType = gestureEvent.onTouch(this, ev);

        if (gestureType ==
                GestureEvent.ReturnType.TAP) {
            return super.dispatchTouchEvent(ev);
        } else if (gestureType == ReturnType.SAVE)
            triggerSave();

        return true;
    }

    private void triggerSave() {
        Timber.d("Triggering save");
        SaveState saveState = boundTrigger.triggerSave();

        if (saveState == null) {
            Timber.d("Null savestate... Ignoring");
            return;
        }

        ToastType toastType;

        switch (saveState) {
            case NOT_READY:
                Timber.i("Snap not ready");
                return;
            case EXISTING:
                toastType = SaveNotification.ToastType.WARNING;
                break;
            case FAILED:
                toastType = SaveNotification.ToastType.BAD;
                break;
            case SUCCESS:
                toastType = SaveNotification.ToastType.GOOD;
                break;
            default:
                Timber.e("Unhandled Save State: " + saveState);
                return;
        }

        SaveNotification.show(
                (Activity) getContext(),
                toastType,
                Toast.LENGTH_LONG,
                boundTrigger.getReadySnap()
        );
    }

    public void initSavingMethod(@Nullable SavingMode savingMode, @Nullable SnapType snapType, @Nullable SavingTrigger boundTrigger) {
        if (savingMode == null) {
            savingMode = lastSavingMode;
            Timber.i("Using the last used saving mode of: " + savingMode);
        }

        if (snapType == null)
            snapType = lastSnapType;

        if (boundTrigger == null)
            boundTrigger = lastBoundTrigger;

        if (this.savingMode != null && this.savingMode == savingMode &&
                this.snapType != null && this.snapType == snapType) {
            Timber.i("View already set to saving mode: %s and snap type: %s", savingMode, snapType);
            return;
        }

        Timber.d("LAYOUT DATA: " + getWindowVisibility());

        if (savingMode == null)
            return;

        this.savingMode = savingMode;
        lastSavingMode = savingMode;

        this.snapType = snapType;
        lastSnapType = snapType;

        this.boundTrigger = boundTrigger;
        lastBoundTrigger = boundTrigger;

        if (getChildCount() > 0)
            removeAllViews();

        switch (savingMode) {
            case NONE:
                break;
            case AUTO:
                break;
            case BUTTON:
                setupButtonSaving();
                break;
            case FLING_TO_SAVE:
                setupFlingSaving();
                break;
            default:
                throw new IllegalArgumentException("Unknown saving mode: " + savingMode);
        }
    }

    private void setupButtonSaving() {
        SavingButton savingButton = new SavingButton(getContext(), snapType);

        savingButton.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        triggerSave();
                    }
                }
        );

        addView(savingButton);
    }

    private void setupFlingSaving() {
        Timber.d("Setting up fling");
        gestureEvent = new FlingSaveGesture(snapType);

        ViewGroup parentView = (ViewGroup) getParent();
        boolean hasChatBox = false;

        if (parentView != null) {
            for (int i = 0; i < parentView.getChildCount(); i++) {
                View child = parentView.getChildAt(i);
                String contentDesc = (String) child.getContentDescription();
                Timber.d("Index: %s, Child: %s, Desc: %s", i, child, contentDesc);

                if (contentDesc != null && contentDesc.equals("CHAT")) {
                    hasChatBox = true;
                    break;
                }
            }
        }

        if (!hasChatBox) {
            chatTouchBounds = null;
            return;
        }

        Resources resources = getContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        int screenHeight = displayMetrics.heightPixels;
        int halfScreenWidth = displayMetrics.widthPixels / 2;
        int chatTouchHeight = (int) (((float) screenHeight) * 0.08f);

        int halfChatTouchWidth = resources.getDimensionPixelSize(
                getDimen(getContext(), "swipe_up_arrow_touch_area_width_no_text")
        ) / 2;

        Timber.d("ChatTouchHeight: " + chatTouchHeight);
        Timber.d("HalfChatTouchWidth: " + halfChatTouchWidth);

        chatTouchBounds = new Rect(
                halfScreenWidth - halfChatTouchWidth,
                screenHeight - chatTouchHeight,
                halfScreenWidth + halfChatTouchWidth,
                screenHeight
        );

        Timber.d("Bounds: " + chatTouchBounds.toString());
    }
}
