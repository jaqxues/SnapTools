package com.ljmu.andre.snaptools.Utils;

import com.jaqxues.akrolyb.logger.FileLogger;
import com.jaqxues.akrolyb.prefs.PrefManager;
import com.jaqxues.akrolyb.prefs.gson.GsonPrefMapSerializer;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import timber.log.Timber;

import java.io.File;

import static com.ljmu.andre.snaptools.Utils.FileUtils.getCreateDir;

/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 25.05.20 - Time 16:25.
 */
public class Common {
    public static final String PREF_FILE = "SnapTools_Preferences.json";

    public static void initPrefs() {
        PrefManager.INSTANCE.init(
                new File(PathProvider.getContentPath(), PREF_FILE),
                new KClass[]{JvmClassMappingKt.getKotlinClass(FrameworkPreferencesDef.class)},
                new GsonPrefMapSerializer()
        );
    }

    public static FileLogger plantFileLogger() {
        try {
            FileLogger fileLogger = FileLogger.Companion.getInstance(getCreateDir(PathProvider.getLogsPath()), true, true);
            Timber.plant(fileLogger);
            return fileLogger;
        } catch (Exception e) {
            Timber.e(e, "Failed to initialize Error Logger");
        }
        return null;
    }
}
