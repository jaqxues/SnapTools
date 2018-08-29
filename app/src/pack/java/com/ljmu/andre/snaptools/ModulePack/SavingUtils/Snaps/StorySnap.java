package com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SaveTriggerManager;
import timber.log.Timber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StorySnap extends Snap {
    private boolean hasStreamSaved;
    private boolean hasSnapDisplayed;

    StorySnap() {
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

                hasStreamSaved = true;

                if (!hasSnapDisplayed)
                    return SaveState.NOT_READY;


                return SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);
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

            if (!hasStreamSaved)
                return SaveState.NOT_READY;

            return SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("hasStreamSaved", hasStreamSaved)
                .add("hasSnapDisplayed", hasSnapDisplayed)
                .add("super", super.toString())
                .toString();
    }
}
