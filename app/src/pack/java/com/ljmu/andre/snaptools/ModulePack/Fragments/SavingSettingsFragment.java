package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.SavingViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials.SavingTutorial;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.List;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.MEDIA_PATH;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingSettingsFragment extends FragmentHelper {
    private static final int SELECT_MEDIA_DIR_REQUEST = 300;
    private static final List<TutorialDetail> TUTORIAL_DETAILS = SavingTutorial.getTutorials();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_MEDIA_DIR_REQUEST) {
            if (resultCode != RESULT_OK) {
                SafeToast.show(getActivity(), "Cancelled Media folder selection", Toast.LENGTH_LONG, true);
                return;
            }

            List<Uri> selectedMediaPaths = Utils.getSelectedFilesFromResult(data);

            if (selectedMediaPaths.isEmpty()) {
                DialogFactory.createErrorDialog(
                        getActivity(),
                        "Error Changing Media Folder",
                        "No folder has been selected"
                ).show();
                return;
            }

            File mediaDir = Utils.getFileForUri(selectedMediaPaths.get(0));

            Timber.d("New Media Path: " + mediaDir);

            if (!mediaDir.isDirectory()) {
                DialogFactory.createErrorDialog(
                        getActivity(),
                        "Error Changing Media Folder",
                        "Selected location is not a directory!"
                ).show();
                return;
            }

            String currentMediaPath = getPref(MEDIA_PATH);

            if (currentMediaPath.equals(mediaDir.getAbsolutePath())) {
                DialogFactory.createErrorDialog(
                        getActivity(),
                        "Error Changing Media Folder",
                        "Selected same media directory as current"
                ).show();

                return;
            }

            File currentMediaDir = new File(currentMediaPath);

            if (!currentMediaDir.exists()) {
                putAndKill(MEDIA_PATH, mediaDir.getAbsolutePath(), getActivity());
                SafeToast.show(getActivity(), "Successfully changed Media folder", Toast.LENGTH_LONG);
                return;
            }

            DialogFactory.createConfirmation(
                    getActivity(),
                    "Transfer Media Folder?",
                    "Would you like to attempt to move the current Media folder to the new location?"
                            + "\n\nWARNING: This MAY delete all media stored in the target location",
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            if (!currentMediaDir.renameTo(mediaDir)) {
                                DialogFactory.createConfirmation(
                                        getActivity(),
                                        "Issues During Switch",
                                        "Couldn't transfer existing Media to the new destination\n" +
                                                "Would you like to continue using the new folder?",
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                putAndKill(MEDIA_PATH, mediaDir.getAbsolutePath(), getActivity());
                                                themedDialog.dismiss();
                                            }
                                        }
                                ).setDismissable(false).show();
                            } else {
                                putAndKill(MEDIA_PATH, mediaDir.getAbsolutePath(), getActivity());
                                SafeToast.show(getActivity(), "Successfully transferred Media folder", Toast.LENGTH_LONG);
                            }

                            themedDialog.dismiss();
                        }
                    },
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
							/*putAndKill(MEDIA_PATH, mediaDir.getAbsolutePath(), getActivity());
							SafeToast.show(getActivity(), "Successfully changed Media folder", Toast.LENGTH_LONG);*/
                            SafeToast.show(getActivity(), "Cancelled moving Media folder", Toast.LENGTH_LONG, true);
                            themedDialog.dismiss();
                        }
                    }
            ).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup mainContainer = new SavingViewProvider(getActivity()).getMainContainer();

        ResourceUtils.<Button>getDSLView(mainContainer, "button_media_path")
                .setOnClickListener(v -> {
                    Intent i = new Intent(getActivity(), FilePickerActivity.class);
                    // This works if you defined the intent filter
                    // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                    // Set these depending on your use case. These are the defaults.
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

                    // Configure initial directory by specifying a String.
                    // You could specify a String like "/storage/emulated/0/", but that can
                    // dangerous. Always use Android's API calls to get paths to the SD-card or
                    // internal memory.
                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, (String) getPref(MEDIA_PATH));

                    startActivityForResult(i, SELECT_MEDIA_DIR_REQUEST);
                });

        setTutorialDetails(TUTORIAL_DETAILS);

        return mainContainer;
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

    @Override
    public String getName() {
        return "Saving";
    }

    @Override
    public Integer getMenuId() {
        return getName().hashCode();
    }
}
