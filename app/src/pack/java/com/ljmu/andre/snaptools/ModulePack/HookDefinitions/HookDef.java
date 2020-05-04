package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.View;
import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HookDef extends ConstantDefiner<HookDef.Hook> {
    public static final Hook AB_TEST_CHECK_BOOLEAN = new Hook("AB_TEST_CHECK_BOOLEAN", HookClassDef.AB_TEST_MANAGER, "a", String.class, String.class, Boolean.TYPE);
    public static final Hook AB_TEST_CHECK_FLOAT = new Hook("AB_TEST_CHECK_FLOAT", HookClassDef.AB_TEST_MANAGER, "a", String.class, String.class, Float.TYPE);
    public static final Hook AB_TEST_CHECK_INT = new Hook("AB_TEST_CHECK_INT", HookClassDef.AB_TEST_MANAGER, "a", String.class, String.class, Integer.TYPE);
    public static final Hook AB_TEST_CHECK_LONG = new Hook("AB_TEST_CHECK_LONG", HookClassDef.AB_TEST_MANAGER, "a", String.class, String.class, Long.TYPE);
    public static final Hook AB_TEST_CHECK_STRING = new Hook("AB_TEST_CHECK_VALUE", HookClassDef.AB_TEST_MANAGER, "a", String.class, String.class, String.class);
    public static final Hook BATCHED_MEDIA_LIMITER = new Hook("BATCHED_MEDIA_LIMITER", HookClassDef.SENT_VIDEO, "aV", new Object[0]);
    public static final Hook CAMERA_IS_VISIBLE = new Hook("CAMERA_IS_VISIBLE", HookClassDef.CAMERA_FRAGMENT, "a", "azdk");
    public static final Hook CAPTION_CREATE_HOOK = new Hook("CAPTION_CREATE_HOOK", HookClassDef.CAPTION_MANAGER_CLASS, "onCreateActionMode", ActionMode.class, Menu.class);
    public static final Hook CHAT_IMAGE_GET_ALGORITHM = new Hook("CHAT_IMAGE_GET_ALGORITHM", HookClassDef.CHAT_IMAGE_METADATA, "a", HookClassDef.CHAT_VIDEO.getStrClass());
    public static final Hook CHAT_ISSAVED_INAPP = new Hook("CHAT_ISSAVED_INAPP", (HookClassDef.HookClass) null, "eC_", new Object[0]);
    public static final Hook CHAT_MESSAGE_VIEW_MEASURE = new Hook("CHAT_MESSAGE_VIEW_MEASURE", HookClassDef.CHAT_MESSAGE_VIEW_HOLDER, "F", new Object[0]);
    public static final Hook CHAT_METADATA_READ = new Hook("CHAT_METADATA_READ", HookClassDef.CHAT_METADATA_JSON_PARSER, "a", "com.google.gson.stream.JsonReader");
    public static final Hook CHAT_METADATA_READ_SECOND = new Hook("CHAT_METADATA_READ_SECOND", HookClassDef.CHAT_METADATA_JSON_PARSER_SECOND, "a", "com.google.gson.stream.JsonReader");
    public static final Hook CHAT_METADATA_WRITE = new Hook("CHAT_METADATA_WRITE", HookClassDef.CHAT_METADATA_JSON_PARSER, "a", "com.google.gson.stream.JsonWriter", HookClassDef.CHAT_METADATA.getStrClass());
    public static final Hook CHAT_METADATA_WRITE_SECOND = new Hook("CHAT_METADATA_WRITE_SECOND", HookClassDef.CHAT_METADATA_JSON_PARSER_SECOND, "a", "com.google.gson.stream.JsonWriter", "bbyv");
    public static final Hook CHAT_NOTIFICATION = new Hook("CHAT_NOTIFICATION", HookClassDef.CHAT_NOTIFICATION_CREATOR, "a", "avxe", "avwy");
    public static final Hook CHAT_SAVE_INAPP = new Hook("CHAT_SAVE_INAPP", HookClassDef.CHAT_MESSAGE_VIEW_HOLDER, "x", new Object[0]);
    public static final Hook CHAT_VIDEO_GET_ALGORITHM = new Hook("CHAT_VIDEO_GET_ALGORITHM", HookClassDef.CHAT_VIDEO_METADATA, "e", new Object[0]);
    public static final Hook CHAT_VIDEO_PATH = new Hook("CHAT_VIDEO_PATH", HookClassDef.CHAT_VIDEO, "eT_", new Object[0]);
    public static final Hook CHECK_LENS_ASSET_AUTH = new Hook("CHECK_LENS_ASSET_AUTH", HookClassDef.LENS_AUTHENTICATION, "a", "ambc", String.class);
    public static final Hook CHECK_LENS_AUTH = new Hook("CHECK_LENS_AUTH", HookClassDef.LENS_AUTHENTICATION, "a", HookClassDef.LENS.getStrClass(), String.class);
    public static final Hook CHECK_LENS_CATEGORY_AUTH = new Hook("CHECK_LENS_CATEGORY_AUTH", HookClassDef.LENS_AUTHENTICATION, "a", "amaw", String.class);
    public static final Hook CONSTRUCTOR_OPERA_PAGE_VIEW = new Hook("CONSTRUCTOR_OPERA_PAGE_VIEW", HookClassDef.OPERA_PAGE_VIEW, (String) null, Context.class);
    public static final Hook COUNTDOWNTIMER_VIEW_ONDRAW = new Hook("COUNTDOWNTIMER_VIEW_ONDRAW", HookClassDef.COUNTDOWNTIMER_VIEW, "onDraw", Canvas.class);
    public static final Hook CREATE_CHEETAH_PROFILE_SETTINGS_VIEW = new Hook("CREATE_CHEETAH_PROFILE_SETTINGS_VIEW", HookClassDef.CHEETAH_PROFILE_SETTINGS_CREATOR, (String) null, View.class);
    public static final Hook DIRECT_GET_ALGORITHM = new Hook("DIRECT_GET_ALGORITHM", HookClassDef.RECEIVED_SNAP_ENCRYPTION, "a", HookClassDef.RECEIVED_SNAP.getStrClass(), String.class);
    public static final Hook DISPATCH_CHAT_UPDATE = new Hook("DISPATCH_CHAT_UPDATE", HookClassDef.NETWORK_DISPATCHER, "a", "aphd", "bbnl");
    public static final Hook ENCRYPTION_ALGORITHM_STREAM = new Hook("ENCRYPTION_ALGORITHM_STREAM", HookClassDef.ENCRYPTION_ALGORITHM, "b", InputStream.class);
    public static final Hook ERROR_SUPPRESS_DOWNLOADER_RUNNABLE = new Hook("ERROR_SUPPRESS_DOWNLOADER_RUNNABLE", HookClassDef.DOWNLOADER_RUNNABLE, "run", new Object[0]);
    public static final Hook EXPERIMENT_PUSH_STATE = new Hook("EXPERIMENT_PUSH_STATE", HookClassDef.EXPERIMENT_BASE, "j", new Object[0]);
    public static final Hook FONT_HOOK = new Hook("FONT_HOOK", HookClassDef.FONT_CLASS, "createFromFile", String.class);
    public static final Hook FRIEND_PROFILE_POPUP_CREATED = new Hook("FRIEND_PROFILE_POPUP_CREATED", HookClassDef.FRIEND_PROFILE_POPUP_FRAGMENT, "onViewCreated", View.class, Bundle.class);
    public static final Hook FRIEND_STORY_TILE_USERNAME = new Hook("FRIEND_STORY_TILE_USERNAME", HookClassDef.STORY_FRIEND_VIEWED, "a", new Object[0]);
    public static final Hook GET_RECEIVED_SNAP_PAYLOAD = new Hook("GET_RECEIVED_SNAP_PAYLOAD", HookClassDef.RECEIVED_SNAP_PAYLOAD_BUILDER, "getRequestPayload", new Object[0]);
    public static final Hook GET_SNAP_ID = new Hook("GET_SNAP_ID", HookClassDef.SNAP_BASE, "h", new Object[0]);
    public static final Hook GET_STORY_SNAP_PAYLOAD = new Hook("GET_STORY_SNAP_PAYLOAD", HookClassDef.STORY_SNAP_PAYLOAD_BUILDER, "getRequestPayload", new Object[0]);
    public static final Hook GET_USERNAME = new Hook("GET_USERNAME", HookClassDef.USER_PREFS, "N");
    public static final Hook GROUP_ALGORITHM_UNWRAPPED = new Hook("GROUP_ALGORITHM_UNWRAPPED", HookClassDef.GROUP_SNAP_WRAPPER, "a", String.class);
    public static final Hook GROUP_GET_ALGORITHM = new Hook("GROUP_GET_ALGORITHM", HookClassDef.GROUP_SNAP_METADATA, "a", "apjb");
    public static final HookDef INST = new HookDef();
    public static final Hook LENS_LOADING = new Hook("LENS_LOADING", HookClassDef.LENS_LOADER, "a", List.class);
    public static final Hook LOAD_INITIAL_STORIES = new Hook("LOAD_INITIAL_STORIES", HookClassDef.STORY_MANAGER, "a", Integer.TYPE, Integer.TYPE, Integer.TYPE, HashMap.class, HashMap.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, List.class, Long.TYPE);
    public static final Hook LOAD_NEW_STORY = new Hook("LOAD_NEW_STORY", HookClassDef.STORY_MANAGER, "a", "auht");
    public static final Hook LOAD_STORIES = new Hook("LOAD_STORIES", HookClassDef.STORY_LOADER, "a", List.class);
    public static final Hook LOAD_STORY_SNAP_ADVERT = new Hook("LOAD_STORY_SNAP_ADVERT", HookClassDef.STORY_SNAP_AD_LOADER, "a", HookClassDef.STORY_SNAP_AD_LOADER.getStrClass(), "ffh");
    public static final Hook MARK_DIRECT_CHAT_VIEWED_PRESENT = new Hook("MARK_DIRECT_CHAT_VIEWED_PRESENT", HookClassDef.CHAT_DIRECT_VIEW_MARKER, "a", "apfi", "bcbx");
    public static final Hook MARK_DIRECT_CHAT_VIEWED_UNPRESENT = new Hook("MARK_DIRECT_CHAT_VIEWED_UNPRESENT", HookClassDef.CHAT_DIRECT_VIEW_MARKER, "b", "apfi", "awqi");
    public static final Hook MARK_GROUP_CHAT_VIEWED = new Hook("MARK_GROUP_CHAT_VIEWED", HookClassDef.CHAT_GROUP_VIEW_MARKER, "a", "aphd", String.class);
    public static final Hook MARK_STORY_VIEWED = new Hook("MARK_STORY_VIEWED", HookClassDef.STORY_STATUS_UPDATER, "a", "atyj", HookClassDef.STORY_SNAP.getStrClass(), Boolean.TYPE);
    public static final Hook NETWORK_EXECUTE_SYNC = new Hook("NETWORK_EXECUTE_SYNC", HookClassDef.NETWORK_MANAGER, "executeSynchronously", new Object[0]);
    public static final Hook NEW_CONCENTRIC_TIMERVIEW_ONDRAW = new Hook("NEW_CONCENTRIC_TIMERVIEW_ONDRAW", HookClassDef.NEW_CONCENTRIC_TIMERVIEW, "onDraw", Canvas.class);
    public static final Hook OPENED_SNAP = new Hook("OPENED_SNAP", HookClassDef.RECEIVED_SNAP, "e", Boolean.TYPE);
    public static final Hook REPLACE_SHARED_IMAGE = new Hook("REPLACE_SHARED_IMAGE", HookClassDef.SHARE_IMAGE, "a", Bitmap.class, Integer.class, String.class, Long.TYPE, Boolean.TYPE, Integer.TYPE, "fye$b");
    public static final Hook REPLACE_SHARED_VIDEO = new Hook("REPLACE_SHARED_VIDEO", HookClassDef.SHARE_VIDEO, "a", Uri.class, Integer.TYPE, Boolean.TYPE, "axec", Long.TYPE, Long.TYPE);
    public static final Hook RESOLVE_LENS_CATEGORY = new Hook("RESOLVE_LENS_CATEGORY", HookClassDef.LENS_CATEGORY_RESOLVER, "a", String.class);
    public static final Hook SCREENSHOT_BYPASS = new Hook("SCREENSHOT_BYPASS", HookClassDef.SCREENSHOT_DETECTOR, "a", LinkedHashMap.class);
    public static final Hook SENT_BATCHED_SNAP = new Hook("SENT_BATCHED_SNAP", HookClassDef.SENT_BATCHED_VIDEO, "d", new Object[0]);
    public static final Hook SENT_SNAP = new Hook("SENT_SNAP", HookClassDef.META_DATA_BUILDER, "a", HookClassDef.SENT_SNAP_BASE.getStrClass());
    public static final Hook SET_SNAP_STATUS = new Hook("SET_SNAP_STATUS", HookClassDef.SNAP_BASE, "a", HookClassDef.SNAP_STATUS.getStrClass());
    public static final Hook SNAP_COUNTDOWN_POSTER = new Hook("SNAP_COUNTDOWN_POSTER", HookClassDef.SNAP_COUNTDOWN_CONTROLLER, "a", Long.TYPE);
    public static final Hook SNAP_GET_MEDIA_TYPE = new Hook("SNAP_GET_MEDIA_TYPE", HookClassDef.SNAP_BASE, "bc_", new Object[0]);
    public static final Hook SNAP_GET_TIMESTAMP = new Hook("SNAP_GET_TIMESTAMP", HookClassDef.STORY_SNAP, "aH_", new Object[0]);
    public static final Hook SNAP_GET_USERNAME = new Hook("SNAP_GET_USERNAME", HookClassDef.RECEIVED_SNAP, "s", new Object[0]);
    public static final Hook STORY_DISPLAYED = new Hook("STORY_DISPLAYED", HookClassDef.STORY_ADVANCER, "F", new Object[0]);
    public static final Hook STORY_GET_ALGORITHM = new Hook("STORY_GET_ALGORITHM", HookClassDef.STORY_SNAP, "ar", new Object[0]);
    public static final Hook STORY_METADATA_BUILDER = new Hook("STORY_METADATA_BUILDER", HookClassDef.STORY_METADATA_LOADER, "a", HookClassDef.STORY_SNAP.getStrClass(), "awbb", "awba", "atyz");
    public static final Hook STORY_METADATA_GET_OBJECT = new Hook("STORY_METADATA_GET_OBJECT", HookClassDef.STORY_METADATA, "a", String.class);
    public static final Hook STORY_METADATA_INSERT_OBJECT = new Hook("STORY_METADATA_INSERT_OBJECT", HookClassDef.STORY_METADATA, "b", String.class, Object.class);
    public static final Hook STREAM_TYPE_CHECK_BYPASS = new Hook("STREAM_TYPE_CHECK_BYPASS", HookClassDef.ENCRYPTED_STREAM_BUILDER, "a", "ye", Integer.TYPE, Integer.TYPE);
    public static final Hook TEXTURE_VIDVIEW_SETLOOPING = new Hook("TEXTURE_VIDVIEW_SETLOOPING", HookClassDef.TEXTURE_VIDEO_VIEW, "setLooping", Boolean.TYPE);
    public static final Hook TEXTURE_VIDVIEW_START = new Hook("TEXTURE_VIDVIEW_START", HookClassDef.TEXTURE_VIDEO_VIEW, "start", new Object[0]);

    public static class Hook extends Constant {
        private final HookClassDef.HookClass hookClass;
        @Nullable
        private final String hookMethod;
        private final Object[] hookParams;

        Hook(String name, HookClassDef.HookClass hookClass2, @Nullable String hookMethod2, Object... hookParams2) {
            super(name);
            this.hookClass = hookClass2;
            this.hookMethod = hookMethod2;
            this.hookParams = hookParams2;
        }

        public HookClassDef.HookClass getHookClass() {
            return this.hookClass;
        }

        @Nullable
        public String getHookMethod() {
            return this.hookMethod;
        }

        public Object[] getHookParams() {
            return this.hookParams;
        }
    }
}
