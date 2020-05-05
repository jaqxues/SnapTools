package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;

import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.AB_TEST_CHECK_BOOLEAN;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.AB_TEST_CHECK_FLOAT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.AB_TEST_CHECK_INT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.AB_TEST_CHECK_LONG;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.AB_TEST_CHECK_STRING;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.ERROR_SUPPRESS_DOWNLOADER_RUNNABLE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.NETWORK_EXECUTE_SYNC;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_ANIMATED_CONTENT_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CAMERA2_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CAPTIONV2_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CHAT_VIDEO_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_EMOJIBRUSH_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_FPS_OVERLAY_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_GIPHY_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_HANDSFREEREC_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_INSIGHTS_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_MULTI_SNAP_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_SKYFILTERS_STATE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_ADVERTS_BLOCKED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_DISCOVER_BLOCKED;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ForcedHooks extends ModuleHelper {

    private boolean miscChangesEnabled;

    public ForcedHooks(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[0];
    }

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        boolean blockDiscovery = getPref(STORY_BLOCKER_DISCOVER_BLOCKED);
        boolean blockAds = getPref(STORY_BLOCKER_ADVERTS_BLOCKED);

        miscChangesEnabled = !collectionContains(DISABLED_MODULES, "Misc Changes");

        /**
         * ===========================================================================
         * Returns TRUE to mark SC as DEBUG build
         * ===========================================================================
         */
//        findAndHookMethod(
//                "aqly", snapClassLoader,
//                "p", XC_MethodReplacement.returnConstant(true)
//        );


//		findAndHookMethod(
//				"tfj", snapClassLoader,
//				"a", findClass("tfj$c", snapClassLoader),
//				new ST_MethodHook() {
//					@Override protected void after(MethodHookParam param) throws Throwable {
//						Timber.d("Inbound debug item: " + param.args[0] + " | " + param.getResult());
//
//						Enum debugEnum = (Enum) param.args[0];
//
//						switch (debugEnum.name()) {
//							case "CROP_SNAP_ENABLED":
//								param.setResult(true);
//								break;
//						}
//					}
//				}
//		);

        //Forced Chronological Friends Feed
//		findAndHookMethod(
//				"ifj", snapClassLoader,
//				"a", List.class, findClass("sda", snapClassLoader),
//				new HookWrapper((HookBefore) param -> XposedHelpers.setObjectField(param.thisObject, "a", null))
//		);

        /*
        Snapchat Experiments
         */
        Boolean insightsMode = transformBoolean(FORCE_INSIGHTS_STATE);
        String multiSnapMode = transformOverwrite(FORCE_MULTI_SNAP_STATE);
        Boolean videoChatMode = transformBoolean(FORCE_CHAT_VIDEO_STATE);
        String animatedContentMode = transformOverwrite(FORCE_ANIMATED_CONTENT_STATE);
        Boolean giphyMode = transformBoolean(FORCE_GIPHY_STATE);
        String captionV2Mode = transformOverwrite(FORCE_CAPTIONV2_STATE);
        String camera2Mode = transformOverwrite(FORCE_CAMERA2_STATE);
        Boolean camera2ModeBool = transformBoolean(FORCE_CAMERA2_STATE);
        String handsFreeMode = transformOtherOnOff(FORCE_HANDSFREEREC_STATE, "FULLY_ENABLED", "DISABLED", null);
        Boolean fpsOverlayMode = transformBoolean(FORCE_FPS_OVERLAY_STATE);
        String skyFilterMode = transformOverwrite(FORCE_SKYFILTERS_STATE);
        Boolean emojiBrushMode = transformBoolean(FORCE_EMOJIBRUSH_STATE);

        HookAfter experimentDebugHook = null;

//		if (Constants.getApkVersionCode() >= 73 && Constants.isApkDebug())
//			experimentDebugHook = this::handleExperimentPrinting;

        findAndHookMethod(
                "axbl", snapClassLoader,
                "a", "axbm", Object.class,
                new HookWrapper(
                        param -> {
                            String key = (String) callMethod(param.args[0], "a");

                            switch (key) {
                                /**
                                 * ===========================================================================
                                 * Experiments
                                 * ===========================================================================
                                 */
//                                case "developerOptionCheetahMode":
//                                    handleExperiment(param, cheetahMode);
//                                    break;
//							case "chat_v10":
//								handleExperiment(param, chatV10Mode);
//								break;
                                case "developerOptionsImpalaForceShowInsights":
                                    handleExperiment(param, insightsMode);
                                    break;
                                case "magikarp_overwrite":
                                    handleExperiment(param, multiSnapMode);
                                    break;
                                case "chat_video_enabled":
                                    handleExperiment(param, videoChatMode);
                                    break;
                                case "animated_content_overwrite":
                                    handleExperiment(param, animatedContentMode);
                                    break;
                                case "giphy_in_preview":
                                    handleExperiment(param, giphyMode);
                                    break;
                                case "caption_v2_overwrite":
                                    handleExperiment(param, captionV2Mode);
                                    break;
                                case "camera2_overwrite_state":
                                    handleExperiment(param, camera2Mode);
                                    break;
                                case "developerOptionsHandsFreeRecordingMode":
                                    handleExperiment(param, handsFreeMode);
                                    break;
                                case "developerOptionsShouldShowFpsOverlay":
                                    handleExperiment(param, fpsOverlayMode);
                                    break;
                                case "sky_filters_overwrite":
                                    handleExperiment(param, skyFilterMode);
                                    break;
                                case "emoji_brush":
                                    handleExperiment(param, emojiBrushMode);

                                    break;


                                /**
                                 * ===========================================================================
                                 * Forced Enabled Settings
                                 * ===========================================================================
                                 */
                                case "nycEnableStreaming":
                                case "developerOptionsNycSearchStreamingAbtestOverride":
                                case "developerOptionsNycPublicStoryStreamingAbtestOverride":
                                    param.setResult("FALSE");
                                    break;
                                case "discover_feed_tab_mode":
                                    param.setResult("FORCE_ON");
                                    break;
                                case "discover_feed_should_show_subscribed_tab_nux":
                                    param.setResult(true);
                                    break;
                                case "enable geofilters":
                                    param.setResult(true);
                                    break;
                            }
                        },
                        experimentDebugHook
                )
        );

        ST_MethodHook abTestHook = new HookWrapper(
                (HookBefore) param -> {
                    String groupName = (String) param.args[0];
                    String experimentName = (String) param.args[1];

                    String key = groupName + ":" + experimentName;

                    switch (key) {
                        case "DOWNSCALE_TAKE_PICTURE_API_PHOTO_BEFORE_SEND:ENABLED":
                            handleExperiment(param, camera2ModeBool);
                            break;
                        case "ADS_HOLDOUT_01:SHOW_ADS":
                        case "ADS_HOLDOUT_01:ADS_IN_AA":
                            if (blockAds)
                                param.setResult(false);
                            break;
                        case "CRASHLYTICS:USE_CRASHLYTICS":
                            param.setResult(false);
                            break;
                        case "PUBLIC_STORY_STREAMING_ANDROID:enable":
                        case "STREAMING_PROMOTED_STORIES_ANDROID:enable":
                            param.setResult(false);
                            break;
                    }
                },
//				(Constants.getApkVersionCode() >= 73 && Constants.isApkDebug()) ?
//						(HookAfter) param -> handleABTestPrinting(param) : null
                null
        );

        hookMethod(
                AB_TEST_CHECK_STRING,
                abTestHook
        );

        hookMethod(
                AB_TEST_CHECK_INT,
                abTestHook
        );

        hookMethod(
                AB_TEST_CHECK_LONG,
                abTestHook
        );

        hookMethod(
                AB_TEST_CHECK_BOOLEAN,
                abTestHook
        );

        hookMethod(
                AB_TEST_CHECK_FLOAT,
                abTestHook
        );

//		hookMethod(
//				AB_TEST_CHECK_VALUE,
//				new HookWrapper((HookAfter) param -> {
//					Timber.d("RAK: [p1: %s][p2: %s][p3: %s][r: %s]", param.args[0], param.args[1], param.args[2], param.getResult());
//
//					switch ((String) param.args[0]) {
////							case "CHEETAH_ANDROID":
////								if (param.args[1].equals("CHEETAH_MODE"))
////									param.setResult(forceCheetah ? "FULL_CHEETAH" : "OLD_DESIGN");
////								break;
////							case "DIRECT_FILE_SNAP_UPLOAD":
////								if (param.args[1].equals("ENABLED"))
////									param.setResult("true");
////								break;
////								case "LANDING_PAGE_CAMERA_VIEW":
////									param.setResult("true");
////									break;
//						case "DOWNSCALE_TAKE_PICTURE_API_PHOTO_BEFORE_SEND":
//							if (param.args[1].equals("ENABLED"))
//								param.setResult("false");
//							break;
////							case "ANDROID_CAMERA2_AND_TAKEPICTURE_API_v2":
////								if (param.args[1].equals("enable"))
////									param.setResult("true");
////								break;
////							case "CAMERA_FRAGMENT_V2":
////								if (param.args[1].equals("ENABLED"))
////									param.setResult("true");
////								break;
//						case "PUBLIC_STORY_STREAMING_ANDROID":
//							if (param.args[1].equals("enable"))
//								param.setResult("false");
//							return;
//						case "STREAMING_PROMOTED_STORIES_ANDROID":
//							if (param.args[1].equals("enable"))
//								param.setResult("false");
//							return;
////							case "ASYM_VIEW_PAGER":
////								if (param.args[1].equals("ENABLED"))
////									param.setResult("true");
////								break;
////							case "SECURE_CHAT_SESSION_V2":
////								if (param.args[1].equals("ENABLED"))
////									param.setResult("true");
////								break;
//					}
//
//					switch ((String) param.args[1]) {
//						case "SHOW_ADS":
//						case "ADS_IN_AA":
//							if (getPref(STORY_BLOCKER_ADVERTS_BLOCKED))
//								param.setResult("false");
//							return;
//						case "ENABLE_MULTI_SNAP":
//							param.setResult("true");
//							return;
//						case "ENABLE_PREVIEW_V2":
//							param.setResult("true");
//							return;
//						case "USE_CRASHLYTICS":
//							param.setResult("false");
//							return;
////							case "RELEASE_DELAY_TIME_WHEN_USER_SWIPE_INTO_CONVERSATION":
////								param.setResult("5000");
////								break;
//						case "ENCODING_QUALITY":
//							param.setResult("100");
//							break;
//						case "DF_TAB_ANDROID":
//							if (blockDiscovery)
//								param.setResult("true");
//							break;
////							case "USE_SERVER_SIDE_PRECACHING":
////								param.setResult("false");
////								break;
////							case "MEDIA_PLAYER_IMPL_EXOPLAYER":
////								param.setResult("true");
////								break;
////							case "SHOULD_SHOW_SPONSORED_SECTION":
////								param.setResult("false");
////								break;
////							case "fps":
////								param.setResult("30");
////								break;
//					}
//
//				}));


        hookMethod(
                NETWORK_EXECUTE_SYNC,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        String url = (String) callMethod(param.thisObject, "getUrl");

                        Timber.d("Network URL: " + url);

                        if (url.endsWith("logout")) {
                            Timber.d("Blocking logout");
                            param.setResult(null);
                        }
                    }
                }
        );
        // Error Suppression for the hook above
        hookMethod(
                ERROR_SUPPRESS_DOWNLOADER_RUNNABLE,
                new HookWrapper((HookAfter) param -> {
                    if (param.getThrowable() != null) {
                        Timber.d("Download Runnable Error Suppression");
                        param.setThrowable(null);
                    }
                })
        );

        /**
         * ===========================================================================
         * Just used as a fatal crash prevention... Likely just moves the issue
         * ===========================================================================
         */
