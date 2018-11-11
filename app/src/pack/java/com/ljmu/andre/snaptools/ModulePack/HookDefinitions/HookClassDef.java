package com.ljmu.andre.snaptools.ModulePack.HookDefinitions;

import android.support.annotation.NonNull;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;

/**
 * ===========================================================================
 * Hook Generation | Contains: Classes, Methods/Constructors, Variables
 * ===========================================================================
 */
@SuppressWarnings({"WeakerAccess"})
public class HookClassDef extends ConstantDefiner<HookClass> {
    public static final HookClassDef INST = new HookClassDef();

    public static final HookClass SCREENSHOT_DETECTOR = new HookClass(
            "SCREENSHOT_DETECTOR",
            "aplj"
    ); // ssr
    //    public static final HookClass SNAP_DATA_JSON = new HookClass(
//            "SNAP_DATA_JSON",
//            "umf"
//    );
    public static final HookClass TEXTURE_VIDEO_VIEW = new HookClass(
            "TEXTURE_VIDEO_VIEW",
            "com.snap.opera.shared.view.TextureVideoView"
    ); // com.snap.opera.shared.view.TextureVideoView
    public static final HookClass SNAP_COUNTDOWN_CONTROLLER = new HookClass(
            "SNAP_COUNTDOWN_CONTROLLER",
            "anaq"
    ); // qiu
    public static final HookClass ENCRYPTION_ALGORITHM = new HookClass(
            "ENCRYPTION_ALGORITHM",
            "com.snapchat.android.framework.crypto.CbcEncryptionAlgorithm"
    ); // com.snapchat.android.framework.crypto.CbcEncryptionAlgorithm
    public static final HookClass ENCRYPTED_STREAM_BUILDER = new HookClass(
            "ENCRYPTED_STREAM_BUILDER",
            "aoxn"
    ); // sfo
    public static final HookClass STORY_METADATA = new HookClass(
            "STORY_METADATA",
            "svq"
    ); // cez
    public static final HookClass SNAP_BASE = new HookClass(
            "SNAP_BASE",
            "aisv"
    ); // mph
    public static final HookClass SNAP_STATUS = new HookClass(
            "SNAP_STATUS",
            "aisv$a"
    ); // mph$a
    public static final HookClass RECEIVED_SNAP = new HookClass(
            "RECEIVED_SNAP",
            "antc"
    ); // rbl
    public static final HookClass RECEIVED_SNAP_ENCRYPTION = new HookClass(
            "RECEIVED_SNAP_ENCRYPTION",
            "aiue"
    ); // mqt
    public static final HookClass RECEIVED_SNAP_PAYLOAD_BUILDER = new HookClass(
            "RECEIVED_SNAP_PAYLOAD_BUILDER",
            "zly"
    ); // dys
    public static final HookClass STORY_SNAP_PAYLOAD_BUILDER = new HookClass(
            "STORY_SNAP_PAYLOAD_BUILDER",
            "zxm"
    ); // ejj
    public static final HookClass GROUP_SNAP_METADATA = new HookClass(
            "GROUP_SNAP_METADATA",
            "aicy"
    ); // mai
    public static final HookClass STORY_SNAP = new HookClass(
            "STORY_SNAP",
            "ankl"
    ); // qta
    public static final HookClass STORY_STATUS_UPDATER = new HookClass(
            "STORY_STATUS_UPDATER",
            "zyc"
    ); // ejw
    public static final HookClass STORY_METADATA_LOADER = new HookClass(
            "STORY_METADATA_LOADER",
            "aaic"
    ); // eur
    public static final HookClass STORY_ADVANCER = new HookClass(
            "STORY_ADVANCER",
            "sdl"
    ); // buf
    public static final HookClass SENT_IMAGE = new HookClass(
            "SENT_IMAGE",
            "antf"
    ); // rbp
    public static final HookClass SENT_VIDEO = new HookClass(
            "SENT_VIDEO",
            "antt"
    ); // rcf
    public static final HookClass SENT_BATCHED_VIDEO = new HookClass(
            "SENT_BATCHED_VIDEO",
            "abbh"
    ); // fql
    public static final HookClass ENUM_BATCHED_SNAP_POSITION = new HookClass(
            "ENUM_BATCHED_SNAP_POSITION",
            "ymq"
    ); // ddf
    public static final HookClass SENT_SNAP_BASE = new HookClass(
            "SENT_SNAP_BASE",
            "ansh"
    ); // rat
    public static final HookClass META_DATA_BUILDER = new HookClass(
            "META_DATA_BUILDER",
            "ajly"
    ); // ncg
    public static final HookClass CHAT_IMAGE_METADATA = new HookClass(
            "CHAT_IMAGE_METADATA",
            "aicf"
    ); // lzv
    public static final HookClass CHAT_VIDEO_METADATA = new HookClass(
            "CHAT_VIDEO_METADATA",
            "aivv"
    ); // msp
    public static final HookClass CHAT_VIDEO = new HookClass(
            "CHAT_VIDEO",
            "aipr"
    ); // mmc
    public static final HookClass NEW_CONCENTRIC_TIMERVIEW = new HookClass(
            "NEW_CONCENTRIC_TIMERVIEW",
            "com.snap.opera.view.NewConcentricTimerView"
    ); // com.snapchat.android.framework.ui.views.NewConcentricTimerView
    public static final HookClass COUNTDOWNTIMER_VIEW = new HookClass(
            "COUNTDOWNTIMER_VIEW",
            "com.snap.opera.view.CountdownTimerView"
    ); // com.snapchat.android.framework.ui.views.CountdownTimerView
    public static final HookClass LENS_AUTHENTICATION = new HookClass(
            "LENS_AUTHENTICATION",
            "com.snapchat.android.app.feature.lenses.internal.security.LensesSecurityManager"
    ); // jij
    public static final HookClass ENUM_SNAP_ADVANCE_MODES = new HookClass(
            "ENUM_SNAP_ADVANCE_MODES",
            "slz"
    ); // cad
    public static final HookClass LENS = new HookClass(
            "LENS",
            "afmk"
    ); // jkk
    public static final HookClass LENS_CATEGORY_RESOLVER = new HookClass(
            "LENS_CATEGORY_RESOLVER",
            "afmr"
    ); // jkr
    public static final HookClass LENS_LOADER = new HookClass(
            "LENS_LOADER",
            "afgg"
    ); // jhg
    public static final HookClass LENS_CONTEXT_HOLDER = new HookClass(
            "LENS_CONTEXT_HOLDER",
            "afmx"
    ); // jkv
    public static final HookClass LENS_CAMERA_CONTEXT_ENUM = new HookClass(
            "LENS_CAMERA_CONTEXT_ENUM",
            "afmo"
    ); // jko
    public static final HookClass LENS_APPLICATION_CONTEXT_ENUM = new HookClass(
            "LENS_APPLICATION_CONTEXT_ENUM",
            "afml"
    ); // jkl
    public static final HookClass STORY_LOADER = new HookClass(
            "STORY_LOADER",
            "zul"
    ); // ees
    public static final HookClass STORY_SPONSORED = new HookClass(
            "STORY_ADVERT",
            "esr"
    ); // dwm // TODO NOT FOUND
    public static final HookClass STORY_FRIEND_RECENT = new HookClass(
            "STORY_FRIEND_RECENT",
            "erc"
    ); // dux // TODO: NOT FOUND
    public static final HookClass STORY_FRIEND_VIEWED = new HookClass(
            "STORY_FRIEND_VIEWED",
            "aafy"
    ); // dud // TODO CHECK
    public static final HookClass STORY_SNAP_AD_LOADER = new HookClass(
            "STORY_SNAP_AD_LOADER",
            "zwf"
    ); // ehu
    public static final HookClass FRIEND_PROFILE_POPUP_FRAGMENT = new HookClass(
            "FRIEND_PROFILE_POPUP_FRAGMENT",
            "com.snapchat.android.app.feature.miniprofile.internal.friend.FriendMiniProfilePopupFragment"
    ); // com.snapchat.android.app.feature.miniprofile.internal.friend.FriendMiniProfilePopupFragment
    public static final HookClass USER_PREFS = new HookClass(
            "USER_PREFS",
            "com.snapchat.android.core.user.UserPrefsImpl"
    ); // sww
    public static final HookClass CAMERA_FRAGMENT = new HookClass(
            "CAMERA_FRAGMENT",
            "com.snapchat.android.app.feature.camera.CameraFragmentV2"
    ); // com.snapchat.android.app.main.camera.CameraFragmentV1
    public static final HookClass SHARE_IMAGE = new HookClass(
            "SHARE_IMAGE",
            "aavx"
    ); // CAMERA_FRAGMENT
    public static final HookClass SHARE_VIDEO = new HookClass(
            "SHARE_VIDEO",
            "aawc"
    ); // CAMERA_FRAGMENT
    public static final HookClass ENUM_LENS_TYPE = new HookClass(
            "ENUM_LENS_TYPE",
            "afmk$c"
    ); // jkk$b
    public static final HookClass LENS_CATEGORY = new HookClass(
            "LENS_CATEGORY",
            "afmt"
    ); // jkt
    public static final HookClass LENS_SLUG = new HookClass(
            "LENS_SLUG",
            "avbs"
    ); // xet
    public static final HookClass LENS_TRACK = new HookClass(
            "LENS_TRACK",
            "avgb"
    ); // xjk
    //    public static final HookClass LENS_ASSET = new HookClass(
//            "LENS_ASSET",
//            "qvy"
//    );
    public static final HookClass LENS_ASSET_BUILT = new HookClass(
            "LENS_ASSET_BUILT",
            "afmm"
    ); // jkm
    public static final HookClass LENS_ASSET_TYPE = new HookClass(
            "LENS_ASSET_TYPE",
            "afmm$a"
    ); // jkm$a
    public static final HookClass LENS_ASSET_LOAD_MODE = new HookClass(
            "LENS_ASSET_LOAD_MODE",
            "afnj"
    ); // jlb
    public static final HookClass ENUM_LENS_ACTIVATOR_TYPE = new HookClass(
            "ENUM_LENS_ACTIVATOR_TYPE",
            "com.looksery.sdk.domain.Category.ActivatorType"
    ); // com.looksery.sdk.domain.Category.ActivatorType
    public static final HookClass FONT_CLASS = new HookClass(
            "FONT_CLASS",
            "android.graphics.Typeface"
    ); // android.graphics.Typeface
    public static final HookClass SNAPCHAT_CAPTION_VIEW_CLASS = new HookClass(
            "SNAPCHAT_CAPTION_VIEW_CLASS",
            "com.snapchat.android.app.feature.creativetools.caption.SnapCaptionView"
    ); // com.snapchat.android.app.feature.creativetools.caption.SnapCaptionView
    public static final HookClass CAPTION_MANAGER_CLASS = new HookClass(
            "CAPTION_MANAGER_CLASS",
            "tvp"
    ); // gau
    public static final HookClass CHAT_MESSAGE_VIEW_HOLDER = new HookClass(
            "CHAT_MESSAGE_VIEW_HOLDER",
            "aimh"
    ); // mje
    public static final HookClass OPERA_PAGE_VIEW = new HookClass(
            "OPERA_PAGE_VIEW",
            "com.snap.opera.view.OperaPageView"
    ); // com.snap.opera.view.OperaPageView
    public static final HookClass CHAT_METADATA_JSON_PARSER = new HookClass(
            "CHAT_METADATA_JSON_PARSER",
            "auij"
    ); // wjo
    public static final HookClass CHAT_METADATA_JSON_PARSER_SECOND = new HookClass(
            "CHAT_METADATA_JSON_PARSER_SECOND",
            "auvg"
    ); // wxx
    public static final HookClass CHAT_METADATA = new HookClass(
            "CHAT_METADATA",
            "auii"
    ); // wjn
    public static final HookClass CHAT_HEADER_METADATA = new HookClass(
            "CHAT_HEADER_METADATA",
            "auqf"
    ); // wso
    public static final HookClass CHAT_BODY_METADATA = new HookClass(
            "CHAT_BODY_METADATA",
            "auuq"
    ); // wxa
    //    public static final HookClass GEOFILTER_VIEW = new HookClass(
//            "GEOFILTER_VIEW",
//            "com.snapchat.android.app.shared.feature.preview.ui.smartfilters.geofilterview.GeofilterView2"
//    ); // com.snapchat.android.app.shared.feature.preview.ui.smartfilters.GeofilterView
//    public static final HookClass FILTER_LOADER = new HookClass(
//            "FILTER_LOADER",
//            "anhf"
//    );
//    public static final HookClass FILTER_METADATA = new HookClass(
//            "FILTER_METADATA",
//            "anlt"
//    ); // quj
//    public static final HookClass SERIALIZABLE_FILTER_METADATA = new HookClass(
//            "SERIALIZABLE_FILTER_METADATA",
//            "aups"
//    ); // wsa
//    public static final HookClass FILTER_METADATA_LOADER = new HookClass(
//            "FILTER_METADATA_LOADER",
//            "abtu"
//    ); // ghu
//    public static final HookClass FILTER_METADATA_CREATOR = new HookClass(
//            "FILTER_METADATA_CREATOR",
//            "annh$2"
//    ); // qvs$2
//    public static final HookClass GEOFILTER_VIEW_CREATOR = new HookClass(
//            "GEOFILTER_VIEW_CREATOR",
//            "ghb"
//    ); // fdr // TODO: DONE
    public static final HookClass CHAT_NOTIFICATION_CREATOR = new HookClass(
            "CHAT_NOTIFICATION_CREATOR",
            "ahnj"
    ); // lmq
    public static final HookClass NETWORK_MANAGER = new HookClass(
            "NETWORK_MANAGER",
            "apbv"
    ); // sjv
    /**
     * Hooking the {@link HookDef#NETWORK_EXECUTE_SYNC} method leads to an Exception in this
     * Runnable. We hook and suppress this exception
     */
    public static final HookClass DOWNLOADER_RUNNABLE = new HookClass(
            "DOWNLOADER_RUNNABLE",
            "apeh$2"
    );
    public static final HookClass NETWORK_DISPATCHER = new HookClass(
            "NETWORK_DISPATCHER",
            "ahql"
    ); // lpo
    //    public static final HookClass CHAT_MESSAGE_METADATA = new HookClass(
//            "CHAT_MESSAGE_METADATA",
//            "ktw"
//    );
    public static final HookClass CHAT_GROUP_VIEW_MARKER = new HookClass(
            "CHAT_GROUP_VIEW_MARKER",
            "ahsh"
    ); // lrk
    public static final HookClass CHAT_DIRECT_VIEW_MARKER = new HookClass(
            "CHAT_DIRECT_VIEW_MARKER",
            "ahrf"
    ); // lqg
    //    public static final HookClass CHAT_V3_FRAGMENT = new HookClass(
//            "CHAT_V3_FRAGMENT",
//            "com.snapchat.android.app.feature.messaging.chat.fragment.ChatV3Fragment"
//    ); // com.snapchat.android.app.feature.messaging.chat.fragment.ChatV3Fragment
    public static final HookClass CHAT_V10_BUILDER = new HookClass(
            "CHAT_V10_BUILDER",
            "ahvq"
    ); // luf
    public static final HookClass CHEETAH_PROFILE_SETTINGS_CREATOR = new HookClass(
            "CHEETAH_PROFILE_SETTINGS_CREATOR",
            "aeiu"
    ); // irw
    public static final HookClass AB_TEST_MANAGER = new HookClass(
            "AB_TEST_MANAGER",
            "apqw"
    ); // btd
    public static final HookClass EXPERIMENT_BASE = new HookClass(
            "EXPERIMENT_BASE",
            "apqt"
    ); // bsz
    public static final HookClass STORY_MANAGER = new HookClass(
            "STORY_MANAGER",
            "aadm"
    ); // eoa
    public static final HookClass STORY_DATA_DISCOVER = new HookClass(
            "STORY_DATA_DISCOVER",
            "ivb"
    ); // aazh
    public static final HookClass STORY_DATA_DYNAMIC = new HookClass(
            "STORY_DATA_DYNAMIC",
            "ivc"
    ); // aazi
    public static final HookClass STORY_DATA_MAP = new HookClass(
            "STORY_DATA_MAP",
            "ivd"
    ); // aazj
    public static final HookClass STORY_DATA_PROMOTED = new HookClass(
            "STORY_DATA_PROMOTED",
            "ive"
    ); // aazk
    public static final HookClass STORY_DATA_MOMENT = new HookClass(
            "STORY_DATA_MOMENT",
            "ivr"
    ); // aazu

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
