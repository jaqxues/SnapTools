package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.support.annotation.NonNull;
import android.widget.ImageView.ScaleType;

import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.GsonPreferences.Preferences.ConditionalCheck;
import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthLocationOverlay.StealthPosition;
import com.ljmu.andre.snaptools.ModulePack.Notifications.DotNotification.DotLocation;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification.NotificationType;
import com.ljmu.andre.snaptools.ModulePack.Notifications.StackingDotNotification.StackingOrientation;

import java.util.HashMap;
import java.util.HashSet;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CONTENT_PATH;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModulePreferenceDef extends ConstantDefiner<Preference> {
    /**
     * ===========================================================================
     * Strings
     * ===========================================================================
     */
    public static final Preference FILTER_BACKGROUND_SAMPLE_PATH = new Preference(
            /*FILTER_BACKGROUND_SAMPLE_PATH*/ decryptMsg(new byte[]{32, 68, -125, -2, 122, -54, 39, 7, -121, -25, 41, 56, -57, -48, -58, -79, 68, 91, 90, 47, 107, -11, 7, -100, 1, 115, 12, 110, 101, -35, 85, 46}),
            null, String.class
    );
    public static final Preference STORAGE_FORMAT = new Preference(
            /*STORAGE_FORMAT*/ decryptMsg(new byte[]{29, 0, 70, 93, -37, -3, -121, -12, -119, -68, -44, -60, -36, -124, -75, 40}),
            "SnapType->Username->Snaps", String.class
    );

    public static final Preference MEDIA_PATH = new Preference(
            /*MEDIA_PATH*/ decryptMsg(new byte[]{83, 35, -71, -76, -122, -78, 24, 7, 19, -87, -59, -27, -99, -80, -56, -127}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Media/";
        }
    });

    public static final Preference FILTERS_PATH = new Preference(
            /*FILTERS_PATH*/ decryptMsg(new byte[]{-24, 36, -121, 44, -101, 38, 81, -52, 27, -23, -59, -88, 42, -72, 45, -24}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Filters/";
        }
    });

    public static final Preference FONTS_PATH = new Preference(
            /*FONT_PATH*/ decryptMsg(new byte[]{-104, -80, 108, -87, -44, -99, 56, -44, 4, -74, -31, 86, 48, -76, 41, 103}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Fonts/";
        }
    });

    public static final Preference CHAT_EXPORT_PATH = new Preference(
            /*CHAT_EXPORT_PATH*/ decryptMsg(new byte[]{21, -10, -35, -54, -112, -119, 102, -117, -115, -54, 1, -45, -28, -78, -127, -26, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "ExportedChats/";
        }
    });

    public static final Preference BACKUPS_PATH = new Preference(
            /*BACKUPS_PATH*/ decryptMsg(new byte[]{-104, 1, -66, 103, -58, 64, -74, -61, -61, 2, 97, 31, 40, 3, 101, -126}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Backups/";
        }
    });

    public static final Preference ACCOUNTS_PATH = new Preference(
            /*ACCOUNTS_PATH*/ decryptMsg(new byte[]{-58, -47, -81, 106, -60, -68, -101, -128, -30, 54, -93, 66, 82, -24, -23, 118}),
            null, String.class, new ConditionalCheck() {
        @NonNull
        @Override
        protected Object performConditionCheck(Preference preference, Object preferenceVal) {
            return getPref(CONTENT_PATH) + "Accounts/";
        }
    });

    public static final Preference RECEIVED_FOLDER_NAME = new Preference(
            /*RECEIVED_FOLDER_NAME*/ decryptMsg(new byte[]{-26, 49, -92, -22, 93, -93, 115, -107, -17, -98, 71, 21, 28, 110, 17, 57, -107, -92, -127, 125, -82, -53, -70, -93, -25, -110, -98, -45, -8, 114, -112, -35}),
            "Received", String.class
    );

    public static final Preference STORY_FOLDER_NAME = new Preference(
            /*STORY_FOLDER_NAME*/ decryptMsg(new byte[]{82, -104, -28, 109, 125, 23, -30, 126, -11, 66, 19, 60, -97, 48, -119, 63, -37, -50, -10, 27, -98, -89, -128, 49, 58, -45, 112, -48, -76, 15, 50, -13}),
            "Stories", String.class
    );

    public static final Preference CHAT_FOLDER_NAME = new Preference(
            /*CHAT_FOLDER_NAME*/ decryptMsg(new byte[]{26, -25, 9, -116, -122, 86, -6, -22, 45, -68, -41, -12, 68, 2, 111, -50, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            "ChatMedia", String.class
    );

    public static final Preference GROUP_FOLDER_NAME = new Preference(
            /*GROUP_FOLDER_NAME*/ decryptMsg(new byte[]{-106, -82, -71, 115, 5, -60, 15, 15, 16, 122, 49, -81, 67, 33, 79, 53, -37, -50, -10, 27, -98, -89, -128, 49, 58, -45, 112, -48, -76, 15, 50, -13}),
            "Groups", String.class
    );

    public static final Preference SENT_FOLDER_NAME = new Preference(
            /*SENT_FOLDER_NAME*/ decryptMsg(new byte[]{-7, 125, -52, -27, 13, 103, 8, -84, -117, -101, 69, 90, 18, -46, 83, -51, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            "Sent", String.class
    );

    public static final Preference SAVE_NOTIFICATION_TYPE = new Preference(
            /*SAVE_NOTIFICATION_TYPE*/ decryptMsg(new byte[]{-3, 117, -28, 106, 96, -96, 57, 52, -5, -89, 80, -16, 18, -116, 118, 53, 14, -42, 96, 13, -22, 21, 62, 58, -58, -68, -94, -25, -35, 76, 48, 97}),
            NotificationType.DOT.getDisplayText(), String.class
    );

    public static final Preference DOT_LOCATION = new Preference(
            /*DOT_LOCATION*/ decryptMsg(new byte[]{29, -68, 103, -55, 127, 91, 62, -68, -25, -47, 73, -98, 92, 70, 39, 107}),
            DotLocation.BOTTOM_LEFT.getDisplayText(), String.class
    );

    public static final Preference STACKED_ORIENTATION = new Preference(
            /*STACKED_ORIENTATION*/ decryptMsg(new byte[]{82, 29, 105, 18, 2, -71, 124, 103, 45, -63, -22, 21, 45, 19, 100, -108, 90, 124, 9, -127, 53, 120, -19, -36, 78, 29, -75, -73, -3, 19, 51, -113}),
            StackingOrientation.HORIZONTAL.getDisplayText(), String.class
    );

    public static final Preference FILTER_SCALING_TYPE = new Preference(
            /*FILTER_SCALING_TYPE*/ decryptMsg(new byte[]{-45, 4, 108, 122, -38, 81, 48, 13, -77, 96, 58, -78, 12, -116, -76, -120, 58, -102, -97, -48, -23, -89, 38, -4, 21, 106, 75, -112, -97, -55, 106, 72}),
            ScaleType.FIT_CENTER.name(), String.class
    );

    public static final Preference CURRENT_FONT = new Preference(
            /*CURRENT_FONT*/ decryptMsg(new byte[]{-100, -58, -106, -116, 18, 98, 122, 89, 91, -87, -33, 35, -26, -14, 6, -105}),
            "Default", String.class
    );

    public static final Preference SC_USERNAME = new Preference(
            /*SC_USERNAME*/ decryptMsg(new byte[]{-100, 121, -100, -55, -120, 74, -1, 87, 7, -23, 106, 67, 84, -55, 64, -123}),
            "", String.class
    );

    public static final Preference STEALTH_SNAP_BUTTON_LOCATION = new Preference(
            /*STEALTH_SNAP_BUTTON_LOCATION*/ decryptMsg(new byte[]{41, -115, 120, -80, -122, 79, 65, 110, 102, -49, 101, -93, -21, -79, -67, -74, -51, -67, 89, 60, -91, -28, 53, -113, -109, -44, 57, -125, -38, -44, -13, 104}),
            StealthPosition.TOP.name(), String.class
    );

    // ===========================================================================

    /**
     * ===========================================================================
     * Experimental Feature States
     * ===========================================================================
     */
    public static final Preference FORCE_CHEETAH_STATE = new Preference(
            "FORCE_CHEETAH_STATE",
            "Default", String.class
    );// developerOptionCheetahMode

    public static final Preference FORCE_CHEETAH_CHAT_STATE = new Preference(
            "FORCE_CHEETAH_CHAT_STATE",
            "Default", String.class
    ); // chat_v10
    /*true, false*/

    public static final Preference FORCE_INSIGHTS_STATE = new Preference(
            "FORCE_INSIGHTS_STATE",
            "Default", String.class
    ); // developerOptionsImpalaForceShowInsights
    /*true, false*/

    public static final Preference FORCE_MULTI_SNAP_STATE = new Preference(
            "FORCE_MULTI_SNAP_STATE",
            "Default", String.class
    ); // magikarp_overwrite
    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/

    public static final Preference FORCE_CHAT_VIDEO_STATE = new Preference(
            "FORCE_CHAT_VIDEO_STATE",
            "Default", String.class
    );// chat_video_enabled
    /*true, false*/

    public static final Preference FORCE_ANIMATED_CONTENT_STATE = new Preference(
            "FORCE_ANIMATED_CONTENT_STATE",
            "Default", String.class
    );// animated_content_overwrite
    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/

    public static final Preference FORCE_GIPHY_STATE = new Preference(
            "FORCE_GIPHY_STATE",
            "Default", String.class
    );// giphy_in_preview
    /*true, false*/

    public static final Preference FORCE_CAPTIONV2_STATE = new Preference(
            "FORCE_CAPTIONV2_STATE",
            "Default", String.class
    );// caption_v2_overwrite
    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/

    public static final Preference FORCE_CAMERA2_STATE = new Preference(
            "FORCE_CAMERA2_STATE",
            "Default", String.class
    );// camera2_overwrite_state
    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/

    public static final Preference FORCE_HANDSFREEREC_STATE = new Preference(
            "FORCE_HANDSFREEREC_STATE",
            "Default", String.class
    );// developerOptionsHandsFreeRecordingMode
    /*DISABLED, FULLY_ENABLED*/

    public static final Preference FORCE_FPS_OVERLAY_STATE = new Preference(
            "FORCE_FPS_OVERLAY_STATE",
            "Default", String.class
    );// developerOptionsShouldShowFpsOverlay
    /*true, false*/

    public static final Preference FORCE_SKYFILTERS_STATE = new Preference(
            "FORCE_SKYFILTERS_STATE",
            "Default", String.class
    );// sky_filters_overwrite
    /*OVERWRITE_OFF, FORCE_ENABLED, FORCE_DISABLED;*/

    public static final Preference FORCE_EMOJIBRUSH_STATE = new Preference(
            "FORCE_EMOJIBRUSH_STATE",
            "Default", String.class
    );// emoji_brush
    /*true, false*/

//	public static final Preference FORCE_CAMERA2_STATE = new Preference(
//			"FORCE_CAMERA2_STATE",
//			"Default", String.class
//	);

    // ===========================================================================

    /**
     * ===========================================================================
     * Maps
     * ===========================================================================
     */
    public static final Preference SAVING_MODES = new Preference(
            /*SAVING_MODES*/ decryptMsg(new byte[]{30, -30, -79, -69, 70, 105, 55, 60, 52, -79, 4, 65, -108, -109, 102, -86}),
            new HashMap<String, String>(), HashMap.class
    );

    public static final Preference SAVE_BUTTON_LOCATIONS = new Preference(
            /*SAVE_BUTTON_LOCATIONS*/ decryptMsg(new byte[]{8, 34, -123, 13, -77, -22, -33, 67, -9, 107, -42, -105, 95, 3, 98, -103, -78, -107, 10, -81, 22, 87, 105, -20, -75, -64, -58, -73, 66, -23, -57, -95}),
            new HashMap<String, String>(), HashMap.class
    );

    public static final Preference SAVE_BUTTON_WIDTHS = new Preference(
            /*SAVE_BUTTON_WIDTHS*/ decryptMsg(new byte[]{-62, 54, -32, 49, -41, -86, 65, -46, -127, 92, -108, -5, -96, -53, 114, -44, -37, -58, -67, 100, 24, -46, -59, 74, 66, 114, -81, -37, -41, -29, -77, -3}),
            new HashMap<String, Integer>(), HashMap.class
    );

    public static final Preference SAVE_BUTTON_RELATIVE_HEIGHTS = new Preference(
            /*SAVE_BUTTON_RELATIVE_HEIGHTS*/ decryptMsg(new byte[]{66, -95, -36, 97, -51, 74, -7, 12, -99, -1, 52, 61, -17, -84, -69, 94, -27, -7, 93, 95, 121, -122, -19, 88, 97, -110, -121, 74, 102, 34, 51, -59}),
            new HashMap<String, Integer>(), HashMap.class
    );

    public static final Preference SAVE_BUTTON_OPACITIES = new Preference(
            /*SAVE_BUTTON_OPACITIES*/ decryptMsg(new byte[]{-43, -37, 52, 98, 47, 13, -99, 121, -116, -50, 127, 48, 25, -33, 89, 31, -29, 35, 99, 5, -52, 96, 116, -65, -59, 112, 50, -95, -115, -81, 121, 80}),
            new HashMap<String, Integer>(), HashMap.class
    );

    public static final Preference FLING_VELOCITY = new Preference(
            /*FLING_VELOCITY*/ decryptMsg(new byte[]{-29, 40, 10, 101, -9, -110, 47, -2, 16, 47, 68, 31, 8, -123, -82, 120}),
            new HashMap<String, Integer>(), HashMap.class
    );

    // ===========================================================================

    /**
     * ===========================================================================
     * Sets
     * ===========================================================================
     */
    public static final Preference BLOCKED_STORIES = new Preference(
            /*BLOCKED_STORIES*/ decryptMsg(new byte[]{21, -45, -16, -40, -52, -69, -46, -56, -34, -112, 75, 40, -76, 80, -45, -108}),
            new HashSet<String>(), HashSet.class
    );

    // ===========================================================================

    /**
     * ===========================================================================
     * Booleans
     * ===========================================================================
     */
    public static final Preference SAVE_SENT_SNAPS = new Preference(
            /*SAVE_SENT_SNAPS*/ decryptMsg(new byte[]{29, -120, -100, -67, 22, 112, 107, -57, -17, -18, 50, 28, 83, 57, 2, 35}),
            true, Boolean.class
    );

    public static final Preference LENS_AUTO_ENABLE = new Preference(
            /*LENS_AUTO_ENABLE*/ decryptMsg(new byte[]{-38, -56, 92, -2, -5, -116, 127, 40, 20, 25, 86, -70, 100, 23, -102, 73, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            false, Boolean.class
    );

    public static final Preference LENS_MERGE_ENABLE = new Preference(
            /*LENS_MERGE_ENABLE*/ decryptMsg(new byte[]{-98, -3, 1, -3, 91, -35, -31, -64, 16, 45, -2, -100, 36, -112, 74, 79, -37, -50, -10, 27, -98, -89, -128, 49, 58, -45, 112, -48, -76, 15, 50, -13}),
            false, Boolean.class
    );

    public static final Preference SHOW_LENS_NAMES = new Preference(
            /*SHOW_LENS_NAMES*/ decryptMsg(new byte[]{-26, -60, -104, 98, -37, -84, 89, -122, -36, -98, -115, -27, 7, 98, -10, -3}),
            true, Boolean.class
    );

    public static final Preference SAVE_CHAT_IN_SC = new Preference(
            /*SAVE_CHAT_IN_SC*/ decryptMsg(new byte[]{-22, -78, 10, -122, 20, 7, -82, -58, -102, -96, 63, 43, 92, -79, 48, -6}),
            false, Boolean.class
    );

    public static final Preference STORE_CHAT_MESSAGES = new Preference(
            /*STORE_CHAT_MESSAGES*/ decryptMsg(new byte[]{-44, 68, -108, -16, -61, 78, -39, -111, -30, 62, 125, 28, -10, -34, 1, 75, 6, -53, -18, 78, 108, 0, 81, -90, 83, -28, -48, 107, 111, -88, 2, 95}),
            true, Boolean.class
    );

    public static final Preference VIBRATE_ON_SAVE = new Preference(
            /*VIBRATE_ON_SAVE*/ decryptMsg(new byte[]{-43, 125, -56, 46, 88, -124, 90, -71, 122, 59, -42, 2, -49, 74, 91, -101}),
            true, Boolean.class
    );

    public static final Preference SHOW_SHARING_TUTORIAL = new Preference(
            /*SHOW_SHARING_TUTORIAL*/ decryptMsg(new byte[]{-6, -117, 83, -40, 51, 19, 47, 57, 65, 113, -29, 29, 90, 61, -104, -68, -18, 4, -53, -4, -31, -11, 82, -60, -107, -52, -35, 113, 88, -119, -91, 85}),
            true, Boolean.class
    );

    public static final Preference LED_INFO_ALREADY_SENT = new Preference(
            /*LED_INFO_ALREADY_SENT*/ decryptMsg(new byte[]{-37, -52, 0, 73, 46, -44, -125, -66, -119, -106, -54, 17, -50, 73, -63, 82, 102, -81, -10, 22, 51, 55, 25, -16, 105, 7, 82, 31, 50, -32, 31, 59}),
            false, Boolean.class
    );

    public static final Preference FILTER_SHOW_SAMPLE_BACKGROUND = new Preference(
            /*FILTER_SHOW_SAMPLE_BACKGROUND*/ decryptMsg(new byte[]{80, -1, -42, -1, 2, 31, 114, -122, 65, -108, -81, 30, 110, 95, 72, -82, 70, 94, -32, 60, -24, 112, -79, 69, 48, 5, 124, 97, -58, -58, 42, -21}),
            false, Boolean.class
    );

    public static final Preference FILTER_NOW_PLAYING_ENABLED = new Preference(
            /*FILTER_NOW_PLAYING_ENABLED*/ decryptMsg(new byte[]{0, 14, -113, 54, 40, 80, -56, 76, 3, -38, 115, 107, -102, 17, 63, 2, 25, 88, -82, 68, 78, -72, 16, -2, -108, -39, -50, -55, 61, -122, 127, -105}),
            true, Boolean.class
    );

    public static final Preference FILTER_NOW_PLAYING_HIDE_EMPTY_ART = new Preference(
            /*FILTER_NOW_PLAYING_HIDE_EMPTY_ART*/ decryptMsg(new byte[]{0, 14, -113, 54, 40, 80, -56, 76, 3, -38, 115, 107, -102, 17, 63, 2, -114, -24, -4, 124, -96, -83, -62, 61, -57, 64, 35, 44, -58, -32, -15, -79, 84, 71, -13, -96, -32, -100, -71, 88, -32, -92, -27, 51, -107, -84, 41, 96}),
            false, Boolean.class
    );

    public static final Preference STORY_BLOCKER_DISCOVER_BLOCKED = new Preference(
            /*STORY_BLOCKER_DISCOVER_BLOCKED*/ decryptMsg(new byte[]{-83, 10, 121, 125, 37, -1, -95, -103, 82, -27, 113, -68, 91, -11, -13, 54, 88, -106, -10, 7, -24, -64, -89, -113, 106, 106, 12, 86, 57, -68, 82, 119}),
            true, Boolean.class
    );

    public static final Preference STORY_BLOCKER_ADVERTS_BLOCKED = new Preference(
            /*STORY_BLOCKER_ADVERTS_BLOCKED*/ decryptMsg(new byte[]{11, -85, 41, 77, 47, 120, 17, -106, 15, -64, -26, 65, -128, 80, 88, -28, -57, 98, 41, -58, -34, 51, -7, -23, 19, 88, 15, -67, -119, 52, 85, -12}),
            true, Boolean.class
    );

    public static final Preference STORY_BLOCKER_SHOW_BUTTON = new Preference(
            /*STORY_BLOCKER_SHOW_BUTTON*/ decryptMsg(new byte[]{-16, -91, -127, -72, -56, 21, 11, 72, 119, -75, -42, -127, 9, -8, -75, 98, -23, -88, -9, -88, -30, 105, 125, 22, -24, -124, -9, 6, 88, -86, -17, 95}),
            true, Boolean.class
    );

    public static final Preference FORCE_MULTILINE = new Preference(
            /*FORCE_MULTILINE*/ decryptMsg(new byte[]{-27, -116, -9, -28, -44, 92, -65, 117, 103, -15, -65, 64, 40, -71, -109, 24}),
            true, Boolean.class
    );

    public static final Preference CUT_BUTTON = new Preference(
            /*CUT_BUTTON*/ decryptMsg(new byte[]{44, -3, 54, 46, 126, 5, 110, -101, -110, -87, 26, 24, 116, -84, -29, -19}),
            true, Boolean.class
    );

    public static final Preference COPY_BUTTON = new Preference(
            /*COPY_BUTTON*/ decryptMsg(new byte[]{39, 79, 72, 99, -21, -106, -57, -16, -92, 25, 62, -88, 23, 98, 110, 63}),
            true, Boolean.class
    );

    public static final Preference PASTE_BUTTON = new Preference(
            /*PASTE_BUTTON*/ decryptMsg(new byte[]{-109, -109, -25, 102, 123, -22, 29, 18, -118, -13, -73, -10, -70, -90, 73, -39}),
            true, Boolean.class
    );

    public static final Preference UNLIMITED_VIEWING_VIDEOS = new Preference(
            /*UNLIMITED_VIEWING_VIDEOS*/ decryptMsg(new byte[]{114, -45, 31, 48, 62, -32, 11, 99, -123, -15, -81, -61, 48, 26, 0, -78, 77, 91, 119, 81, 121, 114, -108, 102, 38, 7, -74, 74, 61, 54, 112, -72}),
            true, Boolean.class
    );

    public static final Preference UNLIMITED_VIEWING_IMAGES = new Preference(
            /*UNLIMITED_VIEWING_IMAGES*/ decryptMsg(new byte[]{114, -45, 31, 48, 62, -32, 11, 99, -123, -15, -81, -61, 48, 26, 0, -78, -36, 18, -106, -94, 69, -109, -98, -57, -79, -127, -105, 124, 33, -128, -18, -89}),
            true, Boolean.class
    );

    public static final Preference FORCE_CHEETAH = new Preference(
            /*FORCE_CHEETAH*/ decryptMsg(new byte[]{-9, 115, -49, 39, -91, 127, -75, 95, -66, 68, -101, -23, -48, 25, 99, -62}),
            false, Boolean.class
    );

    public static final Preference BLOCK_TYPING_NOTIFICATIONS = new Preference(
            /*BLOCK_TYPING_NOTIFICATIONS*/ decryptMsg(new byte[]{-40, 100, 57, -67, 124, -78, -126, -114, 22, -3, -53, -66, -59, 108, -75, -38, -10, 1, 2, 53, -91, -70, 29, -123, -82, -94, -122, -9, -126, -49, -123, 18}),
            false, Boolean.class
    );

    public static final Preference SHOW_CHAT_STEALTH_BUTTON = new Preference(
            /*SHOW_CHAT_STEALTH_BUTTON*/ decryptMsg(new byte[]{-103, -20, 99, -127, 64, 84, -64, 104, 5, 57, -2, 4, 98, -49, -67, 19, 109, -74, 15, 34, -124, 77, 42, -124, -18, -15, -87, 87, -104, 19, 95, 75}),
            true, Boolean.class
    );

    public static final Preference SHOW_SNAP_STEALTH_BUTTON = new Preference(
            /*SHOW_SNAP_STEALTH_BUTTON*/ decryptMsg(new byte[]{12, 25, 35, -60, 118, -77, -102, 124, 116, -17, 39, 72, -32, -37, -84, 57, 109, -74, 15, 34, -124, 77, 42, -124, -18, -15, -87, 87, -104, 19, 95, 75}),
            true, Boolean.class
    );

    public static final Preference SHOW_CHAT_STEALTH_MESSAGE = new Preference(
            /*SHOW_CHAT_STEALTH_MESSAGE*/ decryptMsg(new byte[]{-103, -20, 99, -127, 64, 84, -64, 104, 5, 57, -2, 4, 98, -49, -67, 19, 79, -57, -1, -53, 51, 40, 94, 50, -78, 55, -33, -3, -90, 9, 85, -94}),
            true, Boolean.class
    );

    public static final Preference SHOW_SNAP_STEALTH_MESSAGE = new Preference(
            /*SHOW_SNAP_STEALTH_MESSAGE*/ decryptMsg(new byte[]{12, 25, 35, -60, 118, -77, -102, 124, 116, -17, 39, 72, -32, -37, -84, 57, 79, -57, -1, -53, 51, 40, 94, 50, -78, 55, -33, -3, -90, 9, 85, -94}),
            true, Boolean.class
    );

    public static final Preference DEFAULT_CHAT_STEALTH = new Preference(
            /*DEFAULT_CHAT_STEALTH*/ decryptMsg(new byte[]{-113, 78, 14, 78, -115, 106, 33, 52, 92, 51, 93, 36, -43, -10, -87, -108, 26, -57, -39, 108, 54, 31, -2, 60, 101, -98, 42, -23, -79, -124, -33, 29}),
            false, Boolean.class
    );

    public static final Preference DEFAULT_SNAP_STEALTH = new Preference(
            /*DEFAULT_SNAP_STEALTH*/ decryptMsg(new byte[]{-8, -20, -20, -94, -127, -79, 127, -90, 28, -118, 126, 33, -58, 124, -83, 119, 26, -57, -39, 108, 54, 31, -2, 60, 101, -98, 42, -23, -79, -124, -33, 29}),
            false, Boolean.class
    );

    public static final Preference STEALTH_CHAT_BUTTON_LEFT = new Preference(
            /*STEALTH_CHAT_BUTTON_LEFT*/ decryptMsg(new byte[]{-107, -115, -21, 105, -35, -87, 60, 126, -85, 67, -6, 77, -21, -35, -93, -25, 108, -50, -100, 36, -72, -54, 23, 65, 123, 46, 111, 55, -31, -104, -111, -108}),
            true, Boolean.class
    );

    public static final Preference STEALTH_MARK_STORY_VIEWED = new Preference(
            /*STEALTH_MARK_STORY_VIEWED*/ decryptMsg(new byte[]{74, -128, 83, 105, 59, -4, -82, -18, -120, -116, -99, 46, 51, -64, 73, 77, 13, 42, 19, 76, 98, -3, -84, 50, 48, -109, -6, -122, -54, 57, 40, -43}),
            false, Boolean.class
    );

    /**
     * ===========================================================================
     * Integers
     * ===========================================================================
     */
    public static final Preference BATCHED_MEDIA_CAP = new Preference(
            /*BATCHED_MEDIA_CAP*/ decryptMsg(new byte[]{83, -71, 54, -79, -2, -123, -37, -1, 57, 2, -91, 21, -79, -82, 52, 15, 42, -92, 127, 10, 40, 65, -70, -67, -40, -37, 84, -3, 18, 89, -30, -123}),
            6, Integer.class
    );
    public static final Preference CURRENT_NOW_PLAYING_VIEW = new Preference(
            /*CURRENT_NOW_PLAYING_VIEW*/ decryptMsg(new byte[]{-35, 40, 83, -80, 4, -60, -46, 118, 84, 17, -39, 14, 31, 15, -40, 63, -126, -91, -106, -37, -42, 83, -113, 103, -69, -37, -80, -8, -38, 8, -104, 35}),
            0, Integer.class
    );
    public static final Preference NOW_PLAYING_BOTTOM_MARGIN = new Preference(
            /*NOW_PLAYING_BOTTOM_MARGIN*/ decryptMsg(new byte[]{-51, 90, -61, -55, -63, 13, 72, -105, 36, -112, 26, 116, 52, 62, 120, -25, -84, 68, 79, -70, 96, -54, -26, 102, 74, 101, -89, -114, 96, -79, -22, 117}),
            100, Integer.class
    );
    public static final Preference NOW_PLAYING_IMAGE_SIZE = new Preference(
            /*NOW_PLAYING_IMAGE_SIZE*/ decryptMsg(new byte[]{82, 108, 78, 40, -114, 5, 103, 95, -122, 69, 42, 18, 96, 39, -47, -125, -33, -22, 40, 87, 98, 63, -16, -103, -18, 19, 74, 44, -123, 64, -33, 48}),
            200, Integer.class
    );
    public static final Preference STEALTH_CHAT_BUTTON_ALPHA = new Preference(
            /*STEALTH_CHAT_BUTTON_ALPHA*/ decryptMsg(new byte[]{-107, -115, -21, 105, -35, -87, 60, 126, -85, 67, -6, 77, -21, -35, -93, -25, -124, 105, 94, -90, 120, 47, 22, -49, 80, 49, -23, 75, 92, 112, -80, 6}),
            100, Integer.class
    );
    public static final Preference STEALTH_CHAT_BUTTON_PADDING = new Preference(
            /*STEALTH_CHAT_BUTTON_PADDING*/ decryptMsg(new byte[]{-107, -115, -21, 105, -35, -87, 60, 126, -85, 67, -6, 77, -21, -35, -93, -25, 4, 19, -25, -111, -70, -112, 3, 34, 30, 111, -119, -117, 124, 52, -65, 71}),
            10, Integer.class
    );
    public static final Preference STEALTH_SNAP_BUTTON_ALPHA = new Preference(
            /*STEALTH_SNAP_BUTTON_ALPHA*/ decryptMsg(new byte[]{41, -115, 120, -80, -122, 79, 65, 110, 102, -49, 101, -93, -21, -79, -67, -74, -124, 105, 94, -90, 120, 47, 22, -49, 80, 49, -23, 75, 92, 112, -80, 6}),
            100, Integer.class
    );
    public static final Preference STEALTH_SNAP_BUTTON_MARGIN = new Preference(
            /*STEALTH_SNAP_BUTTON_MARGIN*/ decryptMsg(new byte[]{41, -115, 120, -80, -122, 79, 65, 110, 102, -49, 101, -93, -21, -79, -67, -74, 104, -37, 90, -27, 91, 28, -117, 107, 123, -104, -95, 9, 76, -96, -50, -38}),
            10, Integer.class
    );
    public static final Preference STEALTH_SNAP_BUTTON_SIZE = new Preference(
            /*STEALTH_SNAP_BUTTON_SIZE*/ decryptMsg(new byte[]{41, -115, 120, -80, -122, 79, 65, 110, 102, -49, 101, -93, -21, -79, -67, -74, -62, 41, 15, -118, 36, -72, 90, 98, 11, -50, -16, -16, -105, -8, 30, -86}),
            50, Integer.class
    );

    /**
     * ===========================================================================
     * Longs
     * ===========================================================================
     */
    public static final Preference LAST_CHECK_KNOWN_BUGS = new Preference(
            /*LAST_CHECK_KNOWN_BUGS*/ decryptMsg(new byte[]{-39, -92, 43, 89, -9, 43, -83, -21, 68, 22, -81, -57, 21, 122, 75, -57, -124, 27, -31, -67, -106, -10, -9, -123, 42, -19, 45, -52, 82, 1, -104, 71}),
            0L, Long.class
    );
}
