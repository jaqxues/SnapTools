package com.ljmu.andre.snaptools.Networking.Helpers;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.jaqxues.akrolyb.prefs.PrefManagerKt.putPref;
import static com.ljmu.andre.snaptools.Utils.Constants.REMOTE_CONFIG_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_REMOTE_CONFIG;
import static com.ljmu.andre.snaptools.Utils.MiscUtils.calcTimeDiff;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 23.09.2018 - Time 13:20.
 */

public class GetRemoteConfig extends CachedFileDownloader {
    private static final String REMOTE_CONFIG_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/General/RemoteConfig.json";

    @Override
    protected boolean shouldUseCache() {
        return calcTimeDiff(getPref(LAST_CHECK_REMOTE_CONFIG)) > REMOTE_CONFIG_COOLDOWN;
    }

    @Override
    protected String getCachedFilename() {
        return "config.json";
    }

    @Override
    protected String getURL() {
        return REMOTE_CONFIG_URL;
    }

    @Override
    protected void updateCacheTime() {
        putPref(LAST_CHECK_REMOTE_CONFIG, System.currentTimeMillis());
    }
}
