package com.ljmu.andre.snaptools.Fragments.Tutorials;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SettingsTutorial {
    public static ImmutableList<TutorialDetail> getTutorials() {
        return new ImmutableList.Builder<TutorialDetail>()
                /**
                 * ===========================================================================
                 * Master Switch Description
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.master_switch)
                                .setTitle("Master Switch")
                                .setMessage(
                                        "Using the Master Switch you can disable the entire framework and hooking system without having to reboot your device."
                                )
                )

                /**
                 * ===========================================================================
                 * Update Settings Descriptions
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_check_apk_updates)
                                .setTitle("Check for APK updates")
                                .setMessage(
                                        "Check for new SnapTools APK updates upon opening the app"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_check_pack_updates)
                                .setTitle("Check for active pack updates")
                                .setMessage(
                                        "Check for updates of whichever pack you currently have enabled upon opening the SnapTools app"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_check_pack_updates_sc)
                                .setTitle("Check for active pack updates in Snapchat")
                                .setMessage(
                                        "Check for updates of whichever pack you currently have enabled upon opening Snapchat"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.update_channel_container)
                                .setTitle("Update Channel")
                                .setMessage(
                                        "Select which update channel you would like to participate in"
                                                + "\nBeta: Provides more frequent updates with newer features, however can contain bugs that need to be found"
                                                + "\nRelease: Provides a smoother experience as features have been cleaned up from the Beta channel"
                                )
                )

                /**
                 * ===========================================================================
                 * Misc Settings
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_kill_sc)
                                .setTitle("Auto Kill Snapchat")
                                .setMessage(
                                        "As Snapchat must be fully restarted for any setting changes to take affect, this switch will attempt to force close snapchat (Using root) any time a setting is changed."
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_enable_auto_reporting)
                                .setTitle("Auto Error Reporting")
                                .setMessage(
                                        "SnapTools uses a system to automatically notify the developers of crashes or errors. This system can be entirely disabled and issues will still log to your ErrorLogs folder"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_enable_load_notify)
                                .setTitle("Show Icon on successful launch")
                                .setMessage(
                                        "Display the SnapTools logo at the top of the screen when a pack has successfully injected its code"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_back_opens_menu)
                                .setTitle("Back button opens menu")
                                .setMessage(
                                        "Following the Discord app's functionality, your back button will open the sidebar menu"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.btn_delete_cache)
                                .setTitle("Clear Snapchat Cache")
                                .setMessage(
                                        "Use root to perform a deeper clear of Snapchat's cache and media cache (This will not log you out)"
                                )
                )

                /**
                 * ===========================================================================
                 * Spin Theme Description
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.app_theme_container)
                                .setTitle("SnapTools Themes")
                                .setMessage(
                                        "If you would prefer a different theme for the app, you can select an alternative"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.switch_transition_animations)
                                .setTitle("Show Transition Animations")
                                .setMessage(
                                        "Select whether or not to show animations when opening new pages or refreshing lists"
                                )
                )

                /**
                 * ===========================================================================
                 * Setting Backup/Restore Descriptions
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.settings_backup_container)
                                .setTitle("Settings Backup/Restore")
                                .setMessage(
                                        "Backup your current settings profile or restore other saved profiles"
                                )
                )

                .build();
    }
}
