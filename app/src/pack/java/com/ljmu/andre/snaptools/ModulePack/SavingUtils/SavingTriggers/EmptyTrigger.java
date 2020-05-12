package com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers;

import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SaveState;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class EmptyTrigger extends SavingTrigger {
    public EmptyTrigger() {
        super();
    }

    @Nullable
    @Override
    public SaveState setReadySnap(Snap readySnap) {
        return SaveState.NOT_READY;
    }

    @Override
    public SaveState triggerSave() {
        return SaveState.NOT_READY;
    }
}
