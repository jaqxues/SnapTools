package com.ljmu.andre.snaptools.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.Translation.Translator;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Dialogs.Content.TextInput;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickWrapper;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.MasterSwitchEvent;
import com.ljmu.andre.snaptools.Fragments.Tutorials.SettingsTutorial;
import com.ljmu.andre.snaptools.MainActivity;
import com.ljmu.andre.snaptools.Networking.Helpers.CheckAPKUpdate;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.RedactedClasses.Answers;
import com.ljmu.andre.snaptools.RedactedClasses.CustomEvent;
import com.ljmu.andre.snaptools.Repackaging.RepackageManager;
import com.ljmu.andre.snaptools.UIComponents.Adapters.CenteredArrayAdapter;
import com.ljmu.andre.snaptools.UIComponents.UITheme;
import com.ljmu.andre.snaptools.Utils.BackupRestoreUtils;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.ljmu.andre.snaptools.Utils.ShellUtils;
import com.ljmu.andre.snaptools.Utils.ThemeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.AUTO_ERROR_REPORTING;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.BACKUP_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.BACK_OPENS_MENU;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CHECK_APK_UPDATES;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CHECK_PACK_UPDATES;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CHECK_PACK_UPDATES_SC;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CURRENT_THEME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.KILL_SC_ON_CHANGE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.NOTIFY_ON_LOAD;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_TRANSITION_ANIMATIONS;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SYSTEM_ENABLED;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TEMP_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TRANSLATION_LOCALE;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SettingsFragment extends FragmentHelper {
    Unbinder unbinder;

    @BindView(R.id.master_switch)
    SwitchCompat masterSwitch;
    @BindView(R.id.switch_kill_sc)
    SwitchCompat switchKillSc;
    @BindView(R.id.switch_enable_auto_reporting)
    SwitchCompat switchEnableAutoReporting;
    @BindView(R.id.switch_enable_load_notify)
    SwitchCompat switchEnableLoadNotify;
    @BindView(R.id.switch_back_opens_menu)
    SwitchCompat switchBackOpensMenu;
    @BindView(R.id.switch_transition_animations)
    SwitchCompat switchTransitionAnimations;
    @BindView(R.id.btn_force_repkg)
    Button btnRepackage;
    @BindView(R.id.spin_theme)
    Spinner spinTheme;
    @BindView(R.id.spin_translations)
    Spinner spinTranslations;
    @BindView(R.id.txt_app_version)
    TextView txtAppVersion;
    @BindView(R.id.switch_check_apk_updates)
    SwitchCompat switchCheckApkUpdates;
    @BindView(R.id.switch_check_pack_updates)
    SwitchCompat switchCheckPackUpdates;
    @BindView(R.id.switch_check_pack_updates_sc)
    SwitchCompat switchCheckPackUpdatesSC;
    @BindView(R.id.spinner_update_channel)
    Spinner spinnerUpdateChannel;
    @BindView(R.id.spinner_restore)
    Spinner spinnerRestore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layoutContainer = (LinearLayout) inflater.inflate(R.layout.frag_settings, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);
        Timber.d("Create");

        setTutorialDetails(
                SettingsTutorial.getTutorials()
        );

        initMasterSwitch();
        initUpdateSettingsSwitch();
        initUpdateChannel();
        initKillSCSwitch();
        initAutoErrorReporting();
        initLoadNotify();
        initBackOpensMenu();
        initTransitionAnimations();
        initTheming();
        initTranslations();
        initBackupRestore();
        initRepackaging();
        txtAppVersion.setText(BuildConfig.VERSION_NAME);

        return layoutContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.soleUnregister(this);
    }

    private void initMasterSwitch() {
        masterSwitch.setChecked(getPref(SYSTEM_ENABLED));
        masterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            putAndKill(SYSTEM_ENABLED, isChecked, getActivity());
            EventBus.getInstance().post(new MasterSwitchEvent());

            Answers.safeLogEvent(
                    new CustomEvent("Master Switch Toggle")
                            .putCustomAttribute("Enabled", String.valueOf(isChecked))
            );
        });
    }

    private void initUpdateSettingsSwitch() {
        boolean apkCheckEnabled = getPref(CHECK_APK_UPDATES);
        switchCheckApkUpdates.setChecked(apkCheckEnabled);

        switchCheckApkUpdates.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putAndKill(CHECK_APK_UPDATES, isChecked, getActivity());
                    Answers.safeLogEvent(
                            new CustomEvent("Apk Update Checking")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );

        boolean packCheckEnabled = getPref(CHECK_PACK_UPDATES);
        switchCheckPackUpdates.setChecked(packCheckEnabled);

        switchCheckPackUpdates.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putAndKill(CHECK_PACK_UPDATES, isChecked, getActivity());

                    Answers.safeLogEvent(
                            new CustomEvent("Pack Update Checking")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );

        boolean packCheckSCEnabled = getPref(CHECK_PACK_UPDATES_SC);
        switchCheckPackUpdatesSC.setChecked(packCheckSCEnabled);

        switchCheckPackUpdatesSC.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putAndKill(CHECK_PACK_UPDATES_SC, isChecked, getActivity());

                    Answers.safeLogEvent(
                            new CustomEvent("Pack Update Checking SC")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initUpdateChannel() {
        SpinnerAdapter spinnerAdapter = spinnerUpdateChannel.getAdapter();

        int selectedIndex = 0;
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            String item = (String) spinnerAdapter.getItem(i);
            if (item.equalsIgnoreCase(BuildConfig.FLAVOR))
                selectedIndex = i;
        }

        if (selectedIndex == -1)
            throw new IllegalStateException("UpdateChannel Index Not Found");

        spinnerUpdateChannel.setSelection(selectedIndex, false);

        int finalSelectedIndex = selectedIndex;
        spinnerUpdateChannel.setOnItemSelectedListener(new OnItemSelectedListener() {
            private int lastSelection = finalSelectedIndex;
            private boolean isInternalCall = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == lastSelection)
                    return;
                int previousSelection = lastSelection;
                lastSelection = position;

                if (isInternalCall) {
                    isInternalCall = false;
                    return;
                }

                String selection = (String) parent.getItemAtPosition(position);

                ObjectResultListener<Boolean> selectionResultListener = new ObjectResultListener<Boolean>() {
                    @Override
                    public void success(String message, Boolean object) {
                        if (!object) {
                            isInternalCall = true;
                            spinnerUpdateChannel.setSelection(previousSelection);
                        }
                    }

                    @Override
                    public void error(String message, Throwable t, int errorCode) {
                        isInternalCall = true;
                        spinnerUpdateChannel.setSelection(previousSelection);
                    }
                };

                DialogFactory.createConfirmation(
                        getActivity(),
                        "Download " + selection + " APK?",
                        "Are you sure you would like to start using a " + htmlHighlight(selection) + " build of SnapTools?"
                                + "\n(A restart will be required after installing)",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                CheckAPKUpdate.updateApk(
                                        getActivity(),
                                        "https://github.com/jaqxues/SnapTools_DataProvider/blob/master/Apks/Files/SnapTools-" + selection + ".apk?raw=true",
                                        getPref(TEMP_PATH),
                                        "SnapTools_" + selection + ".apk",
                                        themedDialog,
                                        selectionResultListener
                                );
                            }
                        },
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                selectionResultListener.success(null, false);
                                themedDialog.dismiss();
                            }
                        }
                ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initKillSCSwitch() {
        boolean isEnabled = getPref(KILL_SC_ON_CHANGE);
        switchKillSc.setChecked(isEnabled);

        switchKillSc.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    putPref(KILL_SC_ON_CHANGE, isChecked);

                    if (isChecked)
                        ShellUtils.sendCommand("").subscribe();

                    Answers.safeLogEvent(
                            new CustomEvent("SC Killswitch")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initAutoErrorReporting() {
        boolean isEnabled = getPref(AUTO_ERROR_REPORTING);
        switchEnableAutoReporting.setChecked(isEnabled);

        switchEnableAutoReporting.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putAndKill(AUTO_ERROR_REPORTING, isChecked, getActivity());

                    Answers.safeLogEvent(
                            new CustomEvent("Error Reporting")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initLoadNotify() {
        boolean isEnabled = getPref(NOTIFY_ON_LOAD);
        switchEnableLoadNotify.setChecked(isEnabled);

        switchEnableLoadNotify.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putAndKill(NOTIFY_ON_LOAD, isChecked, getActivity());

                    Answers.safeLogEvent(
                            new CustomEvent("Load Notifying")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initBackOpensMenu() {
        boolean isEnabled = getPref(BACK_OPENS_MENU);
        switchBackOpensMenu.setChecked(isEnabled);

        switchBackOpensMenu.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putPref(BACK_OPENS_MENU, isChecked);

                    Answers.safeLogEvent(
                            new CustomEvent("Back Opens Menu")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initTransitionAnimations() {
        boolean isEnabled = getPref(SHOW_TRANSITION_ANIMATIONS);
        switchTransitionAnimations.setChecked(isEnabled);

        switchTransitionAnimations.setOnCheckedChangeListener(
                (buttonView, isChecked)
                        -> {
                    putPref(SHOW_TRANSITION_ANIMATIONS, isChecked);

                    Answers.safeLogEvent(
                            new CustomEvent("Transition Animations")
                                    .putCustomAttribute("Enabled", String.valueOf(isChecked))
                    );
                }
        );
    }

    private void initTheming() {
        String theme = getPref(CURRENT_THEME);
        UITheme ut = UITheme.getMatchingThemeFromName(theme);
        spinTheme.setSelection(ut.getID());
        int currentPosition = spinTheme.getSelectedItemPosition();
        spinTheme.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentPosition) {
                    return;
                }
                UITheme selectedTheme = UITheme.getMatchingThemeByID(position);
                SafeToast.show(
                        getActivity(),
                        "Changing theme to "
                                + selectedTheme.getName(),
                        Toast.LENGTH_SHORT
                );

                putPref(CURRENT_THEME, selectedTheme.getName());

                ThemeUtils.changeToTheme(
                        getActivity(),
                        selectedTheme.getTheme()
                );

                Answers.safeLogEvent(
                        new CustomEvent("Theme Changed")
                                .putCustomAttribute("Theme", selectedTheme.getName())
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void initTranslations() {
        String selectedTranslation = getPref(TRANSLATION_LOCALE);
        List<String> translations = Translator.getAvailableTranslations();
        int selectedIndex = translations.indexOf(selectedTranslation);

        Timber.d("TranslationFiles: " + translations);

        ArrayAdapter<String> adapter = new CenteredArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, translations);

        spinTranslations.setOnItemSelectedListener(null);
        spinTranslations.setAdapter(adapter);
        spinTranslations.setSelection(selectedIndex, false);

        spinTranslations.setOnItemSelectedListener(new OnItemSelectedListener() {
            private int currentSelection = selectedIndex;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentSelection == position) {
                    return;
                }

                currentSelection = position;
                String translation = (String) parent.getItemAtPosition(position);
                Timber.d("Selected Translation: " + translation);
                putPref(TRANSLATION_LOCALE, translation);

                ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                        getActivity(),
                        "Fetching Translation",
                        "Translation is being downloaded and applied"
                );

                progressDialog.show();

                Translator.init(
                        getActivity(),
                        translation + ".xml",
                        success -> {
                            progressDialog.dismiss();

                            if (success) {
                                DialogFactory.createConfirmation(
                                        getActivity(),
                                        "Requires Restart",
                                        "Translations require SnapTools to restart."
                                                + "\nPerform restart now?",
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();
                                                Translator.reset();
                                                getActivity().finish();
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                            }
                                        }
                                ).show();
                            } else {
                                SafeToast.show(getActivity(), "Failed to fetch translations", Toast.LENGTH_LONG);
                            }
                        }
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initBackupRestore() {
        List<String> restoreValues = new ArrayList<>();
        restoreValues.add("Current");

        File backupDir = getCreateDir(BACKUP_PATH);

        /**
         * ===========================================================================
         * Load the restore point filenames
         * ===========================================================================
         */
        if (backupDir == null || !backupDir.exists()) {
            Timber.w("Backup directory couldn't be created");
        } else {
            // ===========================================================================
            File[] backupFiles = backupDir.listFiles((file, s) -> s.startsWith("SettingsBackup_"));
            if (backupFiles != null) {
                for (File backup : backupFiles) {
                    restoreValues.add(
                            backup.getName()
                                    .replace("SettingsBackup_", "")
                                    .replace(".json", "")
                    );
                }
            }
            // ===========================================================================
        }

        ArrayAdapter<String> adapter = new CenteredArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, restoreValues);
        spinnerRestore.setAdapter(adapter);

        spinnerRestore.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }

                String profile = (String) parent.getItemAtPosition(position);

                DialogFactory.createConfirmation(
                        getActivity(),
                        "Confirm Restore",
                        "Are you sure you wish to restore the settings profile: " + htmlHighlight(profile),
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                themedDialog.dismiss();
                                boolean success = BackupRestoreUtils.restoreProfile(profile);

                                if (!success) {
                                    SafeToast.show(
                                            getActivity(),
                                            "Failed to restore settings profile",
                                            true
                                    );

                                    return;
                                }

                                Preferences.loadPreferenceMap();

                                if (getPref(KILL_SC_ON_CHANGE))
                                    PackUtils.killSCService(getActivity());

                                MiscUtils.restartActivity(getActivity());
                            }
                        },
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                spinnerRestore.setSelection(0);
                                themedDialog.dismiss();
                            }
                        }
                ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initRepackaging() {
        if (getPref(REPACKAGE_NAME) == null) {
            btnRepackage.setOnClickListener(v ->
                    RepackageManager.askUserDialog(getActivity()).show());
        } else
            btnRepackage.setVisibility(View.GONE);
    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public Integer getMenuId() {
        return R.id.nav_settings;
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

    @OnClick(R.id.btn_check_apk_updates)
    public void onCheckApkUpdatesClicked() {
        CheckAPKUpdate.checkApkUpdate(getActivity(), true);
    }

    @OnClick(R.id.btn_delete_cache)
    public void onDeleteMediaCacheClicked() {
        File dataDir = getDataDir();

        if (dataDir == null) {
            SafeToast.show(
                    getActivity(),
                    "Couldn't determine the data directory location",
                    Toast.LENGTH_LONG,
                    true
            );
            return;
        }

        File mediaCacheDir = new File(dataDir.getAbsolutePath() + "/com.snapchat.android/files/media_cache/");
        File cacheDir = new File(dataDir.getAbsolutePath() + "/com.snapchat.android/cache/");

        if (!cacheDir.exists() && !mediaCacheDir.exists()) {
            SafeToast.show(
                    getActivity(),
                    "Cache doesn't exist",
                    Toast.LENGTH_LONG,
                    true
            );
            return;
        }

        DialogFactory.createConfirmation(
                getActivity(),
                "Clear Snapchat Cache and MediaCache?",
                "By pressing YES you will trigger a ROOT command that will clear all data from Snapchat's Cache (Including MediaCache)"
                        + "\n\nCache Location: " + htmlHighlight(cacheDir.getAbsolutePath())
                        + "\n\nMediaCache Location: " + htmlHighlight(mediaCacheDir.getAbsolutePath())
                        + "\n\n^If these are wrong, do not continue^",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();

                        PackUtils.killSCService(getActivity());

                        ShellUtils.sendCommand(
                                "rm -r \"" + mediaCacheDir.getAbsolutePath() + "\""
                                        + "\nrm -r \"" + cacheDir.getAbsolutePath() + "\""
                        ).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SimpleObserver<Boolean>() {
                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        if (mediaCacheDir.exists())
                                            aBoolean = false;

                                        if (aBoolean) {
                                            SafeToast.show(
                                                    getActivity(),
                                                    "Successfully cleared the Cache",
                                                    Toast.LENGTH_LONG
                                            );
                                        } else {
                                            SafeToast.show(
                                                    getActivity(),
                                                    "Failed to execute command",
                                                    Toast.LENGTH_LONG,
                                                    true
                                            );
                                        }
                                    }
                                });
                    }
                }
        ).show();
    }

    @Nullable
    private File getDataDir() {
        String snaptoolsDataDirString = getActivity().getApplicationInfo().dataDir;

        if (snaptoolsDataDirString == null)
            return null;

        File snaptoolsDataDir = new File(snaptoolsDataDirString);
        return snaptoolsDataDir.getParentFile();
    }

    @OnClick(R.id.btn_backup)
    public void onBackupSettings() {
        new ThemedDialog(getActivity())
                .setTitle("Settings Backup")
                .setExtension(
                        new TextInput()
                                .setMessage(
                                        "Backup your current settings profile?"
                                                + "\n" + htmlHighlight("Filename:")
                                )
                                .setInputMessage(BackupRestoreUtils.getFreshFilenameNoExt())
                                .setYesClickListener(
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();

                                                TextInput textInput = themedDialog.getExtension();
                                                boolean success = BackupRestoreUtils.backupCurrentProfile(textInput.getInputMessage());

                                                if (!success) {
                                                    SafeToast.show(
                                                            getActivity(),
                                                            "Failed to backup settings profile",
                                                            true
                                                    );
                                                } else {
                                                    SafeToast.show(
                                                            getActivity(),
                                                            "Backed up settings profile",
                                                            false
                                                    );
                                                }
                                            }
                                        }
                                )
                ).show();
    }

    @OnClick(R.id.btn_reset_prefs)
    public void onResetSettings() {
        Observable<Boolean> initialMessageObservable = Observable.create(
                e -> DialogFactory.createConfirmation(
                        getActivity(),
                        "Reset Settings?",
                        "Resetting your settings is an irreversible action and will log out your SnapTools account as well as reset all of your changes.",
                        new ThemedClickWrapper(themedDialog -> {
                            themedDialog.dismiss();
                            e.onNext(true);
                            e.onComplete();
                        }),
                        new ThemedClickWrapper(themedDialog -> {
                            themedDialog.dismiss();
                            e.onNext(false);
                            e.onComplete();
                        })
                ).show());

        Observable<Boolean> confirmationObservable = Observable.create(
                e -> DialogFactory.createConfirmation(
                        getActivity(),
                        "Confirm Action",
                        "Are you sure?",
                        new ThemedClickWrapper(themedDialog -> {
                            themedDialog.dismiss();
                            e.onNext(true);
                            e.onComplete();
                        }),
                        new ThemedClickWrapper(themedDialog -> {
                            themedDialog.dismiss();
                            e.onNext(false);
                            e.onComplete();
                        })
                ).show());


        initialMessageObservable
                .flatMap(aBoolean -> {
                    if (!aBoolean)
                        return Observable.just(false);

                    return confirmationObservable;
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            SafeToast.show(
                                    getActivity(),
                                    "Not resetting settings.",
                                    false
                            );

                            return;
                        }

                        Preferences.forceReset();
                        SafeToast.show(
                                getActivity(),
                                "Settings reset, it is recommended to restart SnapTools.",
                                false
                        );
                    }
                });
    }
}
