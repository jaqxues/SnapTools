package com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps;

import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SaveTriggerManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ReceivedSnap extends Snap {
    private boolean hasSaved;
    private boolean hasSnapDisplayed;

    ReceivedSnap() {
    }

    @Override
    public SaveState providingAlgorithm() {
        synchronized (PROCESSING_LOCK) {
            return null;
        }
    }

    @Override
    public SaveState copyStream(ByteArrayOutputStream outputStream) {
        synchronized (PROCESSING_LOCK) {
            try {
                SnapDiskCache.getInstance().writeToCache(this, outputStream);

                if (!hasSnapDisplayed)
                    return SaveState.NOT_READY;

                Timber.d("Copying received snap stream");
                SaveState saveState = SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);

                if (saveState == SaveState.SUCCESS)
                    hasSaved = true;
                else if (saveState == SaveState.EXISTING) {
                    if (hasSaved)
                        return SaveState.NOT_READY;

                    hasSaved = true;
                    return SaveState.EXISTING;
                }

                return saveState;
            } catch (IOException e) {
                Timber.e(e);
            }

            return SaveState.FAILED;
        }
    }

    @Override
    public SaveState finalDisplayEvent() {
        synchronized (PROCESSING_LOCK) {
            hasSnapDisplayed = true;

            Timber.d("received snap final display event");
            if (!SnapDiskCache.getInstance().containsKey(getKey()))
                return SaveState.NOT_READY;

            SaveState saveState = SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);

            if (saveState == SaveState.SUCCESS)
                hasSaved = true;
            else if (saveState == SaveState.EXISTING) {
                if (hasSaved)
                    return SaveState.NOT_READY;

                hasSaved = true;
                return SaveState.EXISTING;
            }

            return saveState;
        }
    }
}
