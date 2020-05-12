package com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps;

import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SaveTriggerManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GroupSnap extends Snap {
    GroupSnap() {
    }

    @Nullable
    @Override
    public SaveState providingAlgorithm() {
        synchronized (PROCESSING_LOCK) {
            return null;
        }
    }

    @Nullable
    @Override
    public SaveState copyStream(ByteArrayOutputStream outputStream) {
        synchronized (PROCESSING_LOCK) {

            try {
                SnapDiskCache.getInstance().writeToCache(this, outputStream);
                finished();
                return SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);
            } catch (IOException e) {
                Timber.e(e);
            }

            return SaveState.FAILED;
        }
    }

    @Nullable
    @Override
    public SaveState finalDisplayEvent() {
        synchronized (PROCESSING_LOCK) {
            return null;
        }
    }
}
