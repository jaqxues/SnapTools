package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;

public class HookVariableDef extends ConstantDefiner<HookVariableDef.HookVariable> {
    public static final HookVariable BATCHED_MEDIA_ITEM_BOOLEAN = new HookVariable("BATCHED_MEDIA_ITEM_BOOLEAN", "e");
    public static final HookVariable BATCHED_MEDIA_LIST = new HookVariable("BATCHED_MEDIA_LIST", "aK");
    public static final HookVariable CHAT_METADATA_MEDIA = new HookVariable("CHAT_METADATA_MEDIA", "c");
    public static final HookVariable CHAT_SAVING_LINKER = new HookVariable("CHAT_SAVING_LINKER", "B");
    public static final HookVariable CHAT_SAVING_LINKER_CHAT_REF = new HookVariable("CHAT_SAVING_LINKER_CHAT_REF", "d");
    public static final HookVariable CHAT_TOP_PANEL_VIEW = new HookVariable("CHAT_TOP_PANEL_VIEW", "o");
    public static final HookVariable FILTER_METADATA_CACHE = new HookVariable("FILTER_METADATA_CACHE", "a");
    public static final HookVariable FILTER_SERIALIZABLE_METADATA = new HookVariable("FILTER_SERIALIZABLE_METADATA", "a");
    public static final HookVariable GEOFILTER_VIEW_CREATION_ARG3 = new HookVariable("GEOFILTER_VIEW_CREATION_ARG3", "a");
    public static final HookVariable GROUP_ALGORITHM_WRAPPER_FIELD = new HookVariable("GROUP_ALGORITHM_WRAPPER_FIELD", "b");
    public static final HookVariable LENS_ACTIVATOR = new HookVariable("LENS_ACTIVATOR", "b");
    public static final HookVariable LENS_CATEGORY = new HookVariable("LENS_CATEGORY", "a");
    public static final HookVariable LENS_CATEGORY_MAP = new HookVariable("LENS_CATEGORY_MAP", "a");
    public static final HookVariable MCANONICALDISPLAYNAME = new HookVariable("MCANONICALDISPLAYNAME", "aK");
    public static final HookVariable NOTIFICATION_TYPE = new HookVariable("NOTIFICATION_TYPE", "a");
    public static final HookVariable NO_AUTO_ADVANCE = new HookVariable("NO_AUTO_ADVANCE", "NO_AUTO_ADVANCE");
    public static final HookVariable RECEIVED_SNAP_PAYLOAD_HOLDER = new HookVariable("RECEIVED_SNAP_PAYLOAD_HOLDER", "b");
    public static final HookVariable RECEIVED_SNAP_PAYLOAD_MAP = new HookVariable("RECEIVED_SNAP_PAYLOAD_MAP", "a");
    public static final HookVariable SENT_BATCHED_VIDEO_MEDIAHOLDER = new HookVariable("SENT_BATCHED_VIDEO_MEDIAHOLDER", "c");
    public static final HookVariable SENT_MEDIA_BATCH_DATA = new HookVariable("SENT_MEDIA_BATCH_DATA", "cf");
    public static final HookVariable SENT_MEDIA_BITMAP = new HookVariable("SENT_MEDIA_BITMAP", "aF");
    public static final HookVariable SENT_MEDIA_TIMESTAMP = new HookVariable("SENT_MEDIA_TIMESTAMP", "bE");
    public static final HookVariable SENT_MEDIA_VIDEO_URI = new HookVariable("SENT_MEDIA_VIDEO_URI", "aO");
    public static final HookVariable SNAPCAPTIONVIEW_CONTEXT = new HookVariable("SNAPCAPTIONVIEW_CONTEXT", "b");
    public static final HookVariable SNAP_IS_ZIPPED = new HookVariable("SNAP_IS_ZIPPED", "aJ");
    public static final HookVariable STORY_ADVANCER_DISPLAY_STATE = new HookVariable("STORY_ADVANCER_DISPLAY_STATE", "f");
    public static final HookVariable STORY_ADVANCER_METADATA = new HookVariable("STORY_ADVANCER_METADATA", "c");
    public static final HookVariable STORY_COLLECTION_MAP = new HookVariable("STORY_COLLECTION_MAP", "c");
    public static final HookVariable STORY_UPDATE_METADATA = new HookVariable("STORY_UPDATE_METADATA", "b");
    public static final HookVariable STORY_UPDATE_METADATA_ID = new HookVariable("STORY_UPDATE_METADATA_ID", "a");
    public static final HookVariable STORY_UPDATE_METADATA_LIST = new HookVariable("STORY_UPDATE_METADATA_LIST", "b");
    public static final HookVariable STREAM_TYPE_CHECK_BOOLEAN = new HookVariable("STREAM_TYPE_CHECK_BOOLEAN", "d");

    public static class HookVariable extends Constant {
        private final String varName;

        HookVariable(String name, String varName2) {
            super(name);
            this.varName = varName2;
        }

        public String getVarName() {
            return this.varName;
        }
    }
}
