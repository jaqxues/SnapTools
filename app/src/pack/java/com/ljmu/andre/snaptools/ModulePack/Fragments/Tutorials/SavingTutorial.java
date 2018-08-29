package com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.scrollTo;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingTutorial {
    public static ImmutableList<TutorialDetail> getTutorials() {
        return new ImmutableList.Builder<TutorialDetail>()
                /**
                 * ===========================================================================
                 * Snap Settings
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "saving_settings_tab")))
                                .setTitle("Snap Saving Settings")
                                .setMessage(
                                        "You can customise how you would like each individual Snap to be saved, each with their own independant settings"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "saving_settings_header")))
                                .setTitle("Snap Saving Options")
                                .setMessage(
                                        "There are a wide range of settings to use, experiment a little to find your preference"
                                )
                )

                /**
                 * ===========================================================================
                 * Notification Settings
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "notification_settings_container")))
                                .setTitle("Save Notification Settings")
                                .setMessage(
                                        "SnapTools provides multiple methods of notifying you when a snap has saved or failed."
                                                + "\nYou can select one of those methods and play around with their settings"
                                )
                )

                /**
                 * ===========================================================================
                 * Media Storage Format
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "media_storage_container")))
                                .setTitle("Media Storage Format")
                                .setMessage(
                                        "Snaps can be stored in a number of different file structures"
                                                + "\nThis setting provides you the ability to customise the structure of your saved snaps"
                                )
                )

                /**
                 * ===========================================================================
                 * Media Storage Format
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "media_storage_spinner")))
                                .setTitle("Media Storage Format")
                                .setMessage(
                                        "If you change your Storage Format you will be given the option to scan through your existing snaps and attempt to convert to the new format."
                                                + "\nit must be noted, this is a best effort function and should not be completely relied upon. A manual backup is recommended."
                                )
                )

                /**
                 * ===========================================================================
                 * Select Media Path
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "button_media_path")))
                                .setTitle("Media Storage Path")
                                .setMessage(
                                        "You can specify which folder you would like your media to be saved to. The system will also attempt to move your existing media folder if you so choose."
                                )
                )

                /**
                 * ===========================================================================
                 * Select Folder Names
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> scrollTo(getDSLView(activity, "folder_names_container")))
                                .setTitle("Snap Folder Names")
                                .setMessage(
                                        "You can alter the folder names for each Snap type."
                                                + "\nEnter the desired folder names and then press the 'Apply Folder Names' button"
                                )
                )
                .build();
    }
}
