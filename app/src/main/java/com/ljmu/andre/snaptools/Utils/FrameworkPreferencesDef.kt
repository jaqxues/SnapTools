package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.NonNull;

import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.GsonPreferences.Preferences.ConditionalCheck;
import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.UIComponents.UITheme;

import java.util.HashSet;
import java.util.UUID;

import static com.ljmu.andre.GsonPreferences.Preferences.getExternalPath;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FrameworkPreferencesDef extends ConstantDefiner<Preference> {
    /**
     * ===========================================================================
     * Booleans
     * ===========================================================================
     */
    public static final Preference ACCEPTED_TOS = new Preference(
            "ACCEPTED_TOS",
            false, Boolean.class
    );
    public static final Preference SHOW_TUTORIAL = new Preference(
            "SHOW_TUTORIAL",
            true, Boolean.class
    );
    public static final Preference HAS_SHOWN_REPKG_DIALOG = new Preference(
            "HAS_SHOWN_REPKG_DIALOG",
            false, Boolean.class
    );
    public static final Preference SYSTEM_ENABLED = new Preference(
            "SYSTEM_ENABLED",
            true, Boolean.class
    );
    public static final Preference CHECK_APK_UPDATES = new Preference(
            "CHECK_APK_UPDATES",
            true, Boolean.class
    );
    public static final Preference CHECK_PACK_UPDATES = new Preference(
            "CHECK_PACK_UPDATES",
            true, Boolean.class
    );
    public static final Preference CHECK_PACK_UPDATES_SC = new Preference(
            "CHECK_PACK_UPDATES_SC",
            true, Boolean.class
    );
    public static final Preference KILL_SC_ON_CHANGE = new Preference(
            "KILL_SC_ON_CHANGE",
            true, Boolean.class
    );
    public static final Preference SHOWN_ANDROID_P_WARNING = new Preference(
            "SHOWN_ANDROID_P_WARNING",
            false, Boolean.class
    );
    public static final Preference NOTIFY_ON_LOAD = new Preference(
            "NOTIFY_ON_LOAD",
            true, Boolean.class
    );
    public static final Preference RESIZE_SHARING_IMAGE = new Preference(
            "RESIZE_SHARING_IMAGE",
            true, Boolean.class
    );
    public static final Preference LOCK_SHARING_RATIO = new Preference(
            "LOCK_SHARING_RATIO",
            true, Boolean.class
    );
    public static final Preference BACK_OPENS_MENU = new Preference(
            "BACK_OPENS_MENU",
            false, Boolean.class
    );
    public static final Preference SHOW_TRANSITION_ANIMATIONS = new Preference(
            "SHOW_TRANSITION_ANIMATIONS",
            true, Boolean.class
    );
    public static final Preference SHARING_COMPRESS_VIDEOS = new Preference(
            "SHARING_COMPRESS_VIDEOS",
            true, Boolean.class
    );
    public static final Preference SHOW_VIDEO_SHARING_ADVICE = new Preference(
            "SHOW_VIDEO_SHARING_ADVICE",
            true, Boolean.class
    );
    public static final Preference SHOW_VIDEO_COMPRESSION_DIALOG = new Preference(
            "SHOW_VIDEO_COMPRESSION_DIALOG",
            true, Boolean.class
    );
    public static final Preference HAS_SHOWN_PAY_MODEL_REASONING = new Preference(
            "HAS_SHOWN_PAY_MODEL_REASONING",
            false, Boolean.class
    );

    /**
     * ===========================================================================
     * Sets
     * ===========================================================================
     */
    public static final Preference DISABLED_MODULES = new Preference(
            "DISABLED_MODULES",
            new HashSet<>(), HashSet.class
    );
    public static final Preference SELECTED_PACKS = new Preference(
            "SELECTED_PACKS",
            new HashSet<>(), HashSet.class
    );
    public static final Preference SAVING_FILTER = new Preference(
            "SAVING_FILTER",
            new HashSet<>(), HashSet.class
    );

    /**
     * ===========================================================================
     * Strings
     * ===========================================================================
     */
    public static final Preference REPACKAGE_NAME = new Preference(
            "REPACKAGE_NAME",
            null, String.class
    );
    /**
     * Ability to save an old PackageName after App Repackaging to detect duplicates
     */
    public static final Preference PLACE_HOLDER_UNINSTALL_REPKG = new Preference(
            "PLACE_HOLDER_UNINSTALL_REPKG",
            null, String.class
    );
    public static final Preference CURRENT_THEME = new Preference(
            "CURRENT_THEME",
            UITheme.DEFAULT.getName(), String.class
    );
    public static final Preference IGNORED_PACK_UPDATE_VERSION = new Preference(
            "IGNORED_PACK_UPDATE_VERSION",
            "", String.class
    );
    public static final Preference LAST_BUILD_FLAVOUR = new Preference(
            "LAST_BUILD_FLAVOUR",
            BuildConfig.FLAVOR, String.class
    );
    public static final Preference INSTALLATION_ID = new Preference(
            "INSTALLATION_ID",
            "", String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            String id = UUID.randomUUID().toString();
            putPref(preference, id);
            return id;
        }
    });
    /**
     * ===========================================================================
     * FilePaths
     * ===========================================================================
     */
    public static final Preference CONTENT_PATH = new Preference(
            "CONTENT_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getExternalPath() + "/" + STApplication.MODULE_TAG + "/";
        }
    });
    public static final Preference CONTENT_DATA_PATH = new Preference(
            "CONTENT_DATA_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Data/";
        }
    });
    public static final Preference DATABASES_PATH = new Preference(
            "DATABASES_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Databases/";
        }
    });
    public static final Preference MODULES_PATH = new Preference(
            "MODULES_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "ModulePacks/";
        }
    });
    public static final Preference SHARED_IMAGE_PATH = new Preference(
            "SHARED_IMAGE_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "SharedImages/";
        }
    });
    public static final Preference DUMP_PATH = new Preference(
            "DUMP_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Dumps/";
        }
    });
    public static final Preference TEMP_PATH = new Preference(
            "TEMP_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Temp/";
        }
    });
    public static final Preference LOGS_PATH = new Preference(
            "TEMP_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "ErrorLogs/";
        }
    });
    public static final Preference CRASH_DUMP_PATH = new Preference(
            "TEMP_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "CrashDumps/";
        }
    });
    public static final Preference BACKUP_PATH = new Preference(
            "BACKUP_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Backups/";
        }
    });
    public static final Preference TRANSLATIONS_PATH = new Preference(
            "TRANSLATIONS_PATH",
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Translations/";
        }
    });
    /**
     * ===========================================================================
     * Longs
     * ===========================================================================
     */
    public static final Preference LAST_UPDATE_USER = new Preference(
            "LAST_UPDATE_USER",
            0L, Long.class
    );
    public static final Preference LAST_APK_UPDATE_CHECK = new Preference(
            "LAST_APK_UPDATE_CHECK",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_PACKS = new Preference(
            "LAST_CHECK_PACKS",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_SHOP = new Preference(
            "LAST_CHECK_SHOP",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_FAQS = new Preference(
            "LAST_CHECK_FAQS",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_FEATURES = new Preference(
            "LAST_CHECK_FEATURES",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_TRANSLATIONS = new Preference(
            "LAST_CHECK_TRANSLATIONS",
            0L, Long.class
    );
    public static final Preference TRIAL_ACTIVE_TIME = new Preference(
            "TRIAL_ACTIVE_TIME",
            null, Long.class
    );
    public static final Preference LAST_OPEN_APP = new Preference(
            "LAST_OPEN_APP",
            0L, Long.class
    );
    public static final Preference LAST_CHECK_REMOTE_CONFIG = new Preference(
            "LAST_CHECK_REMOTE_CONFIG",
            0L, Long.class
    );
    /**
     * ===========================================================================
     * Integers
     * ===========================================================================
     */
    @Deprecated
    public static final Preference LENS_SELECTOR_SPAN = new Preference(
            "LENS_SELECTOR_SPAN",
            4, Integer.class
    );
    public static final Preference IGNORED_UPDATE_VERSION_CODE = new Preference(
            "IGNORED_UPDATE_VERSION_CODE",
            0, Integer.class
    );
    public static final Preference TRIAL_MODE = new Preference(
            "TRIAL_MODE",
            TrialUtils.TRIAL_UNKNOWN, Integer.class
    );
    public static final Preference COMPRESSION_QUALITY = new Preference(
            "COMPRESSION_QUALITY",
            3000, Integer.class
    );
    public static final Preference LATEST_APK_VERSION_CODE = new Preference(
            "LATEST_APK_VERSION_CODE",
            Constants.getApkVersionCode(), Integer.class
    );
    public static final Preference LATEST_PACK_VERSION_NAME = new Preference(
            "LATEST_PACK_VERSION_NAME",
            0, Integer.class
    );
    public static final Preference WATCHDOG_HANG_TIMEOUT = new Preference(
            "WATCHDOG_HANG_TIMEOUT",
            10000, Integer.class
    );
    public static Preference TRANSLATION_LOCALE = new Preference(
            "TRANSLATION_LOCALE",
            null, String.class
    );
}
