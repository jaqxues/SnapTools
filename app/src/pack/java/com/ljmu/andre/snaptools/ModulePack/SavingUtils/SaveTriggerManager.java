package com.ljmu.andre.snaptools.ModulePack.SavingUtils;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.AutoSave;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.EmptyTrigger;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.ManualSave;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger.SavingMode;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapTypeDef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SaveTriggerManager {
    private static Map<String, SavingTrigger> snapTypeTriggerMap = new ConcurrentHashMap<>();

    public static void init() {
        for (SnapType snapType : SnapTypeDef.INST.values()) {
            String snapTypeName = snapType.getName();
            SavingMode savingMode = SavingModeHelper.getSavingModeForType(snapType);


            String savingType;

            if (savingMode == null)
                savingType = "Auto";
            else
                savingType = savingMode.getSavingType();

            SavingTrigger trigger;

            switch (savingType) {
                case "None":
                    trigger = new EmptyTrigger();
                    break;
                case "Auto":
                    trigger = new AutoSave();
                    break;
                case "Manual":
                    trigger = new ManualSave();
                    break;
                default:
                    throw new RuntimeException("Unknown saving type: " + savingMode);
            }

            snapTypeTriggerMap.put(snapTypeName, trigger);
        }
    }

    public static SavingTrigger getTrigger(SnapType snapType) {
        return snapTypeTriggerMap.get(snapType.getName());
    }
}
