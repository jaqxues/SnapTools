package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FlashNotification extends SaveNotification {
    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        View screenFiller = createFlashView(activity, type);
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        decor.addView(screenFiller, -1);
        AnimationUtils.pulseWRemove(screenFiller, 300);
    }

    private View createFlashView(Activity activity, ToastType toastType) {
        View screenFiller = new View(activity);
        screenFiller.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        String colorString = toastType == ToastType.BAD ? "#88FF0000" : "#88FFFFFF";
        screenFiller.setBackgroundColor(Color.parseColor(colorString));
        screenFiller.setVisibility(View.GONE);
        screenFiller.setId(getIdFromString("SCREEN_FLASH"));
        return screenFiller;
    }
}
