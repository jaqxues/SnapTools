package com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SaveState;
import com.ljmu.andre.snaptools.ModulePack.Utils.SavingViewPool;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class SavingTrigger {
    private static final Object LOCK = new Object();
    private Snap readySnap;

    SavingTrigger() {
    }

    @Nullable
    public SaveState setReadySnap(Snap readySnap) {
        synchronized (LOCK) {
            this.readySnap = readySnap;
        }

        SavingViewPool.readyUpLayouts(readySnap, this);
        return SaveState.NOT_READY;
    }

    public Snap getReadySnap() {
        return readySnap;
    }

    @Nullable
    public SaveState triggerSave() {
        if (readySnap == null) {
            Timber.w("Tried to save null ready snap");
            return null;
        }

        SnapDiskCache.TransferState transferState = SnapDiskCache.getInstance().exportSnapMedia(
                readySnap
        );

        switch (transferState) {
            case SUCCESS:
                return SaveState.SUCCESS;
            case FAILED:
                return SaveState.FAILED;
            case EXISTENT:
                return SaveState.EXISTING;
            default:
                Timber.e("Unhandled transfer state: " + transferState + "... Assuming failed.");
        }

        return SaveState.NOT_READY;
    }

	/*public static SavingTrigger getInstance() {
		if (instance == null) {
			String savingModeName = getPref(SAVING_MODE);
			SavingMode savingMode = SavingMode.fromName(savingModeName);
			String savingType;

			if (savingMode == null)
				savingType = "Auto";
			else
				savingType = savingMode.getSavingType();

			switch (savingType) {
				case "Auto":
					instance = new AutoSave();
					break;
				case "Manual":
					instance = new ManualSave();
					break;
				default:
					throw new RuntimeException("Unknown saving type: " + savingMode);
			}
		}

		return instance;
	}*/

    public enum SavingMode {
        NONE("None", "None"),
        AUTO("Auto", "Auto"),
        BUTTON("Button", "Manual"),
        FLING_TO_SAVE("Fling To Save", "Manual");

        private String displayName;
        private String savingType;

        SavingMode(String displayName, String savingType) {
            this.displayName = displayName;
            this.savingType = savingType;
        }

        public String getSavingType() {
            return savingType;
        }

        @Nullable
        public static SavingMode fromName(String displayName) {
            for (SavingMode mode : values()) {
                if (mode.getDisplayName().equals(displayName))
                    return mode;
            }

            return null;
        }

        @NonNull
        public static SavingMode fromNameOptional(String displayName) {
            for (SavingMode mode : values()) {
                if (mode.getDisplayName().equals(displayName))
                    return mode;
            }

            return AUTO;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}