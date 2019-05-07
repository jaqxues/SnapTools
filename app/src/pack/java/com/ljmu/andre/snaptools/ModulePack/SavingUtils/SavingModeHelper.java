package com.ljmu.andre.snaptools.ModulePack.SavingUtils;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;

import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVING_MODES;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingModeHelper {
    public static SavingMode getSavingModeForType(SnapType snapType) {
        String savingModeName = PreferenceHelpers.getFromMap(SAVING_MODES, snapType.getName());
        if (savingModeName == null)
            return SavingMode.AUTO;

        return SavingMode.fromNameOptional(savingModeName);
    }
}
