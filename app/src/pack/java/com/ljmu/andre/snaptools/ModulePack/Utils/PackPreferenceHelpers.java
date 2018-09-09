package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthLocationOverlay.StealthPosition;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.Utils.SavingButton.ButtonLocation;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTER_SCALING_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FLING_VELOCITY;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_BUTTON_LOCATIONS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_BUTTON_OPACITIES;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_BUTTON_RELATIVE_HEIGHTS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_BUTTON_WIDTHS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVING_MODES;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_SNAP_BUTTON_LOCATION;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackPreferenceHelpers {
    public static SavingMode getSavingMode(SnapType snapType) {
        return getSavingMode(snapType.getName());
    }

    public static SavingMode getSavingMode(String snapTypeName) {
        String savingModeName = PreferenceHelpers.getFromMap(SAVING_MODES, snapTypeName);
        SavingMode savingMode;

        if (savingModeName == null)
            savingMode = SavingMode.AUTO;
        else
            savingMode = SavingMode.fromName(savingModeName);

        return savingMode;
    }

    public static Integer getButtonOpacity(SnapType snapType) {
        return getButtonOpacity(snapType.getName());
    }

    public static int getButtonOpacity(String snapTypeName) {
        Number buttonOpacity = PreferenceHelpers.getFromMap(SAVE_BUTTON_OPACITIES, snapTypeName);

        if (buttonOpacity == null)
            buttonOpacity = 75;

        return buttonOpacity.intValue();
    }

    public static Integer getButtonWidth(SnapType snapType) {
        return getButtonWidth(snapType.getName());
    }

    public static Integer getButtonWidth(String snapTypeName) {
        Number buttonWidth = PreferenceHelpers.getFromMap(SAVE_BUTTON_WIDTHS, snapTypeName);

        if (buttonWidth == null)
            buttonWidth = 20;

        return buttonWidth.intValue();
    }

    public static Integer getButtonHeightAspect(SnapType snapType) {
        return getButtonHeightAspect(snapType.getName());
    }

    public static Integer getButtonHeightAspect(String snapTypeName) {
        Number buttonHeightAspect = PreferenceHelpers.getFromMap(SAVE_BUTTON_RELATIVE_HEIGHTS, snapTypeName);

        if (buttonHeightAspect == null)
            buttonHeightAspect = 100;

        return buttonHeightAspect.intValue();
    }

    public static ButtonLocation getButtonLocation(SnapType snapType) {
        return getButtonLocation(snapType.getName());
    }

    public static ButtonLocation getButtonLocation(String snapTypeName) {
        String buttonLocationName = PreferenceHelpers.getFromMap(SAVE_BUTTON_LOCATIONS, snapTypeName);

        if (buttonLocationName == null)
            return ButtonLocation.BOTTOM_LEFT;

        return ButtonLocation.getFromDisplayText(buttonLocationName);
    }

    public static Integer getFlingVelocity(SnapType snapType) {
        return getFlingVelocity(snapType.getName());
    }

    public static Integer getFlingVelocity(String snapTypeName) {
        Number buttonSize = PreferenceHelpers.getFromMap(FLING_VELOCITY, snapTypeName);

        if (buttonSize == null)
            buttonSize = 2000;

        return buttonSize.intValue();
    }

    public static ScaleType getFilterScaleType() {
        try {
            return ImageView.ScaleType.valueOf(getPref(FILTER_SCALING_TYPE));
        } catch (Exception ignored) {
        }

        return ImageView.ScaleType.valueOf(FILTER_SCALING_TYPE.getDefaultVal());
    }

    public static StealthPosition getStealthLocation() {
        String selectedPositionString = getPref(STEALTH_SNAP_BUTTON_LOCATION);
        StealthPosition stealthPosition = StealthPosition.TOP;

        if (selectedPositionString != null) {
            try {
                stealthPosition = StealthPosition.valueOf(selectedPositionString);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return stealthPosition;
    }
}
