package com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SaveState;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AutoSave extends SavingTrigger {
    public AutoSave() {

    }

    @Override
    public SaveState setReadySnap(Snap readySnap) {
        super.setReadySnap(readySnap);

        return triggerSave();
    }
}