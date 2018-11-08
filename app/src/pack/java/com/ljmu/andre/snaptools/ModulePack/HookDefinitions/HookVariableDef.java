package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.HookVariable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 *
 * <p>
 *     Class References of each Variable are in JavaDoc. Once again, classes in parentheses mean
 *     the class names of the last Update. When updating, just ignore them.<br>
 *     <strong>_var_</strong> is the thing you name you need to find.<br>
 *     <strong>_any_</strong> is just a placeholder for all the obfuscated stuff
 * </p>
 */
public class HookVariableDef extends ConstantDefiner<HookVariable> {
    /**
     * Class: {@link HookClassDef#RECEIVED_SNAP}
     * <p>
     * Code Chunk:
     * <code>
     * return (int) (this._var_ * 1000.0d);
     * </code>
     * </p>
     */
    public static final HookVariable MCANONICALDISPLAYNAME = new HookVariable(
            "MCANONICALDISPLAYNAME",
            "aL"
    );

    // ===========================================================================

    /**
     * Class: Parent of {@link HookClassDef#STORY_ADVANCER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         this._var_ = _any_.NONE;
     *     </code>
     * </p>
     */
    public static final HookVariable STORY_ADVANCER_DISPLAY_STATE = new HookVariable(
            "STORY_ADVANCER_DISPLAY_STATE",
            "f"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#RECEIVED_SNAP}
     * <p>
     *     Code Chunk:
     *     <code>
     *         DiscoverStorySnapModel.ISZIPPED, this.
     *     </code>
     * </p>
     */
    public static final HookVariable SNAP_IS_ZIPPED = new HookVariable(
            "SNAP_IS_ZIPPED",
            "aJ"
    );

    // ===========================================================================

    /**
     * Class: ansy (rbi)
     * <p>
     *     Code Chunk:
     *     <code>
     *         public long _var_ = -1;
     *     </code>
     * </p>
     */
    public static final HookVariable SENT_MEDIA_TIMESTAMP = new HookVariable(
            "SENT_MEDIA_TIMESTAMP",
            "bE"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#SENT_SNAP_BASE}
     * <p>
     *     Code Chunk:
     *     <code>
     *         private void _any_() {
     *             super.a(this._var_);
     *             this._var_ = null;
     *             this._any_ = _any_.RECYCLE;
     *         }
     *     </code>
     * </p>
     */
    public static final HookVariable SENT_MEDIA_BITMAP = new HookVariable(
            "SENT_MEDIA_BITMAP",
            "aF"
    );

    // ===========================================================================

    /**
     * Class: Parent of {@link HookClassDef#SENT_SNAP_BASE}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final Uri _var_;
     *     </code><br>
     *     Also
     *     <code><br>
     *         return this._var_ == null ? null : this._var_.getPath();
     *     </code>
     * </p>
     */
    public static final HookVariable SENT_MEDIA_VIDEO_URI = new HookVariable(
            "SENT_MEDIA_VIDEO_URI",
            "aO"
    );

    /**
     * Class: {@link HookClassDef#SENT_BATCHED_VIDEO}
     * <p>
     * Code Chunk:
     * <code>
     * "Original video has no Magikarp playback metadata", this._var_._any_
     * </code>
     * </p>
     */
    public static final HookVariable SENT_BATCHED_VIDEO_MEDIAHOLDER = new HookVariable(
            "SENT_BATCHED_VIDEO_MEDIAHOLDER",
            "c"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#SENT_VIDEO}
     * <p>
     *     Code Chunk:
     *     <code>
     *         private _int_ _var_;
     *     </code>
     * </p>
     * <strong>Important: _int_ = Interface of {@link HookClassDef#SENT_BATCHED_VIDEO}</strong>
     */
    public static final HookVariable SENT_MEDIA_BATCH_DATA = new HookVariable(
            "SENT_MEDIA_BATCH_DATA",
            "cf"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#CHAT_VIDEO_METADATA}
     * <p>
     *     Code Chunk:
     *     <code>
     *         if (this._var_ == null) {
     *             throw new IllegalStateException("Chat media is null before playing");
     *         }
     *     </code>
     * </p>
     * <strong>Instance of {@link HookClassDef#CHAT_VIDEO}</strong>
     */
    public static final HookVariable CHAT_METADATA_MEDIA = new HookVariable(
            "CHAT_METADATA_MEDIA",
            "c"
    );

    // ===========================================================================

    public static final HookVariable NO_AUTO_ADVANCE = new HookVariable(
            "NO_AUTO_ADVANCE",
            "NO_AUTO_ADVANCE"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#LENS_CATEGORY}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final String _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable LENS_CATEGORY = new HookVariable(
            "LENS_CATEGORY",
            "a"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#LENS_CATEGORY}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final ActivatorType _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable LENS_ACTIVATOR = new HookVariable(
            "LENS_ACTIVATOR",
            "b"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#CAPTION_MANAGER_CLASS}
     * <p>
     *     Code Chunk:
     *     <code>
     *         private final SnapCaptionView _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable SNAPCAPTIONVIEW_CONTEXT = new HookVariable(
            "SNAPCAPTIONVIEW_CONTEXT",
            "b"
    );
    //TODO

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#ENCRYPTED_STREAM_BUILDER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         (this._var_ && _any_.markSupported())
     *     </code>
     * </p>
     */
    public static final HookVariable STREAM_TYPE_CHECK_BOOLEAN = new HookVariable(
            "STREAM_TYPE_CHECK_BOOLEAN",
            "d"
    );

    // ===========================================================================

    /**
     * Class: Parent of {@link HookClassDef#STORY_ADVANCER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         this._any_._any_("OPEN_VIEW_DISPLAYED", this._var_, _any_._any_("page_view_id", this._any_, _any_._any_, Long.valueOf(_any_._any_())));
     *     </code>
     * </p>
     */
    public static final HookVariable STORY_ADVANCER_METADATA = new HookVariable(
            "STORY_ADVANCER_METADATA",
            "c"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#CHAT_MESSAGE_VIEW_HOLDER}
     * <p>
     *     Code Chunk (~Line 113):
     *     <code>
     *         this._var_._any_(this._any_);
     *         this._var._any_((_any_) this._any_);
     *     </code>
     * </p>
     */
    public static final HookVariable CHAT_SAVING_LINKER = new HookVariable(
            "CHAT_SAVING_LINKER",
            "B"
    );

    // ===========================================================================

    /**
     * Class: ahsr (Type of {@link #CHAT_SAVING_LINKER})
     * <p>
     *     Code Chunk:
     *     <code>
     *         public _any_ _var_;
     *         public _any_ _any_;
     *         public boolean _any_;
     *     </code><br>
     *     <strong>The only interfaced non final field</strong>
     * </p>
     */
    public static final HookVariable CHAT_SAVING_LINKER_CHAT_REF = new HookVariable(
            "CHAT_SAVING_LINKER_CHAT_REF",
            "d"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#SENT_VIDEO}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final Long _any_() {
     *             if (this._var_ == null || this._int_ == null) {
     *                 return null;
     *             }
     *             return Long.valueOf((long) this._int_.intValue());
     *         }
     *     </code><br>
     *     <strong>_var_ = SparseArray and _int_ = an Integer Object</strong>
     * </p>
     */
    public static final HookVariable BATCHED_MEDIA_LIST = new HookVariable(
            "BATCHED_MEDIA_LIST",
            "aK"
    );

    // ===========================================================================

    /**
     * Class: ancp
     * <p>
     *     Code Chunk:
     *     <code>
     *         public boolean _var_;
     *     </code><br><br>
     *     IMPORTANT: Code Chunk (~Line 32) in ancp ( = _cls_):
     *     <code>
     *         StringBuffer stringBuffer = new StringBuffer();
     *         for (int i3 = 0; i3 < _any_.size(); i3++) {
     *             _cls_ _cls_ = (_cls_) _any_.valueAt(i3);
     *             stringBuffer.append(_cls_._any_);
     *             stringBuffer.append(':');
     *             stringBuffer.append(_cls_._var_);
     *             stringBuffer.append(',');
     *         }
     *     </code>
     * </p>
     */
    public static final HookVariable BATCHED_MEDIA_ITEM_BOOLEAN = new HookVariable(
            "BATCHED_MEDIA_ITEM_BOOLEAN",
            "e"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#LENS_CATEGORY_RESOLVER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final Map&lt;String, _any_&gt; _var_ = new HashMap();
     *     </code>
     * </p>
     */
    public static final HookVariable LENS_CATEGORY_MAP = new HookVariable(
            "LENS_CATEGORY_MAP",
            "a"
    );

    // ===========================================================================

    public static final HookVariable GEOFILTER_VIEW_CREATION_ARG3 = new HookVariable(
            "GEOFILTER_VIEW_CREATION_ARG3",
            "a"

            /** CLASS: {@link HookClassDef.FILTER_METADATA_LOADER} =======================*/
            // CODE CHUNK ================================================================
            /**
             * arrayList.add(ghb.a(a, context, this.a));
             *                                 ^^^^^^
             */
    ); // TODO: REDO

    // ===========================================================================

    /**
     * Class: apgz (sof) ( = _cls_)
     * <p>
     *     Code Chunk:
     *     <code>
     *         this._var_ = _cls_._var_(bundle.getString("type"));
     *     </code>
     * </p>
     */
    public static final HookVariable NOTIFICATION_TYPE = new HookVariable(
            "NOTIFICATION_TYPE",
            "a"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#RECEIVED_SNAP_PAYLOAD_BUILDER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final _any_ getRequestPayload() {
     *             this._any_ = new HashMap(this._var_._any_());
     *     </code>
     * </p>
     */
    public static final HookVariable RECEIVED_SNAP_PAYLOAD_HOLDER = new HookVariable(
            "RECEIVED_SNAP_PAYLOAD_HOLDER",
            "b"
    );

    // ===========================================================================

    /**
     * Class: zlw (dyq)
     * <p>
     *     Code Chunk:
     *     <code>
     *         final Map&lt;String, _any_&gt; _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable RECEIVED_SNAP_PAYLOAD_MAP = new HookVariable(
            "RECEIVED_SNAP_PAYLOAD_MAP",
            "a"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#STORY_SNAP_PAYLOAD_BUILDER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         private final List&lt;_any_&gt; _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable STORY_UPDATE_METADATA_LIST = new HookVariable(
            "STORY_UPDATE_METADATA_LIST",
            "b"
    );

    // ===========================================================================

    /**
     * Class: ankr (qtg)
     * <p>
     *     Code Chunk:
     *     <code>
     *         public final _any_ _var_ = new _any_();
     *     </code>
     * </p>
     */
    public static final HookVariable STORY_UPDATE_METADATA = new HookVariable(
            "STORY_UPDATE_METADATA",
            "b"
    );

    // ===========================================================================

    /**
     * Class: aunb (xig)
     * <p>
     *     Code Chunk:
     *     <code>
     *         &#64;SerializedName("flushable_story_id")
     *         protected String _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable STORY_UPDATE_METADATA_ID = new HookVariable(
            "STORY_UPDATE_METADATA_ID",
            "a"
    );

    // ===========================================================================

//	public static final HookVariable SNAPCHAT_FRAGMENT_CONTENT_VIEW = new HookVariable(
//			"SNAPCHAT_FRAGMENT_CONTENT_VIEW",
//			"al"
//
//			/** CLASS: SnapchatFragment ==================================================*/
//			// CODE CHUNK ================================================================
//			/**
//			 * public View e_(int i) {
//			 *      return this.al.findViewById(i);
//			 * }
//			 */
//	);

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#CHAT_V10_BUILDER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         this._var_ = ((ViewStub) this._any_._any_(R.id.top_panel_stub)).inflate();
     *     </code>
     * </p>
     */
    public static final HookVariable CHAT_TOP_PANEL_VIEW = new HookVariable(
            "CHAT_TOP_PANEL_VIEW",
            "o"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#STORY_MANAGER}
     * <p>
     *     Code Chunk:
     *     <code>
     *         protected final Map&lt;String, _cls_&gt; = new ConcurrentHashMap();
     *     </code><br>
     *     <strong>To find _cls_ search for '"StoryCollection"'</strong>
     * </p>
     */
    public static final HookVariable STORY_COLLECTION_MAP = new HookVariable(
            "STORY_COLLECTION_MAP",
            "c"
    );

    // ===========================================================================

    /**
     * Class: {@link HookClassDef#FILTER_METADATA_CREATOR}
     * <p>
     *     Code Chunk:
     *     <code>
     *         private /* synthetic &#42;/ _any_ _var_;
     *     </code>
     * </p>
     */
    public static final HookVariable FILTER_METADATA_CACHE = new HookVariable(
            "FILTER_METADATA_CACHE",
            "a"
    );

    // ===========================================================================

    /**
     * Class: annh (qvs)
     * <p>
     *     Code Chunk:
     *     <code>
     *         private /* synthetic &#42;/ _cls_ _var_;
     *     </code><br>
     *     Also (hashCode()):
     *     <code>return this._var_._any_.hashCode();</code><br>
     *     <strong>_cls_ = {@link HookClassDef#SERIALIZABLE_FILTER_METADATA}</strong>
     * </p>
     */
    public static final HookVariable FILTER_SERIALIZABLE_METADATA = new HookVariable(
            "FILTER_SERIALIZABLE_METADATA",
            "a"
    );

    // ===========================================================================


    /**
     * ===========================================================================
     * UNDEFINED HOOK METHODS
     * ===========================================================================
     * These "variables" are just names of hooks for classes that we do not care
     * about the name of
     * ===========================================================================
     */
//    public static final HookVariable STORY_DATA_IS_SUBSCRIBED = new HookVariable(
//            "STORY_DATA_IS_SUBSCRIBED",
//            "s"
//
//            /** CLASS: {@link HookClassDef.STORY_MANAGER}=================================*/
//             CODE CHUNK ================================================================
//            /**
//             * Find function that returns variable: ", isSubscribed=" + this.s
//             */
//    ); // TODO: DONE
    // ===========================================================================


    public static class HookVariable extends Constant {
        private final String varName;

        HookVariable(String name, String varName) {
            super(name);
            this.varName = varName;
        }

        public String getVarName() {
            return this.varName;
        }
    }
}