//		XposedHelpers.findAndHookMethod(
//				"htt", snapClassLoader,
//				"a",
//				new ST_MethodHook() {
//					@Override protected void after(MethodHookParam param) throws Throwable {
//						if (param.getThrowable() != null) {
//							Timber.e(new Throwable(
//									"Error raised from lens: " +
//											XposedHelpers.getObjectField(param.thisObject, "d").toString(),
//									param.getThrowable()
//							));
//
//							param.setResult(false);
//						}
//					}
//				}
//		);

        /**
         * ===========================================================================
         * Just used as a fatal crash prevention... Likely just moves the issue
         * ===========================================================================
         */
//		XposedHelpers.findAndHookMethod(
//				"hto", snapClassLoader,
//				"c", String.class,
//				new ST_MethodHook() {
//					@Override protected void after(MethodHookParam param) throws Throwable {
//						if (param.getThrowable() != null) {
//							Timber.e(new Throwable(
//									"Error raised checking lens set content",
//									param.getThrowable()
//							));
//
//							param.setResult(false);
//						}
//					}
//				}
//		);
    }

    private String transformOtherString(Preference preference) {
        if (!miscChangesEnabled)
            return null;

        String preferenceValue = getPref(preference);

        if (preferenceValue == null || preferenceValue.equals("Default")) {
            return null;
        }

        return preferenceValue;
    }

    private Boolean transformBoolean(Preference preference) {
        if (!miscChangesEnabled)
            return null;

        String preferenceValue = getPref(preference);

        if (preferenceValue != null) {
            if (preferenceValue.equals("On")) {
                return true;
            } else if (preferenceValue.equals("Off")) {
                return false;
            }
        }

        return null;
    }

    private String transformOverwrite(Preference preference) {
        if (!miscChangesEnabled)
            return null;

        String preferenceValue = getPref(preference);

        if (preferenceValue != null) {
            if (preferenceValue.equals("On")) {
                return "FORCE_ENABLED";
            } else if (preferenceValue.equals("Off")) {
                return "FORCE_DISABLED";
            }
        }

        return null;

    }

    private <T> T transformOtherOnOff(Preference preference, T on, T off, T def) {
        if (!miscChangesEnabled)
            return null;

        String preferenceValue = getPref(preference);

        if (preferenceValue != null) {
            if (preferenceValue.equals("On")) {
                return on;
            } else if (preferenceValue.equals("Off")) {
                return off;
            }
        }

        return def;
    }

    private void handleExperiment(MethodHookParam param, Object experimentMode) {
        if (!miscChangesEnabled)
            return;

        if (experimentMode != null)
            param.setResult(experimentMode);
    }

    private void handleABTestPrinting(MethodHookParam param) {
        String groupName = (String) param.args[0];
        String experimentName = (String) param.args[1];

        Object result = param.getResult();

        switch (groupName) {
            case "DISCOVER_V2":
                break;
            default:
                Timber.d("ABTest [Group: %s][Exp: %s][Def: %s][Res: %s]", groupName, experimentName, param.args[2],
                        result + (result != null ? "(" + result.getClass() + ")" : ""));
        }
    }

    private void handleExperimentPrinting(MethodHookParam param) {
        {
            String key = (String) callMethod(param.args[0], "a");
            Object defaultValue = param.args[1];
            Object result = param.getResult();

            switch (key) {
                /**
                 * ===========================================================================
                 * Tests
                 * ===========================================================================
                 */
                case "cold_start_stabilization":
                    param.setResult("Rainbow");
                    break;
                case "is_official_user":
                    param.setResult(true);
                    break;

                case "is_logged_in":
                case "is_registering":
                case "username":
                case "birthday":
                case "enable_shake_to_report":
                case "nycSettingsGhostMode":
                case "nycSettingsGhostModeDuration":
                case "nycSettingsAudience":
                case "nycSettingsCustomFriendIds":
                case "nycHasOnboarded":
                case "nycDontShareState":
                case "memories_year_end_story_badge":
                case "developerOptionsNycTrackActivity":
                case "BITMASK_AVATAR_ID":
                case "has_seen_new_user_onboard_ui":
                case "birthday_in_millis":
                case "birthday_this_year_in_millis":
                case "has_seen_snap_onboarding_message":
                case "memories_year_end_story":
                case "perf_bandwidthsampler_version":
                case "perf_downloadmanager_hyperrequest_enabled":
                case "perf_downloadmanager_hyperrequest_disabled":
                case "user_id":
                case "BITMOJI_SELFIE_ID":
                case "daily_client_id_timestamp":
                case "daily_client_id":
                case "last_lenses_enabled_date":
                case "auth_token":
                case "perf_preferred_network_interface":
                case "email":
                case "perf_enable_detailed_timing_metrics":
                case "story_count":
                case "enable_featured_official_stories":
                case "ad_track_user_data":
                case "ad_preferences":
                case "registered_in_cheetah":
                case "developerOptionMockLocation":
                case "developerOptionMockLocationNYCOffice":
                case "enable_resumable_download":
                case "delta_fetch_one_on_one_conversations":
                case "perf_hyper_callback_feature_executor":
                case "cheetah_selfie_id":
                case "web_attachments_overwrite":
                case "nycSharingNotificationLastSeenTimestamp":
                case "nycLastOpenMapTimestamp":
                case "nycSharingNotificationCount":
                case "nycHasSeenSharingNotification":
                case "nycSettingsShowDevToolTipOnce":
                case "search_seeen_p2s_tooltip_count":
                case "account_creation_millis":
                case "has_seen_cheetah_camera_onboarding_my_story_management_tooltip":
                case "has_seen_cheetah_onboarding_my_story_view_tooltip":
                case "has_given_access_to_contacts":
                case "is_popular_user":
                case "profile_v3_phone_number_verification_prompt":
                case "friendmoji_blocked_keys":
                case "phone_number":
                case "last_profile_fragment_exit_timestamp":
                case "last_seen_added_me_timestamp":
                case "bitmojiFetchInWebp":
                case "FRIEND_FEED_AST_GROUP":
                case "developerOptionsSnapAdsEnablePromotedStories":
                case "developerOptionsCheetahFreeformRankingTreatments":
                case "developer_option_lens_on_preview_tweak":
                case "has_seen_swipe_filters_onboarding_message":
                case "transcoding_overwrite_state":
                case "transcodingState":
                case "video_filters_overwrite_state":
                case "video_filters":
                case "usps_geofencing_v2":
                case "usps_multi_location":
                case "sticker_picker_hometab_overwrite":
                case "context_filter_metadata_timestamp":
                case "captureRotation":
                case "show_geofilter_tools_enabled":
                case "checksum_friends":
                case "checksum_updates":
                case "checksum_conversations":
                case "checksums_study_settings":
                case "last_time_low_sensitivity_unlockables_request":
                case "backup_fastlane":
                case "last_scan_unlocked_lenses_check_time":
                case "next_force_scheduled_lenses_check_time":
                case "should_force_low_sensitivity_request":
                case "is_first_all_updates_on_app_upgrade":
                case "geofilter_allow_concurrent_requests":
                case "friends_sync_token":
                case "FRIEND_FEED_LAST_FULL_SYNC_TIMESTAMP":
                case "developerOptionsShouldShowLocationToast":
                case "enable_usps_geolens":
                case "has_clicked_to_turn_off_lens":
                case "usps_gtq_migration_plan":
                case "developer_option_force_camera_30_fps":
                case "gles3_allowed":
                case "is_using_multiple_frame_buffer_recording":
                case "reg_user_complete_timestamp":
                case "reg_last_transition_action":
                case "reg_last_page":
                case "developerOptionsShowFrameDispatcherBufferUsage":
                case "snapads_ad_session_id":
                case "snap_tag_image":
                case "has_profile_images":
                case "address_book_version":
                case "is_device_whitelisted_for_lenses_on_backend":
                case "PENDING_CLIENT_PROPERTIES_V1":
                case "square_tos_accepted":
                case "has_seen_send_to_quick_add_dialog":
                case "has_seen_send_to_sms_snap_alert_v2":
                case "has_seen_auto_friend_invite_alert":
                case "tos_version_6_accepted":
                case "spectacles_tos_accepted":
                case "has_seen_camera_module_lens":
                case "has_seen_camera_module_scan":
                case "clipboard_detection_enabled":
                case "search_our_story_attribution_tos_accepted":
                case "has_used_memories_search":
                case "number_of_enter_memories":
                case "image_player_reset_timestamp":
                case "transcoding_reset_timestamp":
                case "cheetah_partial_story_response":
                case "cash_customer_allowed":
                case "last_seen_new_friends_tooltip_timestamp":
                case "gcm_registration_id":
                case "notificationsEnabled":
                case "-1572590044has_given_access_to_contacts":
                case "story_privacy_setting":
                case "last_checked_trophycase_timestamp":
                case "display_name_pop_up_count":
                case "last_identity_action_timestamp":
                case "developerOptionsDiscoverCustomRegion":
                case "developerOptionsDiscoverCustomCountry":
                case "suggested_friend_sync_version_v2":
                case "use_unsigned_receipt":
                case "dirty_video_rendering_overwrite_state":
                case "dirty_video_rendering":
                case "video_decoder_texcoord_transformation":
                case "developerOptionsSetDecoderOperatingRate":
                case "snapchatInfiniteVideoPreference":
                case "should_override_track_url":
                case "FRIEND_FEED_WARM_START_BACKGROUND_TIME_THRESHOLD_SECONDS":
                case "FRIEND_FEED_WARM_START_LAST_FULL_RANKING_THRESHOLD_SECONDS":
                case "has_seen_sound_tools_tooltip":
                case "has_seen_auto_sticker_generation_tooltip":
                case "developerOptionsCustomEndpoint":
                case "app_application_open_client_ts":
                case "last_permission_report_timestamp":
                case "is_snapchat_contact_permission_synced":
                case "reg_started":
                    break;
                default:
                    Timber.d(
                            "New preference check: " + key + " | " + defaultValue + " | "
                                    + result + (result != null ? "(" + result.getClass() + ")" : "")
                    );
            }
        }
    }
}
