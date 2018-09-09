package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import android.support.annotation.NonNull;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;

import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * ===========================================================================
 * Hook Generation | Contains: Classes, Methods/Constructors, Variables
 * ===========================================================================
 */
@SuppressWarnings({"WeakerAccess"})
public class HookClassDef extends ConstantDefiner<HookClass> {
    public static final HookClassDef INST = new HookClassDef();

    public static final HookClass SCREENSHOT_DETECTOR = new HookClass(
            /*SCREENSHOT_DETECTOR*/ decryptMsg(new byte[]{-17, -38, -62, 73, -80, 51, 67, -4, -37, -29, -114, 81, -96, 120, -100, -93, 84, -119, 86, 48, -41, -84, -103, -110, 55, -12, 85, -47, 43, -49, -22, 90}),
            /*ssr*/ decryptMsg(new byte[]{25, 53, 118, 113, 85, 80, -64, -60, -64, -84, -103, 8, -8, -14, -61, -39})
    );// qtz // TODO: DONE
    //	public static final HookClass SNAP_DATA_JSON = new HookClass(
//			/*SNAP_DATA_JSON*/ decryptMsg(new byte[]{-93, 21, 88, -115, -63, 105, -59, -35, -5, 104, -29, 64, 117, -100, 76, 87}),
//			/*umf*/ decryptMsg(new byte[]{25, -90, 33, -88, -30, 1, 58, -113, 73, -119, -15, 78, 51, 30, -51, -51})
//	);
    public static final HookClass TEXTURE_VIDEO_VIEW = new HookClass(
            /*TEXTURE_VIDEO_VIEW*/ decryptMsg(new byte[]{-23, 114, -85, -36, 9, 50, 56, 8, 7, 32, -96, -9, 45, -115, 29, 15, 48, 22, 61, -1, -77, 13, 76, -104, -50, -3, 104, -30, 102, 19, -13, -75}),
            /*com.snap.opera.shared.view.TextureVideoView*/ decryptMsg(new byte[]{-74, 38, -74, -47, -126, 47, -93, -40, -81, -103, -85, 109, 45, 97, 4, 14, 32, -7, -124, 101, 50, -21, 60, -118, -69, -126, -34, 112, 48, -104, 86, 55, 127, -113, 14, -33, 35, -55, 98, 46, -104, 35, 27, -101, -121, -93, 10, 72})
    ); // com.snapchat.opera.shared.view.TextureVideoView // TODO: DONE
    public static final HookClass SNAP_COUNTDOWN_CONTROLLER = new HookClass(
            /*SNAP_COUNTDOWN_CONTROLLER*/ decryptMsg(new byte[]{69, -63, -15, 21, 49, -124, 0, -4, 115, 72, -99, -45, 67, -122, -111, 72, 47, -67, -24, -24, -71, -19, 33, 58, 78, 5, -103, 24, -65, 116, -74, -46}),
            /*qiu*/ decryptMsg(new byte[]{-93, 73, 21, 77, 97, -26, -64, -87, -45, -46, -32, -53, 16, 59, -73, 84})
    );// okm // TODO: DONE
    public static final HookClass ENCRYPTION_ALGORITHM = new HookClass(
            /*ENCRYPTION_ALGORITHM*/ decryptMsg(new byte[]{95, -64, 95, 21, 52, -52, 92, -16, -33, -81, 77, -44, 74, 81, 54, -76, 26, 47, -104, 46, -32, -96, -36, -62, 71, -123, -62, -24, 36, -90, 110, -78}),
            /*com.snapchat.android.framework.crypto.CbcEncryptionAlgorithm*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, 22, 78, 101, 111, -27, 83, -27, -120, 27, -122, -66, -127, 78, 26, 117, 9, -61, -7, -80, 114, -9, 123, -98, 107, -52, -78, 61, -91, -108, -57, -45, 127, 55, 53, 124, -89, -105, 41, -56, -21, 92, 118, 73, -19, 21, 56, -19, -8})
    ); // com.snapchat.android.framework.crypto.CbcEncryptionAlgorithm // TODO: DONE
    public static final HookClass ENCRYPTED_STREAM_BUILDER = new HookClass(
            /*ENCRYPTED_STREAM_BUILDER*/ decryptMsg(new byte[]{-82, -81, -56, 25, 97, -114, 22, 84, 119, -54, -98, -14, -35, 58, 48, -43, -48, -110, -45, -34, 120, -13, 63, 45, 100, -102, 17, -47, 56, 85, 114, -101}),
            /*sfo*/ decryptMsg(new byte[]{-8, -108, -70, 26, -21, -65, -112, 57, -116, -123, 76, -58, -115, -120, -20, 50})
    ); // qgr // TODO: DONE
    public static final HookClass STORY_METADATA = new HookClass(
            /*STORY_METADATA*/ decryptMsg(new byte[]{-4, -25, 114, 16, 71, 20, -43, 105, -8, 20, 123, -105, -56, -11, 6, -17}),
            /*cez*/ decryptMsg(new byte[]{-23, -14, -112, -79, 48, 58, 67, -120, -108, -91, -81, 37, 70, 78, 101, 3})
    ); // tul // TODO: DONE
    public static final HookClass SNAP_BASE = new HookClass(
            /*SNAP_BASE*/ decryptMsg(new byte[]{-83, 10, -15, 71, -24, 49, 101, 73, 0, -87, 7, -72, -7, 98, -7, -40}),
            /*mph*/ decryptMsg(new byte[]{11, 91, 101, 63, 91, 6, -40, -122, -71, -94, -84, -102, -87, 88, 17, -99})
    ); // kxs // TODO: DONE
    public static final HookClass SNAP_STATUS = new HookClass(
            /*SNAP_STATUS*/ decryptMsg(new byte[]{63, -106, 13, 61, -78, -19, 25, -113, -70, -64, -96, -55, -65, 80, 22, -59}),
            /*mph$a*/ decryptMsg(new byte[]{-119, 18, -60, 117, -72, -84, -52, 30, 65, 80, -87, 111, -124, -120, -69, 87})
    ); // kxs$a // TODO: DONE
    public static final HookClass RECEIVED_SNAP = new HookClass(
            /*RECEIVED_SNAP*/ decryptMsg(new byte[]{114, 25, 89, -22, 28, -103, -19, 78, 2, -92, -19, -91, -56, 0, -5, 82}),
            /*rbl*/ decryptMsg(new byte[]{-95, 109, -62, -97, 96, -100, -23, 72, -12, 4, -30, -27, -55, 103, 28, -65})
    ); // pcb // TODO: DONE
    public static final HookClass RECEIVED_SNAP_ENCRYPTION = new HookClass(
            /*RECEIVED_SNAP_ENCRYPTION*/ decryptMsg(new byte[]{-39, 28, -56, 11, 22, 62, 121, -100, 49, 124, -87, -60, 114, -68, -9, 22, 76, 73, -35, -87, 48, -73, -6, 86, -92, -20, 7, 49, 86, 120, -15, -30}),
            /*mqt*/ decryptMsg(new byte[]{-103, -37, 39, 2, -60, 42, -23, -118, -116, 37, 105, 85, -105, -57, -1, -96})
    ); // kyr // TODO: DONE
    public static final HookClass RECEIVED_SNAP_PAYLOAD_BUILDER = new HookClass(
            /*RECEIVED_SNAP_PAYLOAD_BUILDER*/ decryptMsg(new byte[]{98, -94, 121, 124, -100, -96, 40, -16, -64, -51, -80, -68, 56, 76, 127, -42, 34, -24, 62, -24, 70, -119, 60, 127, -16, 2, -117, 52, 14, -26, 102, -121}),
            /*dys*/ decryptMsg(new byte[]{54, -95, -70, -75, -104, -24, -20, 2, 36, -104, 74, 45, 127, 66, -26, -44})
    ); // dfy // TODO: DONE
    public static final HookClass STORY_SNAP_PAYLOAD_BUILDER = new HookClass(
            /*STORY_SNAP_PAYLOAD_BUILDER*/ decryptMsg(new byte[]{100, -68, 31, -7, -51, 31, 85, 68, -8, 104, 93, 55, 100, 4, 94, -3, -55, 106, 29, 18, -16, 39, -125, 114, -102, -65, -26, -54, -13, 39, -99, -53}),
            /*ejj*/ decryptMsg(new byte[]{-26, 122, -44, 105, -82, -66, -79, -68, 71, -112, 46, 49, 105, -69, 51, -42})
    ); // dnj // TODO: DONE
    public static final HookClass GROUP_SNAP_METADATA = new HookClass(
            /*GROUP_SNAP_METADATA*/ decryptMsg(new byte[]{-93, 86, -68, -65, 80, -110, 9, -109, -112, -32, -8, -33, -22, 2, 101, 54, -60, -33, -90, 10, -76, -27, 116, 126, 59, 64, 122, 42, -102, -87, -91, 29}),
            /*mai*/ decryptMsg(new byte[]{42, -101, 123, 0, 127, 83, 81, 103, 8, 88, -1, 83, -38, -36, -98, 39})
    ); // kiz // TODO: DONE - Changed a bit
    public static final HookClass STORY_SNAP = new HookClass(
            /*STORY_SNAP*/ decryptMsg(new byte[]{-17, -14, -86, -109, 88, 89, -125, -120, -20, -125, 92, -105, -4, 114, 9, 9}),
            /*qta*/ decryptMsg(new byte[]{91, 65, -73, -68, -10, 6, -93, 67, -86, 83, 94, 103, 62, 27, 122, -7})
    ); // oud // TODO: DONE
    public static final HookClass STORY_STATUS_UPDATER = new HookClass(
            /*STORY_STATUS_UPDATER*/ decryptMsg(new byte[]{71, -4, -108, 44, 80, 3, 82, -13, 32, 6, 5, 99, -84, -83, -10, -72, -119, -84, 95, -27, 15, 31, 80, 17, 45, 13, 64, -22, 42, -8, -86, 39}),
            /*ejw*/ decryptMsg(new byte[]{116, -59, -66, 9, 70, -48, -15, -64, 54, 91, 15, -73, -55, -92, 81, 23})
    ); // dnu // TODO: DONE
    public static final HookClass STORY_METADATA_LOADER = new HookClass(
            /*STORY_METADATA_LOADER*/ decryptMsg(new byte[]{-111, 7, -41, 99, -50, -68, -128, -39, -99, 127, -55, -29, 87, -70, -110, -35, -124, -19, 39, -76, -121, -73, -17, -112, -23, 0, -28, -64, -120, -33, 1, -18}),
            /*eur*/ decryptMsg(new byte[]{88, -87, 112, -36, 97, 1, 6, 30, 39, 88, -111, -57, 24, 89, 0, -17})
    ); // dye // TODO: DONE
    public static final HookClass STORY_ADVANCER = new HookClass(
            /*STORY_ADVANCER*/ decryptMsg(new byte[]{105, 20, -16, 104, -75, -123, -26, 14, -13, 118, -89, -56, -39, 11, -11, -55}),
            /*buf*/ decryptMsg(new byte[]{-23, -69, -52, 55, -116, 92, -12, 123, -114, -118, 62, 114, 41, 92, 56, -84})
    ); // tka // TODO: DONE
    public static final HookClass SENT_IMAGE = new HookClass(
            /*SENT_IMAGE*/ decryptMsg(new byte[]{-78, 70, 29, 103, -80, -128, -43, 118, 127, 78, 114, 32, -54, 89, 94, 25}),
            /*rbp*/ decryptMsg(new byte[]{55, 80, -7, -116, -37, 81, -49, -90, 115, -54, -52, -72, 20, 19, 11, 126})
    ); // pcf // TODO: DONE
    public static final HookClass SENT_VIDEO = new HookClass(
            /*SENT_VIDEO*/ decryptMsg(new byte[]{-1, 86, -110, -121, -59, -88, 1, -34, -123, 1, -96, 57, 5, 54, -57, -108}),
            /*rcf*/ decryptMsg(new byte[]{117, -68, 51, -74, 97, -114, 34, -82, -41, -83, 79, 32, -59, -51, 97, 35})
    ); // pct // TODO: DONE
    public static final HookClass SENT_BATCHED_VIDEO = new HookClass(
            /*SENT_BATCHED_VIDEO*/ decryptMsg(new byte[]{10, -125, 35, -42, 35, -31, 107, -43, -60, -10, -51, -44, -105, -90, 73, -91, 100, -80, 122, 4, -58, -32, 92, -127, -52, 103, -100, 122, 106, -24, 116, -83}),
            /*fql*/ decryptMsg(new byte[]{-60, -36, -10, 83, 9, -123, -123, 118, -13, -10, -32, -108, 29, 43, 41, -75})
    ); // eoo // TODO: DONE
    public static final HookClass ENUM_BATCHED_SNAP_POSITION = new HookClass(
            /*ENUM_BATCHED_SNAP_POSITION*/ decryptMsg(new byte[]{53, 0, -87, 36, 13, -57, 121, -88, 74, -94, 8, 9, -20, 110, 61, -118, -83, -35, 46, -14, 115, -124, -28, 99, 24, 21, 57, -109, 49, -34, -66, -6}),
            /*ddf*/ decryptMsg(new byte[]{101, -13, -41, -108, -60, -102, 36, 33, -101, -85, -121, -45, 62, 116, 79, 107})
    ); // cjh // TODO: DONE
    public static final HookClass SENT_SNAP_BASE = new HookClass(
            /*SENT_SNAP_BASE*/ decryptMsg(new byte[]{91, 57, 89, 39, -104, -90, -79, 125, 124, 91, 102, -52, 15, 41, 87, -67}),
            /*rat*/ decryptMsg(new byte[]{35, 111, 96, -83, -105, 76, -128, -99, 12, -99, 39, 53, -49, -87, -65, 54})
    ); // pbk // TODO: DONE
    public static final HookClass META_DATA_BUILDER = new HookClass(
            /*META_DATA_BUILDER*/ decryptMsg(new byte[]{-59, 47, 9, -52, -94, -66, 72, 110, -27, 119, -4, -56, 97, -93, 73, 3, -37, 44, 36, -91, -79, -96, 61, 47, -119, 89, 86, 113, 117, 99, 31, 29}),
            /*ncg*/ decryptMsg(new byte[]{39, 41, -42, 126, -73, 45, -76, 27, -18, -63, -15, 7, 6, -61, -101, 79})
    ); // ljn // TODO: DONE
    public static final HookClass CHAT_IMAGE_METADATA = new HookClass(
            /*CHAT_IMAGE_METADATA*/ decryptMsg(new byte[]{74, -55, -51, 15, -94, -114, -15, 69, 48, -66, -59, -43, -87, 64, -53, 71, -60, -33, -90, 10, -76, -27, 116, 126, 59, 64, 122, 42, -102, -87, -91, 29}),
            /*lzv*/ decryptMsg(new byte[]{-58, 110, 30, 99, -114, 40, -109, 50, -73, 122, 0, 51, 2, -72, -54, 2})
    ); // kin // TODO: DONE
    public static final HookClass CHAT_VIDEO_METADATA = new HookClass(
            /*CHAT_VIDEO_METADATA*/ decryptMsg(new byte[]{119, -7, -48, -4, 58, 86, 87, 96, -33, 116, 12, -9, -40, 126, -103, -63, -60, -33, -90, 10, -76, -27, 116, 126, 59, 64, 122, 42, -102, -87, -91, 29}),
            /*msp*/ decryptMsg(new byte[]{-54, 76, -46, 36, -128, 17, -44, 78, 55, -46, -102, 90, -115, -100, 22, -41})
    ); // lam // TODO: DONE
    public static final HookClass CHAT_VIDEO = new HookClass(
            /*CHAT_VIDEO*/ decryptMsg(new byte[]{-94, -120, 13, 64, 102, 117, -72, -114, 9, -11, -90, 123, -96, -44, 28, 41}),
            /*mmc*/ decryptMsg(new byte[]{-7, -90, 85, 56, 36, 43, -52, -88, 13, 111, -19, 82, 82, -5, 104, -75})
    ); // kun // TODO: DONE
    public static final HookClass NEW_CONCENTRIC_TIMERVIEW = new HookClass(
            /*NEW_CONCENTRIC_TIMERVIEW*/ decryptMsg(new byte[]{87, 17, -48, -51, 1, -1, 62, 36, 56, -105, 22, 74, 105, 24, -109, -28, -16, -83, 50, -14, -19, 25, 109, -63, 116, 9, 110, 26, 41, -92, -13, -99}),
            /*com.snapchat.android.framework.ui.views.NewConcentricTimerView*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -68, -48, 5, -89, -122, 21, -35, -19, 112, -97, -80, 106, -15, -16, 82, 76, -116, 92, 45, 124, -14, -4, -57, 73, 36, -55, -79, 22, 19, 37, 31, 114, 40, 26, 76, 46, -69, 31, -8, -63, 96, 125, -87, 76, 46, 54, -57, -74})
    ); // com.snapchat.android.framework.ui.views.NewConcentricTimerView // TODO: DONE
    public static final HookClass COUNTDOWNTIMER_VIEW = new HookClass(
            /*COUNTDOWNTIMER_VIEW*/ decryptMsg(new byte[]{-34, 96, -61, -20, 15, -126, 1, 32, 15, 117, -43, 95, -82, 96, 26, -91, -15, -54, -9, -31, 119, -2, -31, 47, -79, -103, -16, 50, -38, -32, 80, -120}),
            /*com.snapchat.android.framework.ui.views.CountdownTimerView*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -68, -48, 5, -89, -122, 21, -35, -19, 112, -97, -80, 106, -15, -16, 82, 76, -15, -69, -2, 46, 73, -25, 98, 122, -64, 15, 85, 10, 34, -99, -41, -88, 107, 47, -53, -28, 95, -54, -100, -38, 74, 7, -10, 18, 96, -105, 62, -125})
    ); // com.snapchat.android.framework.ui.views.CountdownTimerView* // TODO: DONE
    public static final HookClass LENS_AUTHENTICATION = new HookClass(
            /*LENS_AUTHENTICATION*/ decryptMsg(new byte[]{-2, -122, 36, -116, 52, -70, 55, 89, -6, -76, -101, 17, 47, 17, 59, 81, 90, 124, 9, -127, 53, 120, -19, -36, 78, 29, -75, -73, -3, 19, 51, -113}),
            /*jij*/ decryptMsg(new byte[]{31, -58, 88, -48, -13, -115, 101, 54, 122, -69, -1, -70, -43, -6, 60, -97})
    ); // hvm // TODO: DONE
    public static final HookClass ENUM_SNAP_ADVANCE_MODES = new HookClass(
            /*ENUM_SNAP_ADVANCE_MODES*/ decryptMsg(new byte[]{-109, 58, 26, -106, 62, -10, -114, -107, 25, 103, -68, -29, 105, -95, -13, 104, -111, 106, -122, 32, 95, -33, 21, 102, 84, -107, 103, -63, 4, 117, 40, -44}),
            /*cad*/ decryptMsg(new byte[]{98, -33, 106, -90, -49, 87, 112, -117, -79, -81, -125, -114, -35, -68, -46, -34})
    ); // tpt // TODO: DONE
    public static final HookClass LENS = new HookClass(
            /*LENS*/ decryptMsg(new byte[]{84, 42, -89, 49, 104, 39, -23, -53, -4, 38, -58, -121, -65, 27, -62, 42}),
            /*jkk*/ decryptMsg(new byte[]{91, 26, 52, -23, 92, 92, 100, 55, -11, -69, 81, -6, 84, -1, 104, 18})
    ); // hxb // TODO: DONE
    public static final HookClass LENS_CATEGORY_RESOLVER = new HookClass(
            /*LENS_CATEGORY_RESOLVER*/ decryptMsg(new byte[]{87, 106, 79, 25, -49, -69, 37, 62, 4, 117, 26, -42, 109, 40, -93, 97, -89, -86, -52, -127, -63, -26, 100, 40, 95, -110, 17, 70, -12, -83, 115, 57}),
            /*jkr*/ decryptMsg(new byte[]{-102, 41, 16, 13, 38, -46, -44, -75, 39, -63, 108, 77, -23, 1, 55, 44})
    ); // hxh // TODO: DONE
    public static final HookClass LENS_LOADER = new HookClass(
            /*LENS_LOADER*/ decryptMsg(new byte[]{113, 68, 96, -12, 126, 102, 102, -122, 112, 84, -95, -97, 88, 116, -97, -112}),
            /*jhg*/ decryptMsg(new byte[]{55, 10, 91, -70, 98, 44, -44, -109, -124, 1, 74, -105, -24, -115, 100, 45})
    ); // hum // TODO: DONE
    public static final HookClass LENS_CONTEXT_HOLDER = new HookClass(
            /*LENS_CONTEXT_HOLDER*/ decryptMsg(new byte[]{-118, -67, 81, 93, 36, -108, 40, 79, 49, -2, 7, 59, 11, 11, 51, 6, -18, -43, 12, 84, 35, 42, 32, -78, 71, -96, -65, 116, 116, 96, -82, 119}),
            /*jkv*/ decryptMsg(new byte[]{40, -7, 31, -34, 117, -33, -92, -20, -35, -71, -84, 90, -57, 69, 11, 27})
    ); // hum // TODO: DONE
    public static final HookClass LENS_CAMERA_CONTEXT_ENUM = new HookClass(
            /*LENS_CAMERA_CONTEXT_ENUM*/ decryptMsg(new byte[]{-109, 27, -72, -49, -12, -71, -90, -95, -89, 80, -7, 84, -9, 126, -11, 7, -51, 101, -79, -51, 101, 118, -32, 39, 66, -33, 124, -81, -1, 85, 105, -111}),
            /*jko*/ decryptMsg(new byte[]{-69, 66, 122, -100, -39, -91, -55, -52, 86, 10, 31, 63, 35, -106, 38, -1})
    ); // hum // TODO: DONE
    public static final HookClass LENS_APPLICATION_CONTEXT_ENUM = new HookClass(
            /*LENS_APPLICATION_CONTEXT_ENUM*/ decryptMsg(new byte[]{-59, 122, -127, 8, 59, -117, 30, -72, 92, -72, 117, 66, 109, -116, 97, -103, -88, 72, 27, -106, -92, 64, -9, -69, 45, 105, 58, 111, 122, 99, 27, -49}),
            /*jkl*/ decryptMsg(new byte[]{9, 58, -91, -20, -60, 106, 7, 99, 67, -117, -97, -42, 31, -83, -85, -16})
    ); // hum // TODO: DONE
    public static final HookClass STORY_LOADER = new HookClass(
            /*STORY_LOADER*/ decryptMsg(new byte[]{-45, -109, -60, 5, 51, 10, -64, -26, -25, 3, -40, 48, 114, 4, 11, 21}),
            /*ees*/ decryptMsg(new byte[]{-112, -41, 13, -117, 96, 101, 114, -84, 17, -67, -45, -28, -93, 31, -42, -9})
    ); // dir // TODO: DONE
    public static final HookClass STORY_SPONSORED = new HookClass(
            /*STORY_ADVERT*/ decryptMsg(new byte[]{-86, -31, 62, -48, 43, 71, 32, -1, 89, -6, 93, -122, -121, 120, 100, -118}),
            /*esr*/ decryptMsg(new byte[]{-111, -33, 1, 118, 126, -85, -42, -122, -85, 76, 28, -3, 79, 116, 76, -114})
    ); // dwm // TODO: DONE
    public static final HookClass STORY_FRIEND_RECENT = new HookClass(
            /*STORY_FRIEND_RECENT*/ decryptMsg(new byte[]{-6, 50, -27, -84, -97, 87, 57, -82, 126, -76, 45, -32, -121, -7, -82, 62, -72, -77, 57, 70, 52, 94, 36, -73, -112, 18, 42, -123, 42, 85, 21, 61}),
            /*erc*/ decryptMsg(new byte[]{1, 94, 125, -73, -66, -95, -103, 16, -99, 16, -71, 100, -6, 122, -25, -25})
    ); // dux // TODO: DONE
    public static final HookClass STORY_FRIEND_VIEWED = new HookClass(
            /*STORY_FRIEND_VIEWED*/ decryptMsg(new byte[]{-26, 35, 4, -109, 107, 78, -46, -52, 94, 107, 52, -96, -89, -15, 103, -103, -6, -93, -13, 9, 20, 20, -74, 99, 118, 119, 75, 2, -26, -111, -91, 28}),
            /*eqj*/ decryptMsg(new byte[]{-72, 86, -77, -91, -21, -78, -122, -82, 116, 15, -122, -82, -29, 59, 101, -69})
    ); // dud // TODO: DONE
    public static final HookClass STORY_SNAP_AD_LOADER = new HookClass(
            /*STORY_SNAP_AD_LOADER*/ decryptMsg(new byte[]{-50, 46, -111, -88, -75, 5, 7, -18, -12, -40, 66, 57, 103, 122, -28, 10, 123, 16, 48, 90, 21, 108, 105, -27, 114, 87, -115, 55, 113, -30, -108, -15}),
            /*ehu*/ decryptMsg(new byte[]{-70, -41, 40, -88, -104, 114, 63, -71, 44, -78, 78, -87, -12, 22, 119, 98})
    ); // dlv // TODO: DONE
    public static final HookClass FRIEND_PROFILE_POPUP_FRAGMENT = new HookClass(
            /*FRIEND_PROFILE_POPUP_FRAGMENT*/ decryptMsg(new byte[]{59, -52, -36, -28, 33, 44, -117, -10, 73, -65, -47, -69, 17, -5, 116, 74, -24, 85, -109, 8, 114, -103, -78, -31, -38, 115, -38, -73, 12, 127, 59, -18}),
            /*com.snapchat.android.app.feature.miniprofile.internal.friend.FriendMiniProfilePopupFragment*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -42, 19, 50, -101, -37, 44, 18, -49, -107, -26, 49, 14, -123, -33, -97, -79, 74, 109, 0, -84, -58, -99, 55, 122, -45, -43, 95, 84, 5, 82, 13, 46, -30, 114, -125, 35, 115, 14, 51, 49, 90, 10, 117, -67, -120, 46, 83, 5, 85, 51, -112, -86, -127, 6, -63, 38, -98, 97, -120, 77, -45, 54, 108, 89, 91, 88, 47, -83, -127, 113, -127, 38, -1, -89, 85, 29, 34, -124, 17, 101})
    ); // com.snapchat.android.app.feature.miniprofile.internal.friend.FriendMiniProfilePopupFragment // TODO: DONE
    public static final HookClass USER_PREFS = new HookClass(
            /*USER_PREFS*/ decryptMsg(new byte[]{20, 86, -128, 116, -108, -62, -85, 89, 47, -98, -40, -48, 20, 3, -97, -25}),
            /*sww*/ decryptMsg(new byte[]{87, 40, -71, 60, 6, 33, 79, -123, -26, -66, 8, 22, 79, -85, -2, -122})
    ); // com.snapchat.android.core.user.UserPrefs // TODO: DONE
    public static final HookClass CAMERA_FRAGMENT = new HookClass(
            /*CAMERA_FRAGMENT*/ decryptMsg(new byte[]{-103, 61, -71, -120, -10, -53, -21, -116, -95, -37, 65, -15, -56, -85, 56, -86}),
            /*com.snapchat.android.app.main.camera.CameraFragmentV1*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -8, 84, 127, -51, -35, -37, 127, 73, -89, 15, 19, 40, 39, -96, 21, 54, 117, -53, 104, -89, -63, -51, -76, 7, -125, -81, 49, -66, 57, 15, 96, 67, 106, -5, -58, -72, -7, -127, 122, -30, -110, -65, 88, -9, 118, 117, 101, -41})
    ); // com.snapchat.android.app.main.camera.CameraFragmentV1 // TODO: DONE
    public static final HookClass ENUM_LENS_TYPE = new HookClass(
            /*ENUM_LENS_TYPE*/ decryptMsg(new byte[]{44, 38, -51, -116, -83, -43, 19, -40, -105, -78, -23, -107, 80, 101, 41, -50}),
            /*jkk$b*/ decryptMsg(new byte[]{-126, -20, -85, 103, 84, -108, -50, -25, -6, -29, -24, -66, 87, -80, -96, 15})
    ); // hxb$b // TODO: DONE
    public static final HookClass LENS_CATEGORY = new HookClass(
            /*LENS_CATEGORY*/ decryptMsg(new byte[]{106, 40, 31, 69, 15, -45, 46, -40, 18, 101, -7, 91, -47, 78, -46, -124}),
            /*jkt*/ decryptMsg(new byte[]{-9, 3, 100, -43, -61, 20, 4, 81, -4, 74, 86, 104, -9, 82, -40, -82})
    ); // hxj // TODO: DONE
    public static final HookClass LENS_SLUG = new HookClass(
            /*LENS_SLUG*/ decryptMsg(new byte[]{-28, 22, -51, -77, -123, -86, 59, -54, -28, -118, -75, -113, -50, 34, 92, -35}),
            /*xet*/ decryptMsg(new byte[]{55, -112, 95, 18, -56, -7, 28, -83, -95, 118, 79, -32, -13, 54, 124, 107})
    ); // vdi // TODO: DONE
    public static final HookClass LENS_TRACK = new HookClass(
            /*LENS_TRACK*/ decryptMsg(new byte[]{-80, 53, 33, 58, -63, -53, -27, -24, -119, -53, -98, 31, 13, -117, -52, -116}),
            /*xjk*/ decryptMsg(new byte[]{3, 120, 55, -122, 1, 56, -91, -98, 56, -128, 79, -110, -105, 111, -104, 1})
    ); // vha // TODO: DONE
    //	public static final HookClass LENS_ASSET = new HookClass(
//			/*LENS_ASSET*/ decryptMsg(new byte[]{103, -30, 45, 86, 2, -11, -90, -118, -30, -38, 18, -82, -100, 100, 81, -9}),
//			/*qvy*/ decryptMsg(new byte[]{59, -101, -61, 47, 114, 126, 100, -69, -33, 38, -46, 109, -46, 5, -107, 12})
//	);
    public static final HookClass LENS_ASSET_BUILT = new HookClass(
            /*LENS_ASSET_BUILT*/ decryptMsg(new byte[]{-45, -43, -18, 76, -1, 48, 54, -122, 37, -128, 10, -37, 31, -22, -106, -83, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            /*jkm*/ decryptMsg(new byte[]{27, -2, 27, 66, 27, -124, 48, 65, -29, -64, 46, 118, 125, -96, 19, -32})
    ); // hxd // TODO: DONE
    public static final HookClass LENS_ASSET_TYPE = new HookClass(
            /*LENS_ASSET_TYPE*/ decryptMsg(new byte[]{110, 33, -86, -45, -113, 3, 79, 127, 101, 90, -109, -73, 121, -122, 122, -6}),
            /*jkm$a*/ decryptMsg(new byte[]{40, 122, -87, -55, -119, 77, 58, 125, 80, -7, 105, 50, 89, 112, 77, 27})
    ); // hxd$a // TODO: DONE
    public static final HookClass LENS_ASSET_LOAD_MODE = new HookClass(
            /*LENS_ASSET_LOAD_MODE*/ decryptMsg(new byte[]{-93, 111, 1, -111, -79, 106, 96, -79, 92, -75, -71, -107, -102, -81, 59, -50, -16, 117, -15, 77, -73, -67, -116, -15, 88, 36, -63, -19, -33, -101, 23, 11}),
            /*jlb*/ decryptMsg(new byte[]{38, -14, -106, 7, -85, -59, -40, 82, -54, -16, 36, -57, -37, 113, 31, -40})
    ); // hxq // TODO: DONE
    public static final HookClass ENUM_LENS_ACTIVATOR_TYPE = new HookClass(
            /*ENUM_LENS_ACTIVATOR_TYPE*/ decryptMsg(new byte[]{-99, 118, -2, -54, -32, 122, -109, -76, 122, 42, -62, -4, -71, 32, -90, -113, 74, -29, -81, 81, 48, 91, -104, 18, 6, -9, -98, -37, -8, -6, 117, -124}),
            /*com.looksery.sdk.domain.Category.ActivatorType*/ decryptMsg(new byte[]{71, -101, 101, 27, 20, -56, 122, 30, 43, 90, -67, -91, -12, -75, 106, -90, 89, 60, -79, 45, -100, -36, -98, -33, -62, -9, 65, -100, 109, -98, -117, -112, -69, 103, -15, 100, 85, -87, 36, -67, -98, 102, 127, 31, -126, -25, 108, -80})
    ); // com.looksery.sdk.domain.Category.ActivatorType // TODO: DONE
    public static final HookClass FONT_CLASS = new HookClass(
            /*FONT_CLASS*/ decryptMsg(new byte[]{-65, -67, -63, -79, 70, 113, 106, -8, -1, -46, 30, -2, 21, 46, 40, 28}),
            /*android.graphics.Typeface*/ decryptMsg(new byte[]{118, 60, 26, 66, -105, -108, -74, -67, 48, 82, -14, -127, 65, -112, -60, -81, 26, 45, 102, -102, -67, 39, -84, 50, -126, 115, -66, 34, -103, -46, -49, -89})
    ); // android.graphics.Typeface // TODO: DONE
    public static final HookClass SNAPCHAT_CAPTION_VIEW_CLASS = new HookClass(
            /*SNAPCHAT_CAPTION_VIEW_CLASS*/ decryptMsg(new byte[]{-6, -105, -105, -84, 18, 0, 117, -13, 14, -54, -31, -80, -95, 26, 123, -37, 15, -2, 27, -34, 16, -65, 20, 104, 49, -83, 23, -84, -7, 5, 70, -8}),
            /*com.snapchat.android.app.feature.creativetools.caption.SnapCaptionView*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -42, 19, 50, -101, -37, 44, 18, -49, -107, -26, 49, 14, -123, -33, -97, -79, 77, 39, 98, 44, -46, -76, 74, -107, 94, -117, 41, 101, -17, -68, -38, 99, -20, -11, -43, -56, -13, -21, -14, 18, 65, -49, 36, 25, -93, -60, -112, -31, 120, 57, 55, -54, -86, 106, -118, -71, -16, -8, 21, -53, 61, -82, -31, 28})
    ); // com.snapchat.android.app.feature.creativetools.caption.SnapCaptionView // TODO: DONE
    public static final HookClass CAPTION_MANAGER_CLASS = new HookClass(
            /*CAPTION_MANAGER_CLASS*/ decryptMsg(new byte[]{77, 81, -95, 116, -94, 14, -14, -126, 108, 98, -119, 102, -112, 81, -55, -28, 48, 76, 105, 30, 120, 31, -85, 9, 1, -18, -79, -34, -43, 101, -89, 119}),
            /*gau*/ decryptMsg(new byte[]{99, -13, 41, -78, -109, -44, -109, 118, 126, 60, 71, -89, 103, 109, -15, -87})
    ); // eyr // TODO: DONE
    public static final HookClass CHAT_MESSAGE_VIEW_HOLDER = new HookClass(
            /*CHAT_MESSAGE_VIEW_HOLDER*/ decryptMsg(new byte[]{-12, 90, 80, -45, 77, -126, -20, -83, -116, -112, 116, 116, -78, -98, 60, 126, 112, -11, 113, -66, 32, 88, 112, -10, -90, -81, 103, -71, -14, -23, -75, -107}),
            /*mje*/ decryptMsg(new byte[]{-29, 85, -127, 79, -102, 22, 61, -109, -69, 82, 44, -92, -123, -16, -64, -65})
    ); // krr // TODO: DONE
    public static final HookClass OPERA_PAGE_VIEW = new HookClass(
            /*OPERA_PAGE_VIEW*/ decryptMsg(new byte[]{-19, -47, 39, -74, -103, 41, -58, 23, -46, 79, -58, 13, -39, -108, 54, 125}),
            /*com.snap.opera.view.OperaPageView*/ decryptMsg(new byte[]{83, -28, -105, -102, 93, -92, -117, 30, -43, -12, -24, 101, 20, -29, 26, 45, 30, -111, 45, 41, 121, 55, 123, 62, -38, 26, -65, 85, -115, -57, -76, -21, -12, 33, -64, 126, 27, 24, 73, 88, -27, 44, 75, 23, -48, 46, -115, 103})
    ); // com.snapchat.opera.view.OperaPageView // TODO: DONE
    public static final HookClass CHAT_METADATA_JSON_PARSER = new HookClass(
            /*CHAT_METADATA_JSON_PARSER*/ decryptMsg(new byte[]{-68, -114, 1, -65, -66, -102, -82, -26, -15, 89, -95, 121, 102, 112, 80, -68, -114, 84, 105, 83, -92, -2, 63, -12, -51, -53, 12, 78, 63, -79, -20, 76}),
            /*wjo*/ decryptMsg(new byte[]{-86, 1, -124, -75, -47, -117, 107, -83, 65, 64, -79, 122, 7, -50, -42, -58})
    ); // ulu // TODO: DONE
    public static final HookClass CHAT_METADATA_JSON_PARSER_SECOND = new HookClass(
            /*CHAT_METADATA_JSON_PARSER_SECOND*/ decryptMsg(new byte[]{-68, -114, 1, -65, -66, -102, -82, -26, -15, 89, -95, 121, 102, 112, 80, -68, -29, -69, -111, -80, -86, -64, 60, 55, -100, -83, -47, -40, -41, -107, -67, -76, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            /*wxx*/ decryptMsg(new byte[]{-116, 51, 52, 25, 39, 39, -116, 72, 54, 27, -91, 82, 43, 9, 33, -98})
    ); // uxi // TODO: DONE
    public static final HookClass CHAT_METADATA = new HookClass(
            /*CHAT_METADATA*/ decryptMsg(new byte[]{100, 49, 8, -114, -126, -62, -6, 62, -110, 59, 6, 89, -13, -20, 32, 54}),
            /*wjn*/ decryptMsg(new byte[]{-90, -97, -123, -56, 108, -101, 8, 18, -81, 15, -16, 38, -78, -92, -27, 48})
    ); // ult // TODO: DONE
    public static final HookClass CHAT_HEADER_METADATA = new HookClass(
            /*CHAT_HEADER_METADATA*/ decryptMsg(new byte[]{4, -86, -7, -48, -116, -103, 118, 32, -22, -64, 19, -91, -8, 39, -26, 81, 92, 65, 125, 123, -4, -42, -49, -8, 73, 110, 1, -77, 18, -55, 111, 95}),
            /*wso*/ decryptMsg(new byte[]{-50, -103, 23, 92, -18, 58, 1, 90, 40, 78, -97, -63, 74, -8, 120, -16})
    ); // usz // TODO: DONE
    public static final HookClass CHAT_BODY_METADATA = new HookClass(
            /*CHAT_BODY_METADATA*/ decryptMsg(new byte[]{-72, 87, -108, 19, 27, 44, -128, 89, -6, -32, 115, -14, -95, 58, 42, -40, 81, 87, -114, -11, -36, 97, -20, 91, -29, 4, 54, -41, -72, -37, 17, -57}),
            /*wxa*/ decryptMsg(new byte[]{37, 41, -46, 28, 4, -12, 17, 42, 16, 83, -98, -124, -46, 49, 92, 124})
    ); // uwu // TODO: DONE
    public static final HookClass GEOFILTER_VIEW = new HookClass(
            /*GEOFILTER_VIEW*/ decryptMsg(new byte[]{88, -87, 2, 67, -2, -87, 22, 28, -94, 33, -40, 3, -48, -35, 118, -40}),
            /*com.snapchat.android.app.shared.feature.preview.ui.smartfilters.GeofilterView*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, 106, 13, -101, -117, 116, 90, -40, 0, 123, 78, 104, -88, -61, -68, 88, -38, -47, -108, -108, -68, -83, -50, 103, -38, 111, -85, -48, -96, -126, 89, -63, -46, -121, 106, -3, -55, -48, 126, 53, 47, -65, 96, -12, -53, 69, -27, -122, -27, 116, 92, -46, 78, 38, -16, 65, -94, 82, 102, 87, -5, 84, -96, -94, -108})
    ); // com.snapchat.android.app.shared.feature.preview.ui.smartfilters.GeofilterView // TODO: DONE
    public static final HookClass IMAGE_GEOFILTER_VIEW = new HookClass(
            /*IMAGE_GEOFILTER_VIEW*/ decryptMsg(new byte[]{-94, -57, -76, -37, -9, 5, -45, -109, 21, -80, 23, 127, 24, -69, -103, 51, -119, 22, -103, -67, -92, 91, 125, 94, 14, 99, 78, -67, 95, -89, -110, 115}),
            /*com.snapchat.android.app.shared.feature.preview.ui.smartfilters.ImageGeofilterView*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, 106, 13, -101, -117, 116, 90, -40, 0, 123, 78, 104, -88, -61, -68, 88, -38, -47, -108, -108, -68, -83, -50, 103, -38, 111, -85, -48, -96, -126, 89, -63, -46, -121, 106, -3, -55, -48, 126, 53, 47, -65, 96, -12, -53, 69, -27, -122, -27, -10, 40, -78, -51, -4, 32, -100, 95, -83, 98, -22, 0, -126, -116, -27, 123, 76, 18, -122, 16, 89, -15, 117, -26, 54, 90, -113, -46, 39, -103, 58, 67})
    ); // com.snapchat.android.app.shared.feature.preview.ui.smartfilters.ImageGeofilterView // TODO: DONE
    public static final HookClass FILTER_METADATA = new HookClass(
            /*FILTER_METADATA*/ decryptMsg(new byte[]{-17, -5, -106, -47, 74, 47, 3, -85, 45, 32, -71, -128, -20, -118, -92, -58}),
            /*quj*/ decryptMsg(new byte[]{24, 114, -6, 66, 46, 103, -91, -82, -4, -63, 39, 23, -66, -83, -18, -16})
    ); // ovk // TODO: DONE
    public static final HookClass SERIALIZABLE_FILTER_METADATA = new HookClass(
            /*SERIALIZABLE_FILTER_METADATA*/ decryptMsg(new byte[]{8, -98, 93, -86, 2, 95, -95, -41, 55, 7, 84, 42, -124, 4, -84, -36, -108, 36, 6, 98, 81, -36, 24, 35, -51, 81, 127, 59, -128, 63, -28, -93}),
            /*wsa*/ decryptMsg(new byte[]{-14, 54, 42, -123, -98, -12, -53, 118, -78, 114, 107, 44, -124, 74, -30, -49})
    ); // usn // TODO: DONE
    public static final HookClass FILTER_METADATA_LOADER = new HookClass(
            /*FILTER_METADATA_LOADER*/ decryptMsg(new byte[]{54, -48, 117, 63, -48, 81, -62, -2, -78, 1, -30, -50, -119, 94, 43, -44, 48, -51, -81, 124, 49, -113, 57, -65, -60, -48, 26, 31, -106, -9, -86, 10}),
            /*ghu*/ decryptMsg(new byte[]{11, 64, -109, -62, -109, 79, -33, 64, -71, 86, 100, -54, 37, 113, -107, 59})
    ); // fek // TODO: DONE
    public static final HookClass FILTER_METADATA_CREATOR = new HookClass(
            /*FILTER_METADATA_CREATOR*/ decryptMsg(new byte[]{54, -48, 117, 63, -48, 81, -62, -2, -78, 1, -30, -50, -119, 94, 43, -44, -29, -83, 110, -29, 104, 33, -61, -9, 106, -15, -90, -65, -80, 27, -47, 111}),
            /*qvs$2*/ decryptMsg(new byte[]{-50, 6, -66, 90, -27, 93, 76, -53, -108, -18, 56, 75, 50, 102, -87, -93})
    ); // owp$2 // TODO: DONE
    public static final HookClass GEOFILTER_VIEW_CREATOR = new HookClass(
            /*GEOFILTER_VIEW_CREATOR*/ decryptMsg(new byte[]{32, 125, 21, -111, -61, -13, 26, -48, 54, 123, 105, 86, 24, -121, -34, -108, -8, 100, -81, -17, 39, 29, 80, -17, -3, -122, -6, -88, -103, 31, -32, -43}),
            /*ghb*/ decryptMsg(new byte[]{68, 68, -14, 102, 28, -20, 55, -21, -63, -113, -88, -18, -35, -90, 106, 112})
    ); // fdr // TODO: DONE
    public static final HookClass CHAT_NOTIFICATION_CREATOR = new HookClass(
            /*CHAT_NOTIFICATION_CREATOR*/ decryptMsg(new byte[]{-55, 31, -60, 93, 69, -20, 14, -13, -110, -101, 29, 30, 21, -87, 39, 72, -101, -4, 91, -119, 113, -123, -28, 86, 119, 35, -61, 43, 80, 65, -113, -69}),
            /*lmq*/ decryptMsg(new byte[]{115, 67, 26, -33, 83, 34, -45, -101, -94, -38, 90, 78, -46, 39, 71, 11})
    ); // jvy // TODO: DONE
    public static final HookClass NETWORK_MANAGER = new HookClass(
            /*NETWORK_MANAGER*/ decryptMsg(new byte[]{-71, -94, -40, 86, 62, -76, 75, -14, 37, 123, -25, 34, 0, -5, 75, -64}),
            /*sjv*/ decryptMsg(new byte[]{-14, -85, -53, -65, 120, -11, -99, 80, 66, -106, -40, -85, 111, -103, 17, -124})
    ); // qlc // TODO: DONE
    public static final HookClass NETWORK_DISPATCHER = new HookClass(
            /*NETWORK_DISPATCHER*/ decryptMsg(new byte[]{94, 31, -100, 59, -112, 116, 125, 61, -117, 59, 124, -49, -117, -47, -66, -10, 98, 54, -70, 82, -48, -51, 86, -1, 23, 51, 120, -94, 4, -75, 98, -27}),
            /*lpo*/ decryptMsg(new byte[]{-75, -90, -82, 114, -35, 120, -45, 51, -72, -49, 33, -107, 33, -127, 50, -38})
    ); // jyp // TODO: DONE
    //	public static final HookClass CHAT_MESSAGE_METADATA = new HookClass(
//			/*CHAT_MESSAGE_METADATA*/ decryptMsg(new byte[]{-50, 44, 120, -82, 113, -65, 81, -120, 5, -126, -76, -93, 71, -33, -116, -17, 35, 108, 78, -36, 77, -82, 21, 99, -117, 72, 87, 32, 49, 74, 108, -30}),
//			/*ktw*/ decryptMsg(new byte[]{123, 84, -31, -44, -43, 3, -88, 68, 30, 56, -104, 107, 47, -128, 23, 71})
//	);
    public static final HookClass CHAT_GROUP_VIEW_MARKER = new HookClass(
            /*CHAT_GROUP_VIEW_MARKER*/ decryptMsg(new byte[]{-107, -46, -82, 103, -84, -45, -43, -55, 63, 27, 14, 61, -47, 56, 71, -60, 78, -1, 43, -7, -42, -120, -37, -52, -99, 4, -83, -27, 64, 93, -68, 77}),
            /*lrk*/ decryptMsg(new byte[]{-126, 49, 59, -92, 9, 108, -53, -5, 107, -106, -55, -113, 62, -44, -77, -29})
    ); // kal // TODO: DONE
    public static final HookClass CHAT_DIRECT_VIEW_MARKER = new HookClass(
            /*CHAT_DIRECT_VIEW_MARKER*/ decryptMsg(new byte[]{-96, 27, -6, -112, -115, -88, -5, -72, -69, -47, -38, -111, 1, 65, 39, -98, 56, 49, -54, 49, 69, -111, 120, 72, -31, 36, -12, -51, -109, 48, -22, -54}),
            /*lqg*/ decryptMsg(new byte[]{-114, 106, -89, -20, -56, 54, -85, -69, -43, -4, 125, 85, -112, 41, -109, -108})
    ); // jzh // TODO: DONE
    public static final HookClass CHAT_V3_FRAGMENT = new HookClass(
            /*CHAT_V3_FRAGMENT*/ decryptMsg(new byte[]{60, 22, -104, 60, 102, 6, 52, -7, -61, 1, 3, -76, 31, 11, 51, -119, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            /*com.snapchat.android.app.feature.messaging.chat.fragment.ChatV3Fragment*/ decryptMsg(new byte[]{42, -26, 61, 18, -11, 126, -23, 106, 72, 110, 75, 25, -13, 48, 95, -25, -42, 19, 50, -101, -37, 44, 18, -49, -107, -26, 49, 14, -123, -33, -97, -79, 97, -26, 82, 6, -52, -38, 22, 23, 14, 69, -102, -15, 64, 101, -114, -16, -126, -45, 26, 119, -69, -49, 91, 123, 62, 47, -38, 35, -51, 12, 96, 48, -28, 118, 12, -104, -86, -99, -8, 101, 47, -83, 64, 40, 61, -28, 117, 96})
    ); // com.snapchat.android.app.feature.messaging.chat.fragment.ChatV3Fragment
    public static final HookClass CHAT_V10_BUILDER = new HookClass(
            /*CHAT_V10_BUILDER*/ decryptMsg(new byte[]{-103, 124, -91, -123, -51, -58, -31, 111, 31, 99, -116, 61, 19, 116, 70, -13, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            /*luf*/ decryptMsg(new byte[]{70, 4, -125, -107, -67, -97, 53, 73, -77, -61, -72, -23, 0, -34, -6, 46})
    ); // kdd // TODO: DONE
    public static final HookClass PROFILE_SETTINGS_CREATOR = new HookClass(
            /*PROFILE_SETTINGS_CREATOR*/ decryptMsg(new byte[]{29, 97, 115, -84, 103, -62, -26, 122, -16, -37, -31, 68, -22, -112, -24, 81, 3, -43, 32, 41, -121, -16, 102, 119, 61, 38, 119, -13, 71, -14, 112, 84}),
            /*ipn*/ decryptMsg(new byte[]{-22, -78, 124, 87, -121, -77, 88, 41, -23, -7, -109, -21, 14, -6, -47, -39})
    ); // hem // TODO: DONE
    public static final HookClass CHEETAH_PROFILE_SETTINGS_CREATOR = new HookClass(
            /*CHEETAH_PROFILE_SETTINGS_CREATOR*/ decryptMsg(new byte[]{-82, 97, 31, -61, -23, 108, 113, 90, -88, -61, 88, -120, -47, -124, -119, 81, -18, -22, 109, 78, -125, -31, 24, -39, -28, 19, -5, -15, -32, 74, -81, 64, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66}),
            /*irw*/ decryptMsg(new byte[]{-85, 76, 122, -114, 1, 87, 112, -38, 96, -31, -118, 5, 49, -122, 44, -6})
    ); // hgr // TODO: DONE
    public static final HookClass AB_TEST_MANAGER = new HookClass(
            /*AB_TEST_MANAGER*/ decryptMsg(new byte[]{2, -54, -16, 94, 110, 57, -101, -53, 11, 92, 92, 97, 56, -31, -66, -120}),
            /*btd*/ decryptMsg(new byte[]{-7, 13, -18, -34, -114, 77, -49, -86, -99, 49, -4, 16, 39, -128, 13, -45})
    ); // rak // TODO: DONE
    public static final HookClass CHEETAH_EXPERIMENT_ENUM = new HookClass(
            /*CHEETAH_EXPERIMENT_ENUM*/ decryptMsg(new byte[]{69, 25, -55, -61, 26, -38, 60, 54, -11, 104, -13, 11, -67, -119, -96, 23, -11, 22, 48, 38, 6, -28, -93, 78, 20, 111, -76, 109, 19, 7, -42, -106}),
            /*dzk$a$a*/ decryptMsg(new byte[]{-82, -83, -12, -50, 110, 45, -126, -20, -58, -96, -124, 12, -13, -85, 63, -11})
    ); // dgq$a$a // TODO: DONE
    public static final HookClass CHEETAH_ALLOCATOR = new HookClass(
            /*CHEETAH_ALLOCATOR*/ decryptMsg(new byte[]{63, 65, -104, 65, -40, -68, 6, -58, -9, -32, 125, 8, -120, -95, 90, -21, -37, 44, 36, -91, -79, -96, 61, 47, -119, 89, 86, 113, 117, 99, 31, 29}),
            /*dzl*/ decryptMsg(new byte[]{-35, 48, 38, -25, -9, -91, -26, 22, 94, 63, -45, -67, 63, 96, 107, -47})
    ); // dgr // TODO: DONE
    public static final HookClass EXPERIMENT_BASE = new HookClass(
            /*EXPERIMENT_BASE*/ decryptMsg(new byte[]{95, 94, 48, -116, 31, 14, -59, -70, -61, 51, -98, 44, -98, -121, -84, -89}),
            /*bsz*/ decryptMsg(new byte[]{-59, -64, 56, -31, 14, -74, -63, 86, 102, -32, 108, -106, -18, -37, -54, -11})
    ); // rbr // TODO: DONE
    public static final HookClass CHEETAH_EXPERIMENT = new HookClass(
            /*CHEETAH_EXPERIMENT*/ decryptMsg(new byte[]{69, 25, -55, -61, 26, -38, 60, 54, -11, 104, -13, 11, -67, -119, -96, 23, -80, -44, 58, -95, 19, -101, 74, 113, -27, 44, 109, 4, 19, 102, -30, -43}),
            /*dzk$1$1*/ decryptMsg(new byte[]{-18, -6, -69, 3, 78, 61, -65, 32, -3, -66, 52, -123, -43, -96, -38, -38})
    ); // dgq$1$1 // TODO: DONE
    public static final HookClass STORY_MANAGER = new HookClass(
            /*STORY_MANAGER*/ decryptMsg(new byte[]{-12, -115, 52, 110, -33, -49, 97, 25, -58, 24, -23, 122, -20, -107, -98, -59}),
            /*eoa*/ decryptMsg(new byte[]{5, 95, 89, -74, -123, -127, -35, -44, -118, -70, 41, 82, 97, 112, 118, -76})
    ); // dru // TODO: DONE
    public static final HookClass STORY_DATA_DISCOVER = new HookClass(
            /*STORY_DATA_DISCOVER*/ decryptMsg(new byte[]{-110, -15, 77, 85, 20, -100, -48, 123, -66, -126, 13, 56, -75, -88, -98, -59, -38, 23, 40, -84, 11, -9, 42, -45, 5, 53, -6, -42, 87, 67, -95, -93}),
            /*aazh*/ decryptMsg(new byte[]{56, 120, -110, 35, 76, -3, -119, 53, -25, 9, 104, 42, -20, 100, -52, 98})
    ); // yph // TODO: DONE
    public static final HookClass STORY_DATA_DYNAMIC = new HookClass(
            /*STORY_DATA_DYNAMIC*/ decryptMsg(new byte[]{-102, -32, -43, -103, -28, 102, -25, 86, 9, -106, 75, 122, -17, -83, 82, -51, -14, -13, -53, -88, -4, -45, -44, -55, 76, -62, 23, 37, 109, -35, 77, -51}),
            /*aazi*/ decryptMsg(new byte[]{-94, -123, -53, -37, 119, -127, -1, -60, -25, 120, 12, -98, 66, -47, 95, 9})
    ); // ypi // TODO: DONE
    public static final HookClass STORY_DATA_MAP = new HookClass(
            /*STORY_DATA_MAP*/ decryptMsg(new byte[]{33, -63, -5, -120, -75, -111, -61, -20, 76, -20, 75, 47, 11, -56, -43, 40}),
            /*aazj*/ decryptMsg(new byte[]{90, -12, -13, 81, -97, -56, 59, 101, 57, -51, 109, -40, -90, -67, 71, -42})
    ); // ypj // TODO: DONE
    public static final HookClass STORY_DATA_PROMOTED = new HookClass(
            /*STORY_DATA_PROMOTED*/ decryptMsg(new byte[]{-28, 56, 59, -105, -7, 111, -49, -121, 68, 60, 58, -45, 14, 75, 114, -73, 56, 35, -102, -95, -119, 103, 114, -58, -52, -2, -86, 82, -38, -114, -42, 59}),
            /*aazk*/ decryptMsg(new byte[]{35, -11, -104, 41, 4, 111, -77, -53, -20, 72, -9, 75, 17, 58, -6, 39})
    ); // ypk // TODO: DONE
    public static final HookClass STORY_DATA_MOMENT = new HookClass(
            /*STORY_DATA_MOMENT*/ decryptMsg(new byte[]{55, -73, 76, -52, 2, -5, -70, 86, -5, -81, 44, 89, -20, 82, -125, -6, 84, 71, -13, -96, -32, -100, -71, 88, -32, -92, -27, 51, -107, -84, 41, 96}),
            /*aazu*/ decryptMsg(new byte[]{-38, -17, 38, 45, 109, -128, 8, -68, -103, -3, -41, -34, 99, 125, 97, 77})
    ); // ypt // TODO: DONE
    public static final HookClass DOWNLOADER_RUNNABLE = new HookClass(
            /*DOWNLOADER_RUNNABLE*/ decryptMsg(new byte[]{-9, -101, 107, -48, 7, 24, -128, 99, -33, 123, 58, -2, 46, -53, -119, -70, 57, -75, -14, 14, 89, -74, 66, -103, -95, 75, 88, 88, 47, -3, 45, -80}),
            /*smd$2*/ decryptMsg(new byte[]{91, -12, 12, -117, 78, -73, -34, -91, -106, 80, 123, 58, 6, -31, 83, 126})
    );

    public static class HookClass extends Constant {
        private String strClass;

        public HookClass(String name, @NonNull String value) {
            super(name);
            this.strClass = value;
        }

        public String getStrClass() {
            return strClass;
        }
    }
}
