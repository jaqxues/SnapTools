package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.HookVariable;

import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class HookVariableDef extends ConstantDefiner<HookVariable> {
    public static final HookVariable MCANONICALDISPLAYNAME = new HookVariable(
            /*MCANONICALDISPLAYNAME*/ decryptMsg(new byte[]{50, 81, -54, 88, 53, 101, -116, 63, 121, 50, 78, -93, -49, 56, -100, -8, 72, 91, -9, -39, 95, 40, 66, 47, 4, 8, -43, -8, 71, 2, 69, -74}),
            "az"

            /** CLASS: {@link HookClassDef.RECEIVED_SNAP} =============================*/
            // CODE CHUNK ================================================================
            /**
             return (int) (this.az * 1000.0d);
             ^^^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_ADVANCER_DISPLAY_STATE = new HookVariable(
            /*STORY_ADVANCER_DISPLAY_STATE*/ decryptMsg(new byte[]{-109, -24, -56, -10, 72, -66, 64, 116, 34, -28, 81, 80, -11, -66, 71, 38, 13, 64, 59, 85, -85, 107, -8, 61, -83, -70, -36, -124, -88, -85, 119, -20}),
            "f"

            /** CLASS: Parent Of {@link HookClassDef.STORY_ADVANCER} ==================*/
            // CODE CHUNK ================================================================
            /**
             this.f = bub.NONE;
             ^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable SNAP_IS_ZIPPED = new HookVariable(
            /*SNAP_IS_ZIPPED*/ decryptMsg(new byte[]{-14, 59, -77, -67, -72, -128, -81, 114, 32, -43, 93, 38, -39, 49, -36, -61}),
            "ax"

            /** CLASS: {@link HookClassDef.RECEIVED_SNAP} ============================*/
            // CODE CHUNK ================================================================
            /**
             "isZipped", this.ax
             ^^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable SENT_MEDIA_TIMESTAMP = new HookVariable(
            /*SENT_MEDIA_TIMESTAMP*/ decryptMsg(new byte[]{-38, -92, 68, 109, 54, -65, -120, -42, -73, -108, 64, -43, -66, -124, 99, 13, -29, 31, 63, 57, -114, -91, 40, -3, 53, -94, 99, 106, 81, -13, 20, 47}),
            "bt"

            /** CLASS: rbi ==============================================================*/
            // CODE CHUNK ================================================================
            /**
             *	public long bt = -1;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable SENT_MEDIA_BITMAP = new HookVariable(
            /*SENT_MEDIA_BITMAP*/ decryptMsg(new byte[]{-80, 55, -18, -128, 63, -28, 49, -60, 116, 67, -75, -103, -92, 120, 15, 123, 42, -92, 127, 10, 40, 65, -70, -67, -40, -37, 84, -3, 18, 89, -30, -123}),
            "ay"

            /** CLASS: {@link HookClassDef.SENT_SNAP_BASE} ===========================*/
            // CODE CHUNK ================================================================
            /**
             *	super.a(this.ay);
             *           ^^^^^^
             *	this.ay = null;
             *	this.J = qjw.RECYCLE;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable SENT_MEDIA_VIDEO_URI = new HookVariable(
            /*SENT_MEDIA_VIDEO_URI*/ decryptMsg(new byte[]{22, -20, 3, -101, -69, 21, -34, -11, 28, 62, -112, -29, -121, -118, 100, 10, 110, 36, -24, 116, 74, 25, -70, -29, -33, 120, -54, 29, 109, 20, -83, -35}),
            "aG"

            /** CLASS: Parent Of: {@link HookClassDef.SENT_SNAP_BASE} ==================*/
            // CODE CHUNK ================================================================
            /**
             * public final Uri aG;
             *
             * ALSO: return this.aG == null ? null : this.aG.getPath();
             */
    ); // TODO: DONE

    public static final HookVariable SENT_BATCHED_VIDEO_MEDIAHOLDER = new HookVariable(
            /*SENT_BATCHED_VIDEO_MEDIAHOLDER*/ decryptMsg(new byte[]{10, -125, 35, -42, 35, -31, 107, -43, -60, -10, -51, -44, -105, -90, 73, -91, 75, -24, -63, 86, -1, 64, 39, -25, -111, 56, -34, 99, 59, 78, -77, 0}),
            "c"


            /** CLASS: {@link HookClassDef.SENT_BATCHED_VIDEO} ===========================*/
            // CODE CHUNK ================================================================
            /**
             * "Original video has no Magikarp playback metadata", this.c.bd
             *                                                      ^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable SENT_MEDIA_BATCH_DATA = new HookVariable(
            /*SENT_MEDIA_BATCH_DATA*/ decryptMsg(new byte[]{-15, -10, -124, -62, 49, -27, 56, -45, -14, 123, -24, 37, 69, -9, 38, 53, 68, 120, 33, -116, 5, 38, 87, -83, -60, 80, 10, 18, -39, -5, -56, 116}),
            "bP"

            /** CLASS: Interface Of: {@link HookClassDef.SENT_BATCHED_VIDEO} =============*/
            /** FOUND IN: {@link HookClassDef.SENT_VIDEO} ================================*/
            // CODE CHUNK ================================================================
            /**
             * private rba bG;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable CHAT_METADATA_MEDIA = new HookVariable(
            /*CHAT_METADATA_MEDIA*/ decryptMsg(new byte[]{-121, 39, 18, 111, -11, 22, -38, -87, 28, 110, -86, 88, -108, -108, 122, 78, -90, 16, -40, 0, -122, -109, 86, -48, 103, -111, 121, -56, 121, -2, 45, 116}),
            "c"

            /** CLASS: {@link HookClassDef.CHAT_VIDEO_METADATA} ==========================*/
            // CODE CHUNK ================================================================
            /**
             if (this.c == null) {
             ^^^^^^^
             throw new IllegalStateException("Chat media is null before playing");
             }
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable NO_AUTO_ADVANCE = new HookVariable(
            /*NO_AUTO_ADVANCE*/ decryptMsg(new byte[]{43, -41, 8, -104, -105, -61, 90, -81, 33, 28, -32, 116, -95, -97, -75, 25}),
            /*NO_AUTO_ADVANCE*/ decryptMsg(new byte[]{43, -41, 8, -104, -105, -61, 90, -81, 33, 28, -32, 116, -95, -97, -75, 25})
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable LENS_CATEGORY = new HookVariable(
            /*LENS_CATEGORY*/ decryptMsg(new byte[]{106, 40, 31, 69, 15, -45, 46, -40, 18, 101, -7, 91, -47, 78, -46, -124}),
            "a"
            /** CLASS: {@link HookClassDef.LENS_CATEGORY} ================================*/
            // CODE CHUNK ================================================================
            /**
             public final String a;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable LENS_ACTIVATOR = new HookVariable(
            /*LENS_ACTIVATOR*/ decryptMsg(new byte[]{102, 35, -44, 108, 6, -111, 85, 98, -58, 74, -29, -109, 126, 113, 48, 127}),
            "b"

            /** CLASS: {@link HookClassDef.LENS_CATEGORY} ================================*/
            // CODE CHUNK ================================================================
            /**
             public final ActivatorType b;
             */
    ); // TODO: DONE

    // ===========================================================================
    public static final HookVariable SNAPCAPTIONVIEW_CONTEXT = new HookVariable(
            /*SNAPCAPTIONVIEW_CONTEXT*/ decryptMsg(new byte[]{-32, 116, -114, -63, -52, 89, 70, -42, 120, 64, -22, 47, 83, -118, -76, 77, 109, -29, 9, -92, -26, -17, -124, 17, -2, 117, -103, -37, -6, 100, 114, 106}),
            "b"

            /** CLASS: {@link HookClassDef.CAPTION_MANAGER_CLASS} ========================*/
            // CODE CHUNK ================================================================
            /**
             private final SnapCaptionView b;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STREAM_TYPE_CHECK_BOOLEAN = new HookVariable(
            /*STREAM_TYPE_CHECK_BOOLEAN*/ decryptMsg(new byte[]{-122, 113, 114, -81, 123, -50, -102, -44, -125, 11, 83, 65, -121, -126, -30, 47, -4, -44, -121, 17, -27, -58, -68, 10, -24, -24, 22, 33, 111, -47, 85, -104}),
            "d"

            /** CLASS: {@link HookClassDef.ENCRYPTED_STREAM_BUILDER} =====================*/
            // CODE CHUNK ================================================================
            /**
             (this.d && a.markSupported())
             ^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_ADVANCER_METADATA = new HookVariable(
            /*STORY_ADVANCER_METADATA*/ decryptMsg(new byte[]{-23, 17, 125, 52, 6, 114, -19, 6, -44, 2, 41, 9, -61, -97, 125, -124, 122, -84, 8, -64, 117, 77, -81, -124, 85, 72, 27, -124, -101, -49, -59, 68}),
            "c"

            /** CLASS: Parent Of: {@link HookClassDef.STORY_ADVANCER} ====================*/
            // CODE CHUNK ================================================================
            /**
             this.d.a("OPEN_VIEW_DISPLAYED", this.c, cez.a("page_view_id", this.p, buv.u, Long.valueOf(tkp.c())));
             -----------------------------^^^^^^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable CHAT_SAVING_LINKER = new HookVariable(
            /*CHAT_SAVING_LINKER*/ decryptMsg(new byte[]{-74, -17, 35, 112, 102, 23, 127, 18, -86, -109, 104, 89, -109, -70, 24, -56, 98, 54, -70, 82, -48, -51, 86, -1, 23, 51, 120, -94, 4, -75, 98, -27}),
            "N"

            /** CLASS: {@link HookClassDef.CHAT_MESSAGE_VIEW_HOLDER}======================*/
            // CODE CHUNK ================================================================
            /**
             * this.N.a(new b[]{this.M, this.p});
             * ^^^^^^
             * this.N.a(new b[]{(b) this.L});
             * if (this.Y != null) {
             * 	View view2 = this.Y;
             * 	OnTouchListener mtg = new mtg(this.A);
             * 	mtg.c = new mje$b(this, (byte) 0);
             * 	mtg.d = this.R;
             * 	mtg.b = this.Q;
             * 	view2.setOnTouchListener(mtg);
             * }
             **/
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable CHAT_SAVING_LINKER_CHAT_REF = new HookVariable(
            /*CHAT_SAVING_LINKER_CHAT_REF*/ decryptMsg(new byte[]{-74, -17, 35, 112, 102, 23, 127, 18, -86, -109, 104, 89, -109, -70, 24, -56, 77, 96, -17, 99, -13, -58, 109, 11, 105, 91, 122, -53, 77, 71, -53, -64}),
            "d"

            // CLASS: lru ================================================================
            // CODE CHUNK ================================================================
            /**
             public mnt d;
             ^^^^^^^^^^^^ - The only interfaced non final field
             public mnl e;
             public boolean f;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable BATCHED_MEDIA_LIST = new HookVariable(
            /*BATCHED_MEDIA_LIST*/ decryptMsg(new byte[]{-44, -14, -59, 117, 55, -9, 126, -37, 115, -103, -14, -125, -26, -19, 122, -22, -74, 104, 11, 65, -44, -41, 66, 75, -36, 105, 119, 17, 7, -85, 64, 58}),
            "aB"

            /** CLASS: {@link HookClassDef.SENT_VIDEO} ===================================*/
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable BATCHED_MEDIA_ITEM_BOOLEAN = new HookVariable(
            /*BATCHED_MEDIA_ITEM_BOOLEAN*/ decryptMsg(new byte[]{53, 60, 71, -107, -101, -83, -125, 43, -104, 55, -4, 20, -19, 122, 69, -80, 125, 47, -29, 74, -105, -49, 116, 89, 66, -57, -29, -84, -6, 19, -76, -35}),
            "b"

            /** CLASS: qkn ===============================================================*/
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable LENS_CATEGORY_MAP = new HookVariable(
            /*LENS_CATEGORY_MAP*/ decryptMsg(new byte[]{76, 113, 87, -56, 25, 31, 86, -71, 39, -46, 79, 59, -79, 113, -78, 52, 42, -92, 127, 10, 40, 65, -70, -67, -40, -37, 84, -3, 18, 89, -30, -123}),
            "a"

            /** CLASS: {@link HookClassDef.LENS_CATEGORY_RESOLVER} =======================*/
            // CODE CHUNK ================================================================
            /**
             * public final Map<String, jkq> a = new HashMap();
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable GEOFILTER_VIEW_CREATION_ARG3 = new HookVariable(
            /*GEOFILTER_VIEW_CREATION_ARG3*/ decryptMsg(new byte[]{32, 125, 21, -111, -61, -13, 26, -48, 54, 123, 105, 86, 24, -121, -34, -108, -78, 44, -115, -36, -102, -12, 27, 10, 101, -27, 84, -34, 113, -92, 101, -52}),
            "a"

            /** CLASS: {@link HookClassDef.FILTER_METADATA_LOADER} =======================*/
            // CODE CHUNK ================================================================
            /**
             * arrayList.add(ghb.a(a, context, this.a));
             *                                 ^^^^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable NOTIFICATION_TYPE = new HookVariable(
            /*NOTIFICATION_TYPE*/ decryptMsg(new byte[]{-97, -70, -119, 102, 19, -57, 110, -31, -25, -100, 91, -101, 82, 6, -128, 58, -37, -50, -10, 27, -98, -89, -128, 49, 58, -45, 112, -48, -76, 15, 50, -13}),
            "a"

            /** CLASS: sof ===============================================================*/
            // CODE CHUNK ================================================================
            /**
             * this.a = sob.a(bundle.getString("type"));
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable RECEIVED_SNAP_PAYLOAD_HOLDER = new HookVariable(
            /*RECEIVED_SNAP_PAYLOAD_HOLDER*/ decryptMsg(new byte[]{98, -94, 121, 124, -100, -96, 40, -16, -64, -51, -80, -68, 56, 76, 127, -42, 69, 24, 2, -13, -30, -11, 124, 124, 5, 41, 12, 22, -83, -125, -33, 1}),
            "b"

            /** CLASS: {@link HookClassDef.RECEIVED_SNAP_PAYLOAD_BUILDER} ================*/
            // CODE CHUNK ================================================================
            /**
             * public final rnw getRequestPayload() {
             *      this.d = new HashMap(this.b.b());
             *                               ^^^
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable RECEIVED_SNAP_PAYLOAD_MAP = new HookVariable(
            /*RECEIVED_SNAP_PAYLOAD_MAP*/ decryptMsg(new byte[]{98, -94, 121, 124, -100, -96, 40, -16, -64, -51, -80, -68, 56, 76, 127, -42, 104, 53, -16, -112, -46, 0, 21, -104, 38, -118, -60, -117, 12, 127, -114, -2}),
            "a"

            /** CLASS: dyq ===============================================================*/
            // CODE CHUNK ================================================================
            /**
             * final Map<String, rbl> a;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_UPDATE_METADATA_LIST = new HookVariable(
            /*STORY_UPDATE_METADATA_LIST*/ decryptMsg(new byte[]{-31, -4, -54, 91, -106, 121, -14, -11, -102, -18, 48, 107, 13, -33, 2, -47, 50, -8, -33, -102, 55, -121, 40, 101, 18, 24, -97, 72, -92, -50, 23, 107}),
            "b"

            /** CLASS: {@link HookClassDef.STORY_SNAP_PAYLOAD_BUILDER} ===================*/
            // CODE CHUNK ================================================================
            /**
             * private final List<qtg> b;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_UPDATE_METADATA = new HookVariable(
            /*STORY_UPDATE_METADATA*/ decryptMsg(new byte[]{-31, -4, -54, 91, -106, 121, -14, -11, -102, -18, 48, 107, 13, -33, 2, -47, 35, 108, 78, -36, 77, -82, 21, 99, -117, 72, 87, 32, 49, 74, 108, -30}),
            "a"

            /** CLASS: qjg ===============================================================*/
            // CODE CHUNK ================================================================
            /**
             * public final xig a = new xig();
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_UPDATE_METADATA_ID = new HookVariable(
            /*STORY_UPDATE_METADATA_ID*/ decryptMsg(new byte[]{-31, -4, -54, 91, -106, 121, -14, -11, -102, -18, 48, 107, 13, -33, 2, -47, 34, 86, -39, -107, 121, -94, -38, -12, -50, -97, 63, 13, 28, 55, -114, -95}),
            "a"

            /** CLASS: xig ===============================================================*/
            // CODE CHUNK ================================================================
            /**
             * @SerializedName("id")
             * protected String a;
             */
    ); // TODO: DONE

    // ===========================================================================

//	public static final HookVariable SNAPCHAT_FRAGMENT_CONTENT_VIEW = new HookVariable(
//			/*SNAPCHAT_FRAGMENT_CONTENT_VIEW*/ decryptMsg(new byte[]{-87, 117, 119, -29, -76, 52, 120, -51, 117, 22, -12, 11, 74, 120, -21, -122, -7, 11, 111, -74, -25, 26, 80, 87, 75, -34, 88, 8, 12, -89, 31, 54}),
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

    public static final HookVariable CHAT_TOP_PANEL_VIEW = new HookVariable(
            /*CHAT_TOP_PANEL_VIEW*/ decryptMsg(new byte[]{109, -69, -73, -67, 4, -53, 12, 111, -90, -100, 120, 93, -102, -110, 111, 44, -15, -54, -9, -31, 119, -2, -31, 47, -79, -103, -16, 50, -38, -32, 80, -120}),
            "o"

            /** CLASS: {@link HookClassDef.CHAT_V10_BUILDER}==============================*/
            // CODE CHUNK ================================================================
            /**
             * this.o = ((ViewStub) this.b.e_(R.id.top_panel_stub)).inflate();
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable UI_MODE_NAME = new HookVariable(
            /*UI_MODE_NAME*/ decryptMsg(new byte[]{-58, 89, -102, 121, 51, 53, 85, -57, 15, 121, 46, 0, -18, -14, 25, -40}),
            "d"

            /** CLASS: {@link HookClassDef.CHEETAH_ALLOCATOR}=============================*/
            // CODE CHUNK ================================================================
            /**
             * private volatile String d = null;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable UI_MODE_ENUM = new HookVariable(
            /*UI_MODE_ENUM*/ decryptMsg(new byte[]{-73, -48, 56, -3, -22, 125, 124, 63, 35, 38, 0, 66, -14, 6, 6, -116}),
            "b"

            /** CLASS: {@link HookClassDef.CHEETAH_ALLOCATOR}=============================*/
            // CODE CHUNK ================================================================
            /**
             * private volatile dzk$a$a b = dzk$a$a.OLD_DESIGN;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable STORY_COLLECTION_MAP = new HookVariable(
            /*STORY_COLLECTION_MAP*/ decryptMsg(new byte[]{27, 63, -109, 92, -87, 40, -97, -32, 57, 54, -46, -45, 79, 16, 23, -48, -92, -111, 86, -41, 15, 13, 73, 41, 7, -128, -31, -90, -71, -7, 96, -67}),
            "d"

            /** CLASS: {@link HookClassDef.STORY_MANAGER}=================================*/
            // CODE CHUNK ================================================================
            /**
             * protected final Map<String, qsj> d = new ConcurrentHashMap();
             * qsj -> Search for "StoryCollection"
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable FILTER_METADATA_CACHE = new HookVariable(
            /*FILTER_METADATA_CACHE*/ decryptMsg(new byte[]{54, -48, 117, 63, -48, 81, -62, -2, -78, 1, -30, -50, -119, 94, 43, -44, -88, 23, 33, 83, 88, -112, -66, -31, -33, -76, 10, 59, -79, 17, -6, -10}),
            "a"

            /** CLASS: {@link HookClassDef.FILTER_METADATA_CREATOR}=======================*/
            // CODE CHUNK ================================================================
            /**
             * private /* synthetic / qvs a;
             */
    ); // TODO: DONE

    // ===========================================================================

    public static final HookVariable FILTER_SERIALIZABLE_METADATA = new HookVariable(
            /*FILTER_SERIALIZABLE_METADATA*/ decryptMsg(new byte[]{16, 90, 19, 121, -78, -74, 3, 63, -82, 34, -63, 27, 11, -91, -96, 105, -73, -54, -12, 58, 97, -99, -128, 27, 31, -119, -107, -1, 26, -116, 37, 107}),
            "a"

            /** CLASS: qvs ===============================================================*/
            // CODE CHUNK ================================================================
            /**
             * qvs -> Search for "EncryptedGeoLoggingData is null: server returned empty response for"
             *
             * public final wsa a;
             * wsa -> {@link HookClassDef.SERIALIZABLE_FILTER_METADATA}
             */
    ); // TODO: DONE

    // ===========================================================================


    /**
     * ===========================================================================
     * UNDEFINED HOOK METHODS
     * ===========================================================================
     * These "variables" are just names of hooks for classes that we do not care
     * about the name of
     * ===========================================================================
     */
    public static final HookVariable STORY_DATA_IS_SUBSCRIBED = new HookVariable(
            /*STORY_DATA_IS_SUBSCRIBED*/ decryptMsg(new byte[]{27, -39, 115, 36, 18, -82, -77, 32, 105, -53, 63, -38, 121, -122, 62, -2, -19, -20, 87, -60, 70, -25, 53, -64, 121, 75, 90, 86, 95, 41, -65, 73}),
            "s"

            /** CLASS: {@link HookClassDef.STORY_MANAGER}=================================*/
            // CODE CHUNK ================================================================
            /**
             * Find function that returns variable: ", isSubscribed=" + this.s
             */
    ); // TODO: DONE
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
