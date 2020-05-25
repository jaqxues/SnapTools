package com.ljmu.andre.snaptools.Utils

import com.jaqxues.akrolyb.prefs.Preference
import com.jaqxues.akrolyb.prefs.Types.Companion.genericType
import com.ljmu.andre.snaptools.BuildConfig
import com.ljmu.andre.snaptools.UIComponents.UITheme
import java.util.*
import kotlin.collections.HashSet

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
object FrameworkPreferencesDef {
    /**
     * ===========================================================================
     * Booleans
     * ===========================================================================
     */
    @JvmField
    val ACCEPTED_TOS = Preference(
            "ACCEPTED_TOS",
            false, Boolean::class.java
    )
    @JvmField
    val SHOW_TUTORIAL = Preference(
            "SHOW_TUTORIAL",
            true, Boolean::class.java
    )
    @JvmField
    val HAS_SHOWN_REPKG_DIALOG = Preference(
            "HAS_SHOWN_REPKG_DIALOG",
            false, Boolean::class.java
    )
    @JvmField
    val SYSTEM_ENABLED = Preference(
            "SYSTEM_ENABLED",
            true, Boolean::class.java
    )
    @JvmField
    val CHECK_APK_UPDATES = Preference(
            "CHECK_APK_UPDATES",
            true, Boolean::class.java
    )
    @JvmField
    val CHECK_PACK_UPDATES = Preference(
            "CHECK_PACK_UPDATES",
            true, Boolean::class.java
    )
    @JvmField
    val CHECK_PACK_UPDATES_SC = Preference(
            "CHECK_PACK_UPDATES_SC",
            true, Boolean::class.java
    )
    @JvmField
    val KILL_SC_ON_CHANGE = Preference(
            "KILL_SC_ON_CHANGE",
            true, Boolean::class.java
    )
    @JvmField
    val SHOWN_ANDROID_P_WARNING = Preference(
            "SHOWN_ANDROID_P_WARNING",
            false, Boolean::class.java
    )
    @JvmField
    val NOTIFY_ON_LOAD = Preference(
            "NOTIFY_ON_LOAD",
            true, Boolean::class.java
    )
    @JvmField
    val RESIZE_SHARING_IMAGE = Preference(
            "RESIZE_SHARING_IMAGE",
            true, Boolean::class.java
    )
    @JvmField
    val LOCK_SHARING_RATIO = Preference(
            "LOCK_SHARING_RATIO",
            true, Boolean::class.java
    )
    @JvmField
    val BACK_OPENS_MENU = Preference(
            "BACK_OPENS_MENU",
            false, Boolean::class.java
    )
    @JvmField
    val SHOW_TRANSITION_ANIMATIONS = Preference(
            "SHOW_TRANSITION_ANIMATIONS",
            true, Boolean::class.java
    )
    val SHARING_COMPRESS_VIDEOS = Preference(
            "SHARING_COMPRESS_VIDEOS",
            true, Boolean::class.java
    )
    @JvmField
    val SHOW_VIDEO_SHARING_ADVICE = Preference(
            "SHOW_VIDEO_SHARING_ADVICE",
            true, Boolean::class.java
    )
    @JvmField
    val SHOW_VIDEO_COMPRESSION_DIALOG = Preference(
            "SHOW_VIDEO_COMPRESSION_DIALOG",
            true, Boolean::class.java
    )
    @JvmField
    val HAS_SHOWN_PAY_MODEL_REASONING = Preference(
            "HAS_SHOWN_PAY_MODEL_REASONING",
            false, Boolean::class.java
    )

    /**
     * ===========================================================================
     * Sets
     * ===========================================================================
     */
    @JvmField
    val DISABLED_MODULES = Preference<HashSet<String>>(
            "DISABLED_MODULES",
            HashSet(), genericType<HashSet<String>>()
    )
    @JvmField
    val SELECTED_PACKS = Preference<HashSet<String>>(
            "SELECTED_PACKS",
            HashSet(), genericType<HashSet<String>>()
    )
    val SAVING_FILTER = Preference<HashSet<String>>(
            "SAVING_FILTER",
            HashSet(), genericType<HashSet<String>>()
    )

