package com.ljmu.andre.snaptools.Utils;


import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.Translation.Translator.Translation;
import com.ljmu.andre.snaptools.Fragments.HomeFragment;
import com.ljmu.andre.snaptools.Fragments.SettingsFragment;
import com.ljmu.andre.snaptools.MainActivity;
import com.ljmu.andre.snaptools.R;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class TranslationDef extends ConstantDefiner<Translation> {
    /*START*/
    public static final Translation DEFAULT_TRANSLATION_FOUND_TITLE = new Translation(
            "default_translation_found_title",
            "Found Translations"
    );
    public static final Translation DEFAULT_TRANSLATION_FOUND_MESSAGE = new Translation(
            "default_translation_found_message",
            "Automatically found a %s translation file.\nWould you like to use this language?"
    );
    public static final Translation AUTOMATIC_INITIALISATION_SUCCESS = new Translation(
            "automatic_initialisation_success",
            "Automatic Initialisation Successful"
    );
    public static final Translation APPLICATION_INITIALISATION_SUCCESS_TITLE = new Translation(
            "application_initialisation_success_title",
            "Application Initialisation Successful"
    );
    public static final Translation APPLICATION_INITIALISATION_SUCCESS_MSG = new Translation(
            "application_initialisation_success_message",
            "Successfully initialised application, an app restart will be performed now"
    );
    public static final Translation APPLICATION_INITIALISATION_FAILED_TITLE = new Translation(
            "application_initialisation_failed_title",
            "Application Initialisation Failed"
    );
    public static final Translation APPLICATION_INITIALISATION_FAILED_MSG = new Translation(
            "application_initialisation_failed_message",
            "Root access denied, please make sure you check your Root provider (E.g Magisk) and allow SnapTools root access"
    );

    /* Framework & Pack Loading */
    public static final Translation FRAMEWORK_LOAD_ERROR_TITLE = new Translation(
            "framework_load_error_title",
            "Framework Load Error"
    );
    public static final Translation PACK_LOAD_FATAL_ERROR_TITLE = new Translation(
            "pack_load_fatal_error_title",
            "Fatal Error loading ModulePacks"
    );
    public static final Translation PACK_LOAD_FATAL_ERROR_MSG = new Translation(
            "pack_load_fatal_error_message",
            "Fatal error loading module packs!"
    );
    public static final Translation PACK_UPDATE_AVAILABLE_TITLE = new Translation(
            "pack_update_title",
            "New pack update available!"
    );
    /* !!Framework & Pack Loading!! */

    /* Main Activity */
    public static final Translation MASTER_SWITCH_DISABLED = new Translation(
            "master_switch_disabled",
            "Master Switch Is Off: All functions are disabled",
            MainActivity.class,
            R.id.txt_master_switch_disabled
    );
    /* !!Main Activity!! */

    /* Home Fragment */
    public static final Translation HOME_DESCRIPTION = new Translation(
            "home_description",
            "SnapTools is a project aimed to provide Snapchat modifications using the Xposed Framework. We provide new functionality and features to the popular Snapchat application, in a seamless and modular way. " +
                    "\n\nWe use a cloud based download system to allow for straightforward updates as well as support for hotswapping of supported Snapchat versions without the need of a system reboot.",
            HomeFragment.class,
            R.id.txt_description
    );
    /* !!Home Fragment !! */

    /* Settings Fragment */
    public static final Translation MASTER_SWITCH_TITLE = new Translation(
            "master_switch_title",
            "Master Switch",
            SettingsFragment.class,
            R.id.title_master_switch
    );
    public static final Translation MASTER_SWITCH = new Translation(
            "master_switch",
            "Toggle all functions without reboot",
            SettingsFragment.class,
            R.id.master_switch
    );
    public static final Translation UPDATE_SETTINGS_TITLE = new Translation(
            "update_settings_title",
            "Update Settings",
            SettingsFragment.class,
            R.id.title_update_settings
    );
    public static final Translation CHECK_APK_UPDATE_SWITCH = new Translation(
            "check_apk_update_switch",
            "Check for APK updates",
            SettingsFragment.class,
            R.id.switch_check_apk_updates
    );
    public static final Translation CHECK_PACK_UPDATE_SWITCH = new Translation(
            "check_pack_update_switch",
            "Check for active pack updates",
            SettingsFragment.class,
            R.id.switch_check_pack_updates
    );
    public static final Translation CHECK_PACK_UPDATE_SC_SWITCH = new Translation(
            "check_pack_update_sc_switch",
            "Check for active pack updates in Snapchat",
            SettingsFragment.class,
            R.id.switch_check_pack_updates_sc
    );
    public static final Translation UPDATE_CHANNEL_LABEL = new Translation(
            "update_channel_label",
            "Update Channel:",
            SettingsFragment.class,
            R.id.label_update_channel
    );
    public static final Translation FORCE_UPDATE_CHECK_BUTTON = new Translation(
            "force_update_check_button",
            "Force APK Update Check",
            SettingsFragment.class,
            R.id.btn_check_apk_updates
    );
    public static final Translation MISC_SETTINGS_TITLE = new Translation(
            "misc_settings_title",
            "Misc Settings",
            SettingsFragment.class,
            R.id.title_misc_settings
    );
    public static final Translation KILL_SNAPCHAT_SWITCH = new Translation(
            "kill_snapchat_switch",
            "Kill Snapchat on setting change",
            SettingsFragment.class,
            R.id.switch_kill_sc
    );
    public static final Translation ICON_ON_LAUNCH_SWITCH = new Translation(
            "icon_on_launch_switch",
            "Show icon on successful launch",
            SettingsFragment.class,
            R.id.switch_enable_load_notify
    );
    public static final Translation BACK_OPENS_MENU_SWITCH = new Translation(
            "back_opens_menu_switch",
            "Back button opens menu",
            SettingsFragment.class,
            R.id.switch_back_opens_menu
    );
    public static final Translation CLEAR_SNAPCHAT_CACHE_BUTTON = new Translation(
            "clear_snapchat_cache_button",
            "Clear Snapchat Cache",
            SettingsFragment.class,
            R.id.btn_delete_cache
    );
    public static final Translation UI_SETTINGS_TITLE = new Translation(
            "ui_settings_title",
            "UI Settings",
            SettingsFragment.class,
            R.id.title_ui_settings
    );
    public static final Translation APP_THEME_LABEL = new Translation(
            "app_theme_label",
            "App Theme:",
            SettingsFragment.class,
            R.id.label_theme
    );
    public static final Translation TRANSLATION_LABEL = new Translation(
            "translation_label",
            "Translations:",
            SettingsFragment.class,
            R.id.label_translations
    );
    public static final Translation TRANSITION_ANIM_LABEL = new Translation(
            "transition_anim_label",
            "Show transition animations",
            SettingsFragment.class,
            R.id.switch_transition_animations
    );
    public static final Translation BACKUP_RESTORE_TITLE = new Translation(
            "backup_restore_title",
            "Setting Backup/Restore",
            SettingsFragment.class,
            R.id.title_settings_backup
    );
    public static final Translation RESTORE_PROFILE_LABEL = new Translation(
            "restore_profile_label",
            "Restore Profile:",
            SettingsFragment.class,
            R.id.label_restore_profile
    );
    public static final Translation BACKUP_PROFILE_BUTTON = new Translation(
            "backup_profile_button",
            "Backup Settings Profile",
            SettingsFragment.class,
            R.id.btn_backup
    );
    public static final Translation RESET_SETTINGS_BUTTON = new Translation(
            "reset_settings_button",
            "Reset Settings",
            SettingsFragment.class,
            R.id.btn_reset_prefs
    );
    /* !!Settings Fragment!! */
}
