package com.ljmu.andre.snaptools.Networking.Helpers;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.Constants.FAQ_CHECK_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_FAQS;
import static com.ljmu.andre.snaptools.Utils.MiscUtils.calcTimeDiff;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetFAQs extends CachedFileDownloader {
    private static final String FAQS_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/General/FAQs.txt";

    @Override
    public boolean shouldUseCache() {
        return calcTimeDiff(getPref(LAST_CHECK_FAQS)) > FAQ_CHECK_COOLDOWN;
    }

    @Override
    protected String getCachedFilename() {
        return "faqs.txt";
    }

    @Override
    protected String getURL() {
        return FAQS_URL;
    }

    @Override
    protected void updateCacheTime() {
        putPref(LAST_CHECK_FAQS, System.currentTimeMillis());
    }
}
