package com.ljmu.andre.snaptools.ModulePack.Utils

import android.widget.ImageView
import com.jaqxues.akrolyb.prefs.Preference
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthLocationOverlay.StealthPosition
import com.ljmu.andre.snaptools.ModulePack.Notifications.DotNotification.DotLocation
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification.NotificationType
import com.ljmu.andre.snaptools.ModulePack.Notifications.StackingDotNotification.StackingOrientation
import java.util.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
object ModulePreferenceDef {
    /**
     * ===========================================================================
     * Strings
     * ===========================================================================
     */
    @JvmField
    val CUSTOM_MEDIA_PATH = Preference<String?>(
            "CUSTOM_MEDIA_PATH",
            null, String::class.java
    )
    @JvmField
    val FILTER_BACKGROUND_SAMPLE_PATH = Preference<String?>(
            "FILTER_BACKGROUND_SAMPLE_PATH",
            null, String::class.java
    )
    @JvmField
    val STORAGE_FORMAT = Preference(
            "STORAGE_FORMAT",
            "SnapType->Username->Snaps", String::class.java
    )
    @JvmField
    val RECEIVED_FOLDER_NAME = Preference(
            "RECEIVED_FOLDER_NAME",
            "Received", String::class.java
    )
    @JvmField
    val STORY_FOLDER_NAME = Preference(
            "STORY_FOLDER_NAME",
            "Stories", String::class.java
    )
    @JvmField
    val CHAT_FOLDER_NAME = Preference(
            "CHAT_FOLDER_NAME",
            "ChatMedia", String::class.java
    )
    @JvmField
    val GROUP_FOLDER_NAME = Preference(
            "GROUP_FOLDER_NAME",
            "Groups", String::class.java
    )
    @JvmField
    val SENT_FOLDER_NAME = Preference(
            "SENT_FOLDER_NAME",
            "Sent", String::class.java
    )
    @JvmField
    val SAVE_NOTIFICATION_TYPE = Preference(
            "SAVE_NOTIFICATION_TYPE",
            NotificationType.DOT.displayText, String::class.java
    )
    @JvmField
    val DOT_LOCATION = Preference(
            "DOT_LOCATION",
            DotLocation.BOTTOM_LEFT.displayText, String::class.java
    )
    @JvmField
    val STACKED_ORIENTATION = Preference(
            "STACKED_ORIENTATION",
            StackingOrientation.HORIZONTAL.displayText, String::class.java
    )
    @JvmField
    val FILTER_SCALING_TYPE = Preference(
            "FILTER_SCALING_TYPE",
            ImageView.ScaleType.FIT_CENTER.name, String::class.java
    )
    @JvmField
    val CURRENT_FONT = Preference(
            "CURRENT_FONT",
            "Default", String::class.java
    )
    @JvmField
    val STEALTH_SNAP_BUTTON_LOCATION = Preference(
            "STEALTH_SNAP_BUTTON_LOCATION",
            StealthPosition.TOP.name, String::class.java
    )
    // ===========================================================================
    /**
     * ===========================================================================
     * Experimental Feature States
     * ===========================================================================
     */
    @JvmField
    val FORCE_INSIGHTS_STATE = Preference(
            "FORCE_INSIGHTS_STATE",
            "Default", String::class.java
    ) // developerOptionsImpalaForceShowInsights

    /*true, false*/
    @JvmField
    val FORCE_MULTI_SNAP_STATE = Preference(
            "FORCE_MULTI_SNAP_STATE",
            "Default", String::class.java
    ) // magikarp_overwrite

    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/
    @JvmField
    val FORCE_CHAT_VIDEO_STATE = Preference(
            "FORCE_CHAT_VIDEO_STATE",
            "Default", String::class.java
    ) // chat_video_enabled

    /*true, false*/
    @JvmField
    val FORCE_ANIMATED_CONTENT_STATE = Preference(
            "FORCE_ANIMATED_CONTENT_STATE",
            "Default", String::class.java
    ) // animated_content_overwrite

    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/
    @JvmField
    val FORCE_GIPHY_STATE = Preference(
            "FORCE_GIPHY_STATE",
            "Default", String::class.java
    ) // giphy_in_preview

    /*true, false*/
    @JvmField
    val FORCE_CAPTIONV2_STATE = Preference(
            "FORCE_CAPTIONV2_STATE",
            "Default", String::class.java
    ) // caption_v2_overwrite

    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/
    @JvmField
    val FORCE_CAMERA2_STATE = Preference(
            "FORCE_CAMERA2_STATE",
            "Default", String::class.java
    ) // camera2_overwrite_state

    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/
    @JvmField
    val FORCE_HANDSFREEREC_STATE = Preference(
            "FORCE_HANDSFREEREC_STATE",
            "Default", String::class.java
    ) // developerOptionsHandsFreeRecordingMode

