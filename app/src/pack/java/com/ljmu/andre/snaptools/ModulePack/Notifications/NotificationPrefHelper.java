package com.ljmu.andre.snaptools.ModulePack.Notifications;

import com.ljmu.andre.snaptools.ModulePack.Notifications.DotNotification.DotLocation;
import com.ljmu.andre.snaptools.ModulePack.Notifications.StackingDotNotification.StackingOrientation;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.DOT_LOCATION;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STACKED_ORIENTATION;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class NotificationPrefHelper {
    public static DotLocation getDotLocation() {
        String locationName = getPref(DOT_LOCATION);

        if (locationName == null)
            return DotLocation.BOTTOM_LEFT;

        return DotLocation.getFromDisplayText(locationName);
    }

    public static StackingOrientation getStackOrientation() {
        String orientationName = getPref(STACKED_ORIENTATION);

        if (orientationName == null)
            return StackingOrientation.HORIZONTAL;

        return StackingOrientation.getFromDisplayText(orientationName);
    }
}
