package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmu.andre.snaptools.Networking.Helpers.GetRemoteConfig;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.STApplication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("ConstantConditions")
public class RemoteConfig {
    private static RemoteConfig remoteConfig;

    private static Map<String, Object> remoteConfigMap;
    private static final Map<String, Object> defaultConfigMap = ImmutableMap.<String, Object>builder()
            // Constants Defaults ========================================================
            .put("shop_check_cooldown_hours",
                    STApplication.DEBUG ? 30000 : TimeUnit.HOURS.toMillis(1))
            .put("apk_check_cooldown_minutes",
                    STApplication.DEBUG ? 30000 : TimeUnit.MINUTES.toMillis(15))
            .put("pack_check_cooldown_minutes",
                    STApplication.DEBUG ? 30000 : TimeUnit.MINUTES.toMillis(15))
            .put("faq_check_cooldown_hours",
                    STApplication.DEBUG ? 30000 : TimeUnit.HOURS.toMillis(12))
            .put("features_check_cooldown_hours",
                    STApplication.DEBUG ? 30000 : TimeUnit.HOURS.toMillis(12))
            .put("translations_check_cooldown_hours",
                    STApplication.DEBUG ? 30000 : TimeUnit.HOURS.toMillis(12))
            .put("remote_config_check_cooldown_hours",
                    STApplication.DEBUG ? 30000 : TimeUnit.HOURS.toMillis(4))
            .put("remind_tutorial_cooldown_days",
                    TimeUnit.DAYS.toMillis(14))
            // ===========================================================================

            .put("about_us_concept", "SnapTools was initially started in early 2017 as a way to provide Snapchat modifications, such as saving, with a new cloud based way of delivering fast updates. What we had in mind was a fully modular system that would allow all our code that interfaces with Snapchat to be downloaded in the form of \"packs\" from our server and stored and executed within SnapTools itself, allowing for multiple Snapchat versions to be supported, and for code updates to not require a new APK or a device restart.\n\nAs our knowledge of the inner workings of Snapchat grew, so did our project. We had established an incredibly capable framework and started adding features that many users requested, such as lens collection and management, screenshot bypass, and sharing from gallery, along with many others. We made our Slack page open to the public in August as we prepared for a pre-release, to attract attention and bring users to our community that could help with testing our product before our official release.")
            .put("about_us_description", "SnapTools started with Andre (andrerm124) as the sole developer, with Pedro (pedroc1999) and Gabe (quorn23) as part of the decision making team. Ethan (electronicwizard) also joined as a developer/tester and Michal (nightmean) helped while testing SnapTools and during the bug finding process. The majority of our team are currently in or recently graduated from higher level education.")

            .put("support_discord_text", "For the fastest response to your questions, please join our Discord server where there is usually 24/7 support."
                    + "\nOnce joined check out the <b><font color='#efde86'><u>FAQ</u></font></b> channel to check if your question has already been answered. Otherwise try out the <b><font color='#efde86'><u>@Support</u></font></b> tag to notify the support team of your question."
                    + "\n\nSpecial thanks to the Support team (TupaC, Liz, Freekystar, jaqxues, Erik, Zack4200, Badgermole, Kevin T and Fullonrager) for helping out with testing and their community assistance")
            .put("support_discord_link", "https://discordapp.com/invite/T29v543")

            .put("support_website_text", "Want more information about SnapTools? View our official Website hosted by the lovely <b><font color='#efde86'><u>ElectronicWizard</u></font></b> to get an idea of what we have to offer as well as some FAQs\n(No longer maintained)")
            .put("support_website_link", "http://snaptools.org")

            .put("support_reddit_text", "We also have a Sub-Reddit that is under construction but is still completely usable if you want to post anything relating to SnapTools. Subscribing to us can help to build our community to let new users know that we're a trusted application.\nThe Reddit will contain announcements and an up to date FAQ list.")
            .put("support_reddit_link", "http://snaptools.org/reddit")

            .put("support_xda_text", "Our XDA page hasn't been created yet. We're currently waiting for a full public release before we create the page.\n(Maybe at some point just to advertise for Andre's ModulePack System)")
            .put("support_xda_link", "http://snaptools.org/xda")

            .put("support_twitter_text", "(No longer maintained)")
            .put("support_twitter_link", "http://snaptools.org/twitter")

            .put("check_sc_beta", true)

            // Translation API ===========================================================
            .put("enable_translation_api_beta", true)
            .put("enable_translation_api_prod", false)
            .put("translation_url_root", "https://raw.githubusercontent.com/Andrerm124/SnapToolsTranslations/master/")
            .put("translation_files", "English, French, German, Polski, TestLanguage")
            // ===========================================================================

            // Shop Fragment =============================================================
            .put("payment_model_reasoning", "SnapTools uses a <b><font color='#EFDE86'>Pay Per Version</font></b> model instead of a <font color='#EF8686'>monthly subscription</font> based system so that you only pay when you want to use a newer Snapchat version.\n" +
                    "\n" +
                    "This aims to provide a cheaper method for our users, but still make it worth our time for maintaining the software.\n" +
                    "\n" +
                    "It should be stressed that you are <b><font color='#EFDE86'>NEVER</font></b> forced to update to any version. You are free to use any premium packs you purchased for as long as you are able to use them.\n" +
                    "\n" +
                    "SnapTools also offers a <b><font color='#EFDE86'>7 Day Guarantee</font></b> so that if a pack for a newer Snapchat is released within 7 days of you purchasing a pack, you will automatically be given access to the newer pack as well.")
            // ===========================================================================

            .build();

    public static RemoteConfig getConfig() {
        if (remoteConfig == null)
            remoteConfig = new RemoteConfig();
        return remoteConfig;
    }

    public static void init(Activity activity, Callable<RemoteConfig> runnable) {
        new GetRemoteConfig().smartFetch(
                activity,
                new WebResponse.ObjectResultListener<byte[]>() {
                    @Override
                    public void success(String message, byte[] object) {
                        Timber.d("Fetched RemoteConfig, Message: %s", message);
                        try {
                            remoteConfigMap = new Gson().fromJson(
                                    new String(object, Charset.defaultCharset()),
                                    new TypeToken<HashMap<String, Object>>() {
                                    }.getType()
                            );
                        } catch (Exception e) {
                            Timber.e(e, "Could not build Remote Config Map from JSON: %s", new String(object, Charset.defaultCharset()));
                            return;
                        }
                        for (String key : defaultConfigMap.keySet()) {
                            if (!remoteConfigMap.containsKey(key))
                                remoteConfigMap.put(key, defaultConfigMap.get(key));
                        }
                        if (remoteConfig == null)
                            remoteConfig = new RemoteConfig();

                        runnable.call(remoteConfig);
                    }

                    @Override
                    public void error(String message, Throwable t, int errorCode) {
                        Timber.e(t, "Unable to fetch RemoteConfig. Message: %s, ErrorCode: %d", message, errorCode);
                        SafeToast.show(
                                activity,
                                "Unable to fetch RemoteConfig file",
                                true
                        );
                    }
                }
        );
    }

    public long getLong(String key) {
        return (long) getObject(key);
    }

    public String getString(String key) {
        return (String) getObject(key);
    }

    public Object getObject(String key) {
        if (remoteConfigMap != null)
            return remoteConfigMap.get(key);
        return defaultConfigMap.get(key);
    }
}
