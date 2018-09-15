package com.ljmu.andre.snaptools.Networking.Helpers;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.Constants.FEATURES_CHECK_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_FEATURES;
import static com.ljmu.andre.snaptools.Utils.MiscUtils.calcTimeDiff;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetFeatures extends CachedFileDownloader {
    private static final String FEATURES_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/General/Features.txt";

    @Override
    protected boolean shouldUseCache() {
        return calcTimeDiff(getPref(LAST_CHECK_FEATURES)) > FEATURES_CHECK_COOLDOWN;
    }

    @Override
    protected String getCachedFilename() {
        return "features.txt";
    }

    @Override
    protected String getURL() {
        return FEATURES_URL;
    }

    @Override
    protected void updateCacheTime() {
        putPref(LAST_CHECK_FEATURES, System.currentTimeMillis());
    }
}
