package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.Hook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.AB_TEST_MANAGER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CAMERA_FRAGMENT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CAPTION_MANAGER_CLASS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_DIRECT_VIEW_MARKER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_GROUP_VIEW_MARKER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_IMAGE_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_MESSAGE_VIEW_HOLDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA_JSON_PARSER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA_JSON_PARSER_SECOND;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_NOTIFICATION_CREATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_VIDEO;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_VIDEO_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHEETAH_ALLOCATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHEETAH_PROFILE_SETTINGS_CREATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.COUNTDOWNTIMER_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.DOWNLOADER_RUNNABLE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENCRYPTED_STREAM_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENCRYPTION_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.EXPERIMENT_BASE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FILTER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FILTER_METADATA_CREATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FILTER_METADATA_LOADER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FONT_CLASS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FRIEND_PROFILE_POPUP_FRAGMENT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.GEOFILTER_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.GEOFILTER_VIEW_CREATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.GROUP_SNAP_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.IMAGE_GEOFILTER_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_AUTHENTICATION;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_CATEGORY_RESOLVER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_LOADER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.META_DATA_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.NETWORK_DISPATCHER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.NETWORK_MANAGER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.NEW_CONCENTRIC_TIMERVIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.OPERA_PAGE_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.PROFILE_SETTINGS_CREATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.RECEIVED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.RECEIVED_SNAP_ENCRYPTION;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.RECEIVED_SNAP_PAYLOAD_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SCREENSHOT_DETECTOR;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SENT_BATCHED_VIDEO;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SENT_SNAP_BASE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SENT_VIDEO;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SNAP_BASE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SNAP_COUNTDOWN_CONTROLLER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SNAP_STATUS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_ADVANCER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_FRIEND_VIEWED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_LOADER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_MANAGER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_METADATA_LOADER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SNAP_AD_LOADER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SNAP_PAYLOAD_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_STATUS_UPDATER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.TEXTURE_VIDEO_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.USER_PREFS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class HookDef extends ConstantDefiner<Hook> {
    public static final HookDef INST = new HookDef();

    // SAVING ====================================================================
    public static final Hook ENCRYPTION_ALGORITHM_STREAM = new Hook(
            "ENCRYPTION_ALGORITHM_STREAM",
            ENCRYPTION_ALGORITHM, "b", InputStream.class
    ); // TODO: DONE
    public static final Hook STREAM_TYPE_CHECK_BYPASS = new Hook(
            "STREAM_TYPE_CHECK_BYPASS",
            ENCRYPTED_STREAM_BUILDER, "a", "uo", int.class, int.class
    ); // TODO: DONE
    public static final Hook STORY_GET_ALGORITHM = new Hook(
            "STORY_GET_ALGORITHM",
            STORY_SNAP, "aT"
    ); // TODO: DONE
    public static final Hook DIRECT_GET_ALGORITHM = new Hook(
            "DIRECT_GET_ALGORITHM",
            RECEIVED_SNAP_ENCRYPTION, "a", RECEIVED_SNAP.getStrClass(), String.class
    ); // TODO: DONE
    public static final Hook CHAT_IMAGE_GET_ALGORITHM = new Hook(
            "CHAT_IMAGE_GET_ALGORITHM",
            CHAT_IMAGE_METADATA, "b", CHAT_VIDEO.getStrClass()
    ); // TODO: DONE
    public static final Hook CHAT_VIDEO_GET_ALGORITHM = new Hook(
            "CHAT_VIDEO_GET_ALGORITHM",
            CHAT_VIDEO_METADATA, "e"
    ); // TODO: DONE
    public static final Hook CHAT_VIDEO_PATH = new Hook(
            "CHAT_VIDEO_PATH",
            CHAT_VIDEO, "dq_"
    ); // TODO: DONE
    public static final Hook GROUP_GET_ALGORITHM = new Hook(
            "GROUP_GET_ALGORITHM",
            GROUP_SNAP_METADATA, "d", "mpi"
    ); // TODO: DONE
    public static final Hook SENT_SNAP = new Hook(
            "SENT_SNAP",
            META_DATA_BUILDER, "a", SENT_SNAP_BASE.getStrClass()
    ); // TODO: DONE
    public static final Hook SENT_BATCHED_SNAP = new Hook(
            "SENT_BATCHED_SNAP",
            SENT_BATCHED_VIDEO, "a", "rba$b"
    ); // TODO: DONE
    public static final Hook CONSTRUCTOR_OPERA_PAGE_VIEW = new Hook(
            "CONSTRUCTOR_OPERA_PAGE_VIEW",
            OPERA_PAGE_VIEW, null, Context.class
    ); // TODO: DONE
    // ===========================================================================

    // UNLIMITED VIEWING =========================================================
    public static final Hook STORY_METADATA_INSERT_OBJECT = new Hook(
            "STORY_METADATA_INSERT_OBJECT",
            STORY_METADATA, "b", String.class, Object.class
    ); // TODO: DONE
    public static final Hook SNAP_COUNTDOWN_POSTER = new Hook(
            "SNAP_COUNTDOWN_POSTER",
            SNAP_COUNTDOWN_CONTROLLER, "a", long.class
    ); // TODO: DONE
    public static final Hook TEXTURE_VIDVIEW_START = new Hook(
            "TEXTURE_VIDVIEW_START",
            TEXTURE_VIDEO_VIEW, "start"
    ); // TODO: DONE
    public static final Hook TEXTURE_VIDVIEW_SETLOOPING = new Hook(
            "TEXTURE_VIDVIEW_SETLOOPING",
            TEXTURE_VIDEO_VIEW, "setLooping", boolean.class
    ); // TODO: DONE
    // ===========================================================================

    // SHARING ===================================================================
    public static final Hook REPLACE_SHARED_IMAGE = new Hook(
            "REPLACE_SHARED_IMAGE",
            CAMERA_FRAGMENT, "a", Bitmap.class, Integer.class, String.class, long.class, boolean.class, int.class, "sbr$b"
    ); // TODO: DONE
    public static final Hook REPLACE_SHARED_VIDEO = new Hook(
            "REPLACE_SHARED_VIDEO",
            CAMERA_FRAGMENT, "a", Uri.class, int.class, boolean.class, "trq", long.class
    ); // TODO: DONE
    public static final Hook BATCHED_MEDIA_LIMITER = new Hook(
            "BATCHED_MEDIA_LIMITER",
            SENT_VIDEO, "ar"
    ); // TODO: DONE
    public static final Hook CAMERA_IS_VISIBLE = new Hook(
            "CAMERA_IS_VISIBLE",
            CAMERA_FRAGMENT, "onVisible"
    ); // TODO: DONE
    // ===========================================================================

    // LENSES ====================================================================
    public static final Hook LENS_LOADING = new Hook(
            "LENS_LOADING",
            LENS_LOADER, "a", List.class
    ); // TODO: DONE
    public static final Hook CHECK_LENS_AUTH = new Hook(
            "CHECK_LENS_AUTH",
            LENS_AUTHENTICATION, "a", LENS.getStrClass(), String.class
    ); // TODO: DONE
    public static final Hook CHECK_LENS_CATEGORY_AUTH = new Hook(
            "CHECK_LENS_CATEGORY_AUTH",
            LENS_AUTHENTICATION, "a", "jkq", String.class
    ); // TODO: DONE
    public static final Hook CHECK_LENS_ASSET_AUTH = new Hook(
            "CHECK_LENS_ASSET_AUTH",
            LENS_AUTHENTICATION, "a", "jkm", String.class
    ); // TODO: DONE
    public static final Hook RESOLVE_LENS_CATEGORY = new Hook(
            "RESOLVE_LENS_CATEGORY",
            LENS_CATEGORY_RESOLVER, "a", String.class
    ); // TODO: DONE

    // ===========================================================================

    // STORY BLOCKING ============================================================
    public static final Hook LOAD_STORIES = new Hook(
            "LOAD_STORIES",
            STORY_LOADER, "a", List.class
    ); // TODO: DONE
    public static final Hook LOAD_STORY_SNAP_ADVERT = new Hook(
            "LOAD_STORY_SNAP_ADVERT",
            STORY_SNAP_AD_LOADER, "a", "ehu", "wdv"
    ); // TODO: DONE
    public static final Hook FRIEND_STORY_TILE_USERNAME = new Hook(
            "FRIEND_STORY_TILE_USERNAME",
            STORY_FRIEND_VIEWED, "F_"
    ); // TODO: DONE
    public static final Hook FRIEND_PROFILE_POPUP_CREATED = new Hook(
            "FRIEND_PROFILE_POPUP_CREATED",
            FRIEND_PROFILE_POPUP_FRAGMENT, "onViewCreated", View.class, Bundle.class
    ); // TODO: DONE
    public static final Hook LOAD_INITIAL_STORIES = new Hook(
            "LOAD_INITIAL_STORIES",
            STORY_MANAGER, "a", STORY_MANAGER.getStrClass(), int.class, int.class, int.class, HashMap.class, HashMap.class, List.class
    ); // TODO: DONE
    public static final Hook LOAD_NEW_STORY = new Hook(
            "LOAD_NEW_STORY",
            STORY_MANAGER, "a", "qsj"
    ); // TODO: DONE
    // ===========================================================================

    // CHAT MANAGER ===============================================================
    public static final Hook CHAT_METADATA_READ = new Hook(
            "CHAT_METADATA_READ",
            CHAT_METADATA_JSON_PARSER, "a", "com.google.gson.stream.JsonReader"
    ); // TODO: DONE
    public static final Hook CHAT_METADATA_WRITE = new Hook(
            "CHAT_METADATA_WRITE",
            CHAT_METADATA_JSON_PARSER, "a", "com.google.gson.stream.JsonWriter",
            CHAT_METADATA.getStrClass()
    ); // TODO: DONE
    public static final Hook CHAT_METADATA_READ_SECOND = new Hook(
            "CHAT_METADATA_READ_SECOND",
            CHAT_METADATA_JSON_PARSER_SECOND, "a", "com.google.gson.stream.JsonReader"
    ); // TODO: DONE
    public static final Hook CHAT_METADATA_WRITE_SECOND = new Hook(
            "CHAT_METADATA_WRITE_SECOND",
            CHAT_METADATA_JSON_PARSER_SECOND, "a", "com.google.gson.stream.JsonWriter",
            "wxw"
    ); // TODO: DONE
    public static final Hook CHAT_MESSAGE_VIEW_MEASURE = new Hook(
            "CHAT_MESSAGE_VIEW_MEASURE",
            CHAT_MESSAGE_VIEW_HOLDER, "T"
    ); // TODO: DONE
    public static final Hook CHAT_ISSAVED_INAPP = new Hook(
            "CHAT_ISSAVED_INAPP",
            null, "dx_"
            /** FOUND IN -> abstract class mlk*/
    ); // TODO: DONE
    public static final Hook CHAT_SAVE_INAPP = new Hook(
            "CHAT_SAVE_INAPP",
            CHAT_MESSAGE_VIEW_HOLDER, "N"
    ); // TODO: DONE
    public static final Hook CHAT_NOTIFICATION = new Hook(
            "CHAT_NOTIFICATION",
            CHAT_NOTIFICATION_CREATOR, "a", "sof", "sny"
    ); // TODO: DONE

    // SCREENSHOT BYPASS =========================================================
    public static final Hook SCREENSHOT_BYPASS = new Hook(
            "SCREENSHOT_BYPASS",
            SCREENSHOT_DETECTOR, "a", LinkedHashMap.class
    ); // TODO: DONE
//	public static final Hook SET_SCREENSHOT_COUNT = new Hook(
//			"SET_SCREENSHOT_COUNT",
//			SNAP_DATA_JSON, "a", Long.class
//	);
//	public static final Hook SET_VIDEO_SCREENSHOT_COUNT = new Hook(
//			"SET_VIDEO_SCREENSHOT_COUNT",
//			SNAP_DATA_JSON, "c", Long.class
//	);
//	public static final Hook SET_SCREENSHOT_COUNT3 = new Hook(
//			"SET_SCREENSHOT_COUNT",
//			SNAP_DATA_JSON, "d", Long.class
//	);
    // ===========================================================================

    // CUSTOM FILTERS ============================================================
    public static final Hook GEOFILTER_SHOULD_SUBSAMPLE = new Hook(
            "GEOFILTER_SHOULD_SUBSAMPLE",
            IMAGE_GEOFILTER_VIEW, "a", boolean.class
    ); // TODO: DONE
    public static final Hook FILTER_LOAD_METADATA = new Hook(
            "FILTER_LOAD_METADATA",
            FILTER_METADATA_LOADER, "a", List.class, Context.class
    ); // TODO: DONE
    public static final Hook CREATE_FILTER_METADATA = new Hook(
            "CREATE_FILTER_METADATA",
            FILTER_METADATA_CREATOR, "e"
    ); // TODO: DONE
    public static final Hook GET_GEOFILTER_CONTENT_VIEW = new Hook(
            "GET_GEOFILTER_CONTENT_VIEW",
            GEOFILTER_VIEW, "c"
    ); // TODO: DONE
    public static final Hook CREATE_GEOFILTER_VIEW = new Hook(
            "CREATE_GEOFILTER_VIEW",
            GEOFILTER_VIEW_CREATOR, "a", FILTER_METADATA.getStrClass(), Context.class, "bee"
    ); // TODO: DONE
    public static final Hook GEOFILTER_TAPPED = new Hook(
            "GEOFILTER_TAPPED",
            IMAGE_GEOFILTER_VIEW, "a", MotionEvent.class
    ); // TODO: DONE
    // ===========================================================================

    // MISC HOOKS ================================================================
    public static final Hook FONT_HOOK = new Hook(
            "FONT_HOOK",
            FONT_CLASS, "createFromFile", String.class
    ); // TODO: DONE
    public static final Hook CAPTION_CREATE_HOOK = new Hook(
            "CAPTION_CREATE_HOOK",
            CAPTION_MANAGER_CLASS, "onCreateActionMode", ActionMode.class, Menu.class
    ); // TODO: DONE
    public static final Hook CHEETAH_DEFINE_MODE = new Hook(
            "CHEETAH_DEFINE_MODE",
            CHEETAH_ALLOCATOR, "j"
    ); // TODO: DONE
    public static final Hook EXPERIMENT_PUSH_STATE = new Hook(
            "EXPERIMENT_PUSH_STATE",
            EXPERIMENT_BASE, "b"
    ); // TODO: DONE

    // ===========================================================================

    // STEALTH VIEWING ===========================================================
    public static final Hook GET_SNAP_ID = new Hook(
            "GET_SNAP_ID",
            SNAP_BASE, "p"
    ); // TODO: DONE
    public static final Hook SET_SNAP_STATUS = new Hook(
            "SET_SNAP_STATUS",
            SNAP_BASE, "a", SNAP_STATUS.getStrClass()
    ); // TODO: DONE
    public static final Hook MARK_STORY_VIEWED = new Hook(
            "MARK_STORY_VIEWED",
            STORY_STATUS_UPDATER, "a", "qsj", STORY_SNAP.getStrClass(), boolean.class
    ); // TODO: DONE
    public static final Hook GET_RECEIVED_SNAP_PAYLOAD = new Hook(
            "GET_RECEIVED_SNAP_PAYLOAD",
            RECEIVED_SNAP_PAYLOAD_BUILDER, "getRequestPayload"
    ); // TODO: DONE
    public static final Hook GET_STORY_SNAP_PAYLOAD = new Hook(
            "GET_STORY_SNAP_PAYLOAD",
            STORY_SNAP_PAYLOAD_BUILDER, "getRequestPayload"
    ); // TODO: DONE
    public static final Hook NETWORK_EXECUTE_SYNC = new Hook(
            "NETWORK_EXECUTE_SYNC",
            NETWORK_MANAGER, "executeSynchronously"
    ); // TODO: DONE
    public static final Hook DISPATCH_CHAT_UPDATE = new Hook(
            "DISPATCH_CHAT_UPDATE",
            NETWORK_DISPATCHER, "a", "mnl", "wlj"
    ); // TODO: DONE
    //	public static final Hook MARK_CHAT_VIEWED = new Hook(
//			MARK_CHAT_VIEWED",
//			CHAT_MESSAGE_METADATA, "b", long.class
//	);
    public static final Hook MARK_GROUP_CHAT_VIEWED = new Hook(
            "MARK_GROUP_CHAT_VIEWED",
            CHAT_GROUP_VIEW_MARKER, "b", "mnl"
    ); // TODO: DONE
    public static final Hook MARK_DIRECT_CHAT_VIEWED_PRESENT = new Hook(
            "MARK_DIRECT_CHAT_VIEWED_PRESENT",
            CHAT_DIRECT_VIEW_MARKER, "a", "mlq", "tfm"
    ); // TODO: DONE
    public static final Hook MARK_DIRECT_CHAT_VIEWED_UNPRESENT = new Hook(
            "MARK_DIRECT_CHAT_VIEWED_UNPRESENT",
            CHAT_DIRECT_VIEW_MARKER, "b", "tfm", "mlq"
    ); // TODO: DONE
    //	public static final Hook CHAT_V3_FRAGMENT_CREATED = new Hook(
//			"CHAT_V3_FRAGMENT_CREATED",
//			CHAT_V3_FRAGMENT, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class
//	);
    public static final Hook CREATE_PROFILE_SETTINGS_VIEW = new Hook(
            "CREATE_PROFILE_SETTINGS_VIEW",
            PROFILE_SETTINGS_CREATOR, "a", ViewGroup.class, int.class
    ); // TODO: DONE
    public static final Hook CREATE_CHEETAH_PROFILE_SETTINGS_VIEW = new Hook(
            "CREATE_CHEETAH_PROFILE_SETTINGS_VIEW",
            CHEETAH_PROFILE_SETTINGS_CREATOR, null, View.class
    ); // TODO: DONE
    // ===========================================================================

    // FORCED HOOKS ==============================================================
    public static final Hook AB_TEST_CHECK_STRING = new Hook(
            "AB_TEST_CHECK_VALUE",
            AB_TEST_MANAGER, "a", String.class, String.class, String.class
    ); // TODO: DONE
    public static final Hook AB_TEST_CHECK_INT = new Hook(
            "AB_TEST_CHECK_INT",
            AB_TEST_MANAGER, "a", String.class, String.class, int.class
    ); // TODO: DONE
    public static final Hook AB_TEST_CHECK_LONG = new Hook(
            "AB_TEST_CHECK_LONG",
            AB_TEST_MANAGER, "a", String.class, String.class, long.class
    ); // TODO: DONE
    public static final Hook AB_TEST_CHECK_BOOLEAN = new Hook(
            "AB_TEST_CHECK_BOOLEAN",
            AB_TEST_MANAGER, "a", String.class, String.class, boolean.class
    ); // TODO: DONE
    public static final Hook AB_TEST_CHECK_FLOAT = new Hook(
            "AB_TEST_CHECK_FLOAT",
            AB_TEST_MANAGER, "a", String.class, String.class, float.class
    ); // TODO: DONE

    // ===========================================================================

    // ERROR SUPPRESSION (FORCED HOOKS) ==========================================
    public static final Hook ERROR_SUPPRESS_DOWNLOADER_RUNNABLE = new Hook(
            "ERROR_SUPPRESS_DOWNLOADER_RUNNABLE",
            DOWNLOADER_RUNNABLE, "run"
    ); // TODO: DONE
    // ===========================================================================

    public static final Hook SNAP_GET_USERNAME = new Hook(
            "SNAP_GET_USERNAME",
            RECEIVED_SNAP, "dC_"
    ); // TODO: DONE
    public static final Hook SNAP_GET_TIMESTAMP = new Hook(
            "SNAP_GET_TIMESTAMP",
            STORY_SNAP, "l"
    ); // TODO: DONE
    public static final Hook SNAP_GET_MEDIA_TYPE = new Hook(
            "SNAP_GET_MEDIA_TYPE",
            SNAP_BASE, "i"
    ); // TODO: DONE
    public static final Hook STORY_METADATA_GET_OBJECT = new Hook(
            "STORY_METADATA_GET_OBJECT",
            STORY_METADATA, "a", String.class
    ); // TODO: DONE
    public static final Hook STORY_METADATA_BUILDER = new Hook(
            "STORY_METADATA_BUILDER",
            STORY_METADATA_LOADER, "a", STORY_SNAP.getStrClass(), "ssf", "sse", "qsz"
    ); // TODO: DONE
    public static final Hook STORY_DISPLAYED = new Hook(
            "STORY_DISPLAYED",
            STORY_ADVANCER, "E"
    ); // TODO: DONE
    public static final Hook OPENED_SNAP = new Hook(
            "OPENED_SNAP",
            RECEIVED_SNAP, "e", boolean.class
    ); // TODO: DONE
    public static final Hook NEW_CONCENTRIC_TIMERVIEW_ONDRAW = new Hook(
            "NEW_CONCENTRIC_TIMERVIEW_ONDRAW",
            NEW_CONCENTRIC_TIMERVIEW, "onDraw", Canvas.class
    ); // TODO: DONE
    public static final Hook COUNTDOWNTIMER_VIEW_ONDRAW = new Hook(
            "COUNTDOWNTIMER_VIEW_ONDRAW",
            COUNTDOWNTIMER_VIEW, "onDraw", Canvas.class
    ); // TODO: DONE
    public static final Hook GET_USERNAME = new Hook(
            "GET_USERNAME",
            USER_PREFS, "L"
    ); // TODO: DONE

    // CONSTRUCTORS ==============================================================


    // ===========================================================================

    public static class Hook extends Constant {
        private final HookClass hookClass;
        @Nullable
        private final String hookMethod;
        private final Object[] hookParams;

        Hook(String name, HookClass hookClass, @Nullable String hookMethod, Object... hookParams) {
            super(name);

            this.hookClass = hookClass;
            this.hookMethod = hookMethod;
            this.hookParams = hookParams;
        }

        // ===========================================================================

        public HookClass getHookClass() {
            return hookClass;
        }

        @Nullable
        public String getHookMethod() {
            return hookMethod;
        }

        public Object[] getHookParams() {
            return hookParams;
        }
    }
}
