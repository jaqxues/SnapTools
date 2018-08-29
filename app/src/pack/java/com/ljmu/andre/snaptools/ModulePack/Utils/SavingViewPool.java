package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import timber.log.Timber;

import java.util.ArrayList;

import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getSavingMode;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingViewPool {
    private static ArrayList<SavingLayout> activeLayouts = new ArrayList<>();

    public static void readyUpLayouts(Snap readySnap, SavingTrigger savingTrigger) {
        SnapType snapType = readySnap.getSnapType();
        SavingMode savingMode = getSavingMode(snapType);

        Timber.i("Readying up all layouts for mode: " + savingMode);

        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initAllLayouts(savingMode, snapType, savingTrigger);
                }
            });
        } else
            initAllLayouts(savingMode, snapType, savingTrigger);

    }

    private static void initAllLayouts(SavingMode savingMode, SnapType snapType, SavingTrigger savingTrigger) {
        for (SavingLayout activeLayout : activeLayouts)
            activeLayout.initSavingMethod(savingMode, snapType, savingTrigger);

        Timber.i("Readied up %s layouts", activeLayouts.size());
    }

    public static SavingLayout requestLayout(Context context) {
        SavingLayout deadLayout = getDeadLayout();

        if (deadLayout == null)
            return buildLayout(context);

        if (deadLayout.getParent() != null)
            ((ViewGroup) deadLayout.getParent()).removeView(deadLayout);

        return deadLayout;
    }

    private static SavingLayout getDeadLayout() {
        Timber.d("Total active layouts: " + activeLayouts.size());

        for (SavingLayout savingLayout : activeLayouts) {
            Timber.d("Testing layout for death [Shown: %s]", savingLayout.isShown());

            if (savingLayout.getWindowVisibility() == View.GONE) {
                savingLayout.initSavingMethod(null, null, null);
                savingLayout.setVisibility(View.VISIBLE);
                return savingLayout;
            }
        }

        return null;
    }

    private static SavingLayout buildLayout(Context context) {
        Timber.d("Building fresh layout");
        SavingLayout savingLayout = new SavingLayout(context);
        savingLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        savingLayout.setClickable(false);
        savingLayout.initSavingMethod(null, null, null);

        activeLayouts.add(savingLayout);

        return savingLayout;
    }
}