    /*DISABLED, FULLY_ENABLED*/
    @JvmField
    val FORCE_FPS_OVERLAY_STATE = Preference(
            "FORCE_FPS_OVERLAY_STATE",
            "Default", String::class.java
    ) // developerOptionsShouldShowFpsOverlay

    /*true, false*/
    @JvmField
    val FORCE_SKYFILTERS_STATE = Preference(
            "FORCE_SKYFILTERS_STATE",
            "Default", String::class.java
    ) // sky_filters_overwrite

    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/
    @JvmField
    val FORCE_EMOJIBRUSH_STATE = Preference(
            "FORCE_EMOJIBRUSH_STATE",
            "Default", String::class.java
    ) // emoji_brush
    /*true, false*/ //	public static final Preference FORCE_CAMERA2_STATE = new Preference(
    //			"FORCE_CAMERA2_STATE",
    //			"Default", String.class
    //	);
    // ===========================================================================
    /**
     * ===========================================================================
     * Maps
     * ===========================================================================
     */
    @JvmField
    val SAVING_MODES = Preference(
            "SAVING_MODES",
            HashMap<String, String>(), HashMap::class.java
    )
    @JvmField
    val SAVE_BUTTON_LOCATIONS = Preference(
            "SAVE_BUTTON_LOCATIONS",
            HashMap<String, String>(), HashMap::class.java
    )
    @JvmField
    val SAVE_BUTTON_WIDTHS = Preference(
            "SAVE_BUTTON_WIDTHS",
            HashMap<String, Int>(), HashMap::class.java
    )
    @JvmField
    val SAVE_BUTTON_RELATIVE_HEIGHTS = Preference(
            "SAVE_BUTTON_RELATIVE_HEIGHTS",
            HashMap<String, Int>(), HashMap::class.java
    )
    @JvmField
    val SAVE_BUTTON_OPACITIES = Preference(
            "SAVE_BUTTON_OPACITIES",
            HashMap<String, Int>(), HashMap::class.java
    )
    @JvmField
    val FLING_VELOCITY = Preference(
            "FLING_VELOCITY",
            HashMap<String, Int>(), HashMap::class.java
    )
    // ===========================================================================
    /**
     * ===========================================================================
     * Sets
     * ===========================================================================
     */
    @JvmField
    val BLOCKED_STORIES = Preference(
            "BLOCKED_STORIES",
            HashSet<String>(), HashSet::class.java
    )
    // ===========================================================================
    /**
     * ===========================================================================
     * Booleans
     * ===========================================================================
     */
    @JvmField
    val SAVE_SENT_SNAPS = Preference(
            "SAVE_SENT_SNAPS",
            true, Boolean::class.java
    )
    @JvmField
    val LENS_AUTO_ENABLE = Preference(
            "LENS_AUTO_ENABLE",
            false, Boolean::class.java
    )
    @JvmField
    val LENS_MERGE_ENABLE = Preference(
            "LENS_MERGE_ENABLE",
            false, Boolean::class.java
    )
    @JvmField
    val SHOW_LENS_NAMES = Preference(
            "SHOW_LENS_NAMES",
            true, Boolean::class.java
    )
    @JvmField
    val SAVE_CHAT_IN_SC = Preference(
            "SAVE_CHAT_IN_SC",
            false, Boolean::class.java
    )
    @JvmField
    val STORE_CHAT_MESSAGES = Preference(
            "STORE_CHAT_MESSAGES",
            true, Boolean::class.java
    )
    @JvmField
    val VIBRATE_ON_SAVE = Preference(
            "VIBRATE_ON_SAVE",
            true, Boolean::class.java
    )
    @JvmField
    val SHOW_SHARING_TUTORIAL = Preference(
            "SHOW_SHARING_TUTORIAL",
            true, Boolean::class.java
    )
    @JvmField
    val SHARING_AUTO_ROTATE = Preference(
            "SHARING_AUTO_ROTATE",
            false, Boolean::class.java
    )
    val LED_INFO_ALREADY_SENT = Preference(
            "LED_INFO_ALREADY_SENT",
            false, Boolean::class.java
    )
    @JvmField
    val FILTER_SHOW_SAMPLE_BACKGROUND = Preference(
            "FILTER_SHOW_SAMPLE_BACKGROUND",
            false, Boolean::class.java
    )
    val FILTER_NOW_PLAYING_ENABLED = Preference(
            "FILTER_NOW_PLAYING_ENABLED",
            true, Boolean::class.java
    )
    val FILTER_NOW_PLAYING_HIDE_EMPTY_ART = Preference(
            "FILTER_NOW_PLAYING_HIDE_EMPTY_ART",
            false, Boolean::class.java
    )
    @JvmField
    val STORY_BLOCKER_DISCOVER_BLOCKED = Preference(
            "STORY_BLOCKER_DISCOVER_BLOCKED",
            true, Boolean::class.java
    )
    @JvmField
    val STORY_BLOCKER_ADVERTS_BLOCKED = Preference(
            "STORY_BLOCKER_ADVERTS_BLOCKED",
            true, Boolean::class.java
    )
    @JvmField
    val STORY_BLOCKER_SHOW_BUTTON = Preference(
            "STORY_BLOCKER_SHOW_BUTTON",
            true, Boolean::class.java
    )
    @JvmField
    val FORCE_MULTILINE = Preference(
            "FORCE_MULTILINE",
            true, Boolean::class.java
    )
    @JvmField
    val CUT_BUTTON = Preference(
            "CUT_BUTTON",
            true, Boolean::class.java
    )
    @JvmField
    val COPY_BUTTON = Preference(
            "COPY_BUTTON",
            true, Boolean::class.java
    )
    @JvmField
    val PASTE_BUTTON = Preference(
            "PASTE_BUTTON",
            true, Boolean::class.java
    )
    @JvmField
    val UNLIMITED_VIEWING_VIDEOS = Preference(
            "UNLIMITED_VIEWING_VIDEOS",
            true, Boolean::class.java
    )
    @JvmField
    val UNLIMITED_VIEWING_IMAGES = Preference(
            "UNLIMITED_VIEWING_IMAGES",
            true, Boolean::class.java
    )
    @JvmField
    val BLOCK_TYPING_NOTIFICATIONS = Preference(
            "BLOCK_TYPING_NOTIFICATIONS",
            false, Boolean::class.java
    )
    @JvmField
    val SHOW_CHAT_STEALTH_BUTTON = Preference(
            "SHOW_CHAT_STEALTH_BUTTON",
            true, Boolean::class.java
    )
    @JvmField
    val SHOW_SNAP_STEALTH_BUTTON = Preference(
            "SHOW_SNAP_STEALTH_BUTTON",
            true, Boolean::class.java
    )
    val SHOW_CHAT_STEALTH_MESSAGE = Preference(
            "SHOW_CHAT_STEALTH_MESSAGE",
            true, Boolean::class.java
    )
    val SHOW_SNAP_STEALTH_MESSAGE = Preference(
            "SHOW_SNAP_STEALTH_MESSAGE",
            true, Boolean::class.java
    )
    @JvmField
    val DEFAULT_CHAT_STEALTH = Preference(
            "DEFAULT_CHAT_STEALTH",
            false, Boolean::class.java
    )
    @JvmField
    val BLOCK_OUTGOING_TYPING_NOTIFICATION = Preference(
            "BLOCK_OUTGOING_TYPING_NOTIFICATION",
            false, Boolean::class.java
    )
    @JvmField
    val DEFAULT_SNAP_STEALTH = Preference(
            "DEFAULT_SNAP_STEALTH",
            false, Boolean::class.java
    )
    val STEALTH_CHAT_BUTTON_LEFT = Preference(
            "STEALTH_CHAT_BUTTON_LEFT",
            true, Boolean::class.java
    )
    @JvmField
    val STEALTH_MARK_STORY_VIEWED = Preference(
            "STEALTH_MARK_STORY_VIEWED",
            false, Boolean::class.java
    )

