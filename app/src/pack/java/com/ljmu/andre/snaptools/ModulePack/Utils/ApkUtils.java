package com.ljmu.andre.snaptools.ModulePack.Utils;

import com.ljmu.andre.snaptools.BuildConfig;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ApkUtils {
    public static int getApkVersionCodeCompiledWith() {
        return BuildConfig.VERSION_CODE;
    }

    public static String getApkVersionNameCompiledWith() {
        return BuildConfig.VERSION_NAME;
    }
}