    /**
     * ===========================================================================
     * Strings
     * ===========================================================================
     */
    @JvmField
    val REPACKAGE_NAME = Preference<String?>(
            "REPACKAGE_NAME",
            null, String::class.java
    )

    /**
     * Ability to save an old PackageName after App Repackaging to detect duplicates
     */
    @JvmField
    val PLACE_HOLDER_UNINSTALL_REPKG = Preference<String?>(
            "PLACE_HOLDER_UNINSTALL_REPKG",
            null, String::class.java
    )
    @JvmField
    val CURRENT_THEME = Preference(
            "CURRENT_THEME",
            UITheme.DEFAULT.getName(), String::class.java
    )
    @JvmField
    val IGNORED_PACK_UPDATE_VERSION = Preference(
            "IGNORED_PACK_UPDATE_VERSION",
            "", String::class.java
    )
    val LAST_BUILD_FLAVOUR = Preference(
            "LAST_BUILD_FLAVOUR",
            BuildConfig.FLAVOR, String::class.java
    )
    @JvmField
    val INSTALLATION_ID = Preference<String?>(
            "INSTALLATION_ID",
            null, String::class.java)

    /**
     * ===========================================================================
     * Longs
     * ===========================================================================
     */
    val LAST_UPDATE_USER = Preference(
            "LAST_UPDATE_USER",
            0L, Long::class.java
    )
    @JvmField
    val LAST_APK_UPDATE_CHECK = Preference(
            "LAST_APK_UPDATE_CHECK",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_PACKS = Preference(
            "LAST_CHECK_PACKS",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_SHOP = Preference(
            "LAST_CHECK_SHOP",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_FAQS = Preference(
            "LAST_CHECK_FAQS",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_FEATURES = Preference(
            "LAST_CHECK_FEATURES",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_TRANSLATIONS = Preference(
            "LAST_CHECK_TRANSLATIONS",
            0L, Long::class.java
    )
    @JvmField
    val TRIAL_ACTIVE_TIME = Preference<Long?>(
            "TRIAL_ACTIVE_TIME",
            null, Long::class.java
    )
    @JvmField
    val LAST_OPEN_APP = Preference(
            "LAST_OPEN_APP",
            0L, Long::class.java
    )
    @JvmField
    val LAST_CHECK_REMOTE_CONFIG = Preference(
            "LAST_CHECK_REMOTE_CONFIG",
            0L, Long::class.java
    )

    /**
     * ===========================================================================
     * Integers
     * ===========================================================================
     */
    @JvmField
    @Deprecated("")
    val LENS_SELECTOR_SPAN = Preference(
            "LENS_SELECTOR_SPAN",
            4, Int::class.java
    )
    @JvmField
    val IGNORED_UPDATE_VERSION_CODE = Preference(
            "IGNORED_UPDATE_VERSION_CODE",
            0, Int::class.java
    )
    @JvmField
    val TRIAL_MODE = Preference(
            "TRIAL_MODE",
            TrialUtils.TRIAL_UNKNOWN, Int::class.java
    )
    @JvmField
    val COMPRESSION_QUALITY = Preference(
            "COMPRESSION_QUALITY",
            3000, Int::class.java
    )
    @JvmField
    val LATEST_APK_VERSION_CODE = Preference(
            "LATEST_APK_VERSION_CODE",
            Constants.getApkVersionCode(), Int::class.java
    )
    val LATEST_PACK_VERSION_NAME = Preference(
            "LATEST_PACK_VERSION_NAME",
            0, Int::class.java
    )
    @JvmField
    val WATCHDOG_HANG_TIMEOUT = Preference(
            "WATCHDOG_HANG_TIMEOUT",
            10000, Int::class.java
    )
    @JvmField
    var TRANSLATION_LOCALE = Preference<String?>(
            "TRANSLATION_LOCALE",
            null, String::class.java
    )
}