    /**
     * ===========================================================================
     * Integers
     * ===========================================================================
     */
    @JvmField
    val BATCHED_MEDIA_CAP = Preference(
            "BATCHED_MEDIA_CAP",
            6, Int::class.java
    )
    val CURRENT_NOW_PLAYING_VIEW = Preference(
            "CURRENT_NOW_PLAYING_VIEW",
            0, Int::class.java
    )
    val NOW_PLAYING_BOTTOM_MARGIN = Preference(
            "NOW_PLAYING_BOTTOM_MARGIN",
            100, Int::class.java
    )
    val NOW_PLAYING_IMAGE_SIZE = Preference(
            "NOW_PLAYING_IMAGE_SIZE",
            200, Int::class.java
    )
    val STEALTH_CHAT_BUTTON_ALPHA = Preference(
            "STEALTH_CHAT_BUTTON_ALPHA",
            100, Int::class.java
    )
    val STEALTH_CHAT_BUTTON_PADDING = Preference(
            "STEALTH_CHAT_BUTTON_PADDING",
            10, Int::class.java
    )
    val STEALTH_SNAP_BUTTON_ALPHA = Preference(
            "STEALTH_SNAP_BUTTON_ALPHA",
            100, Int::class.java
    )
    val STEALTH_SNAP_BUTTON_MARGIN = Preference(
            "STEALTH_SNAP_BUTTON_MARGIN",
            10, Int::class.java
    )
    val STEALTH_SNAP_BUTTON_SIZE = Preference(
            "STEALTH_SNAP_BUTTON_SIZE",
            50, Int::class.java
    )

    /**
     * ===========================================================================
     * Longs
     * ===========================================================================
     */
    @JvmField
    val LAST_CHECK_KNOWN_BUGS = Preference(
            "LAST_CHECK_KNOWN_BUGS",
            0L, Long::class.java
    )
}