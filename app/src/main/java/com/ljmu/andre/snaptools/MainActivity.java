package com.ljmu.andre.snaptools;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.Translation.Translator;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Dialogs.Content.FrameworkLoadError;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.BannerUpdateEvent;
import com.ljmu.andre.snaptools.EventBus.Events.LoadPackSettingsEvent;
import com.ljmu.andre.snaptools.EventBus.Events.MasterSwitchEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ModuleEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackLoadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackUnloadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ReqCheckApkUpdateEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ReqLoadFragmentEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ShopPurchaseEvent;
import com.ljmu.andre.snaptools.EventBus.Events.TutorialFinishedEvent;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Fragments.HomeFragment;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.Networking.Helpers.CheckAPKUpdate;
import com.ljmu.andre.snaptools.RedactedClasses.Answers;
import com.ljmu.andre.snaptools.RedactedClasses.CustomEvent;
import com.ljmu.andre.snaptools.Repackaging.RepackageManager;
import com.ljmu.andre.snaptools.UIComponents.CustomNavigation;
import com.ljmu.andre.snaptools.UIComponents.CustomNavigation.NavigationFragmentListener;
import com.ljmu.andre.snaptools.UIComponents.UITheme;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.CustomObservers.ErrorObserver;
import com.ljmu.andre.snaptools.Utils.DeviceIdManager;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.ModuleChecker;
import com.ljmu.andre.snaptools.Utils.RemoteConfig;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.ljmu.andre.snaptools.Utils.ShowcaseFactory;
import com.ljmu.andre.snaptools.Utils.ThemeUtils;
import com.ljmu.andre.snaptools.Utils.TranslationDef;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ljmu.andre.GsonPreferences.Preferences.getExternalPath;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.Translation.Translator.translate;
import static com.ljmu.andre.snaptools.Utils.Constants.APK_CHECK_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.Constants.REMIND_TUTORIAL_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.BACK_OPENS_MENU;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CHECK_APK_UPDATES;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CURRENT_THEME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.HAS_SHOWN_REPKG_DIALOG;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_APK_UPDATE_CHECK;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_SHOP;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_OPEN_APP;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LATEST_APK_VERSION_CODE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_TUTORIAL;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SYSTEM_ENABLED;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TRANSLATION_LOCALE;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.addRelativeParamRule;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.AUTOMATIC_INITIALISATION_SUCCESS;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.DEFAULT_TRANSLATION_FOUND_MESSAGE;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.DEFAULT_TRANSLATION_FOUND_TITLE;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.FRAMEWORK_LOAD_ERROR_TITLE;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.PACK_LOAD_FATAL_ERROR_MSG;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.PACK_LOAD_FATAL_ERROR_TITLE;

public class MainActivity
        extends AppCompatActivity
        implements NavigationFragmentListener {

    // ===========================================================================

    private static final String[] PERMISSION_REQUESTS = {
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.KILL_BACKGROUND_PROCESSES,
            permission.INTERNET,
            permission.VIBRATE,
            permission.RECEIVE_BOOT_COMPLETED
    };

    // ===========================================================================

    private static final List<Integer> TUTORIAL_FRAGMENTS = new ImmutableList.Builder<Integer>()
            .add(R.id.nav_home)
            .add(R.id.nav_pack_manager)
            .add(R.id.nav_settings)
            .build();

    // ===========================================================================

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_navigation_view)
    CustomNavigation navigationView;
    @BindView(R.id.txt_mod_inactive)
    TextView txtModInactive;
    @BindView(R.id.txt_virtual_x)
    TextView txtVirtualXposed;
    @BindView(R.id.txt_banner_apk_update)
    TextView txtBannerApkUpdate;
    @BindView(R.id.txt_banner_pack_update)
    TextView txtBannerPackUpdate;
    @BindView(R.id.btn_tutorial)
    ImageButton btnTutorial;
    @BindView(R.id.txt_master_switch_disabled)
    TextView txtMasterSwitchDisabled;
    @BindView(R.id.konfetti)
    KonfettiView konfettiView;

    // ===========================================================================

    private Unbinder unbinder;
    private boolean isInitialised;
    private UITheme currentTheme;

    // ===========================================================================

    /**
     * ===========================================================================
     * Set the view and check for permissions before initialising
     * ===========================================================================
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);

        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        ContextHelper.set(getApplicationContext());
        ContextHelper.setActivity(this);

        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (handlePermissions())
            initialiseApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("Destroyed");
        unbinder.unbind();
        EventBus.soleUnregister(this);
//		Crashlytics.dispose();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    /**
     * ===========================================================================
     * Check for necessary Permissions -> Perform request on missing perms
     * ===========================================================================
     */
    private boolean handlePermissions() {
        for (String permission : PERMISSION_REQUESTS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                // Ask the user if they wish to resolve missing permissions
                DialogFactory.createBasicMessage(
                        this,
                        getString(R.string.permission_request_title),
                        getString(R.string.permission_request_msg),
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                ActivityCompat.requestPermissions(
                                        MainActivity.this,
                                        PERMISSION_REQUESTS,
                                        1
                                );

                                themedDialog.dismiss();
                            }
                        }
                ).setDismissable(false).show();

                return false;
            }
        }

        return true;
    }

    /**
     * ===========================================================================
     * After permissions are granted -> Initialise the App
     * ===========================================================================
     */
    private void initialiseApplication() {
        if (isInitialised)
            return;

        isInitialised = true;


        /**
         * ===========================================================================
         * Preference System Initialisation
         * ===========================================================================
         */
        try {
            Preferences.init(
                    getExternalPath() + "/" + STApplication.MODULE_TAG + "/"
            );
        } catch (Exception e) {
            Timber.e(e);

            DialogFactory.createErrorDialog(
                    this,
                    "Error Initialising Preferences",
                    "Preference system not loaded. The reason is likely to be permission issues. The application will terminate to preserve data integrity"
                            + "\n\n"
                            + "Reason: " + e.getMessage(),
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            finish();
                        }
                    }
            ).setDismissable(false).show();


            return;
        }

        /**
         * ==========================================================================
         * Application Repackaging requires to update {@link STApplication#PACKAGE}
         * with the actual PackageName
         * ==========================================================================
         */
        // Assign the actual PackageName
        STApplication.PACKAGE = getPackageName();

        /**
         * ===========================================================================
         * Translation System Initialisation
         * ===========================================================================
         */
        try {
            String savedLocaleString = getPref(TRANSLATION_LOCALE);
            boolean hasSavedLocale = savedLocaleString != null;

            Timber.d("Saved language locale: "
                    + savedLocaleString);

            if (!hasSavedLocale) {
                savedLocaleString = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
                Timber.d("Saved language not found... Defaulting to %s.", savedLocaleString);
            }
            if (!Translator.getAvailableTranslations().contains(savedLocaleString)) {
                Timber.d("Could not find Translation for %s. Defaulting to English", savedLocaleString);
                savedLocaleString = "English";
            }

            /**
             * ===========================================================================
             * If saved translation mode is not english, load the translations
             * ===========================================================================
             */
            if (!savedLocaleString.equals("English")) {
                Translator.initTranslationDefinitions(this, new TranslationDef(), true);
                String finalSavedLocaleString = savedLocaleString;
                Translator.init(
                        this,
                        savedLocaleString + ".xml",
                        success -> {
                            if (!success) {
                                SafeToast.show(
                                        this,
                                        "Failed to fetch translations",
                                        true
                                );

                                if (!hasSavedLocale) {
                                    putPref(TRANSLATION_LOCALE, Locale.ENGLISH.getDisplayLanguage());
                                }

                                return;
                            }

                            if (!hasSavedLocale) {
                                DialogFactory.createConfirmation(
                                        this,
                                        translate(DEFAULT_TRANSLATION_FOUND_TITLE),
                                        String.format(translate(DEFAULT_TRANSLATION_FOUND_MESSAGE),
                                                finalSavedLocaleString)
                                                + "\n\n"
                                                + String.format(DEFAULT_TRANSLATION_FOUND_MESSAGE.getText(),
                                                finalSavedLocaleString),
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();
                                                putPref(TRANSLATION_LOCALE, finalSavedLocaleString);
                                            }
                                        },
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();
                                                putPref(TRANSLATION_LOCALE, Locale.ENGLISH.getDisplayLanguage());
                                            }
                                        }
                                ).show();
                            }
                        }
                );
            }
        } catch (Throwable t) {
            Timber.e(t, "Couldn't load translation system");

            DialogFactory.createErrorDialog(
                    this,
                    "Error Loading Translations",
                    "An unknown error occurred while loading the translation system"
            ).show();
        }

        // ===========================================================================

        /**
         * ===========================================================================
         * Fabric Setup
         * ===========================================================================
         */
//		Fabric.with(this, this, new Crashlytics());
//		String email = getPref(ST_EMAIL);
//		if (email != null)
//			Crashlytics.setUserEmail(email);
//
//		String displayName = getPref(ST_DISPLAY_NAME_OBFUS);
//		if (displayName != null)
//			Crashlytics.setUserName(displayName);
//
//		Set<String> selectedPacks = getPref(SELECTED_PACKS);
//		Crashlytics.setString("Selected Packs",
//				String.valueOf(selectedPacks));
//		Crashlytics.setString("User", email);


        RemoteConfig.init(this, remoteConfig -> {
            Translator.fetchedRemoteConfig(this, remoteConfig);
            Constants.initConstants(remoteConfig);
        });

        /**
         * ===========================================================================
         * Device ID Initialisation
         * ===========================================================================
         */
        if (!DeviceIdManager.isSystemIdAssigned()) {
            if (!DeviceIdManager.assignSystemId(this)) {
                return;
            } else {
                SafeToast.show(
                        this,
                        translate(AUTOMATIC_INITIALISATION_SUCCESS)
                );
            }
        }

        /**
         * ===========================================================================
         *  App Theming Preferences Check
         * ===========================================================================
         */
        currentTheme = UITheme.getMatchingThemeFromName(getPref(CURRENT_THEME));
        ThemeUtils.cTheme = currentTheme.getTheme();
        ThemeUtils.onActivityCreateSetTheme(this);
        findViewById(R.id.main_activity).setBackgroundResource(currentTheme.getColorBackgroundID());


        /**
         * ===========================================================================
         * Bindings
         * ===========================================================================
         */
        ButterKnife.bind(this);
        CacheDatabase.init(this);
        EventBus.getInstance().register(this);

        // ===========================================================================

        /**
         * ===========================================================================
         * Toolbar Initialisation
         * ===========================================================================
         */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.init(this);
        actionBarDrawerToggle.syncState();

        // ===========================================================================

        /**
         * ===========================================================================
         * Initiate the Banner Headers
         * ===========================================================================
         */
        initBanners();

        // ===========================================================================


        /**
         * ===========================================================================
         * Initialise Security and Load Module Packs
         * ===========================================================================
         */
        try {
//			Security.init(getResources());

            // ===========================================================================
            List<PackLoadState> packLoadStates = FrameworkManager.loadAllModulePacks(this);
            // ===========================================================================

            boolean hasFailed = false;
            for (PackLoadState loadState : packLoadStates) {
                if (loadState.hasFailed())
                    hasFailed = true;
            }

            if (hasFailed) {
                new ThemedDialog(this)
                        .setTitle(translate(FRAMEWORK_LOAD_ERROR_TITLE))
                        .setHeaderDrawable(R.drawable.error_header)
                        .setExtension(new FrameworkLoadError(packLoadStates))
                        .show();
            }
        } catch (Throwable t) {
            Timber.wtf(t, "Fatal error loading packs");
            DialogFactory.createErrorDialog(
                    this,
                    translate(PACK_LOAD_FATAL_ERROR_TITLE),
                    translate(PACK_LOAD_FATAL_ERROR_MSG)
                            + "\n\n" + t.getMessage()
            ).show();
        }

        FrameworkManager.checkPacksForUpdate(this);

        // ===========================================================================

        /**
         * ===========================================================================
         * Less important functions such as setting applications
         * ===========================================================================
         */
        initApkUpdateChecker();
        checkForSnapchatBeta();
        initReminders();
        Translator.translateActivity(this);
        initAppRepackaging();
    }

    /**
     * ===========================================================================
     * Set the initial state of the page banners
     * ===========================================================================
     */
    private void initBanners() {
        //noinspection ConstantConditions=
        txtModInactive.setVisibility(ModuleChecker.isModuleActive() ? GONE : VISIBLE);
        txtVirtualXposed.setVisibility(System.getProperty("vxp") == null ? GONE : VISIBLE);
        // Trigger the master switch visibility check ================================
        handleMasterSwitchEvent(new MasterSwitchEvent());
        handleBannerEvent(new BannerUpdateEvent(BannerUpdateEvent.MASTER_SWITCH));
        handleBannerEvent(new BannerUpdateEvent(BannerUpdateEvent.APK_UPDATE));
        handleBannerEvent(new BannerUpdateEvent(BannerUpdateEvent.PACK_UPDATE));
    }

    /**
     * ===========================================================================
     * Begin checking if an APK update has been released
     * ===========================================================================
     */
    private void initApkUpdateChecker() {
        if (!(boolean) getPref(CHECK_APK_UPDATES))
            return;

        if (MiscUtils.calcTimeDiff(getPref(LAST_APK_UPDATE_CHECK)) > APK_CHECK_COOLDOWN)
            CheckAPKUpdate.checkApkUpdate(this, false);
    }

    /**
     * ===========================================================================
     * Snapchat Beta versions are not currently supported. As a result, when
     * launching SnapTools with a Snapchat Beta version installed, it will
     * display an appropriate message.
     * <p>
     * This function can be controlled via the Firebase Remote Config.
     * ===========================================================================
     */
    private void checkForSnapchatBeta() {

        String installedSCVersion = MiscUtils.getInstalledSCVer(this);

        if (installedSCVersion == null)
            return;

        if (installedSCVersion.toLowerCase().contains("beta")) {
            DialogFactory.createErrorDialog(
                    this,
                    "Snapchat Beta Detected",
                    "Snapchat Beta version detected ("
                            + installedSCVersion + ")"
                            + "\nSnapTools will " + htmlHighlight("NEVER") + " support a Beta version of Snapchat as we require a stable base to work from"
            ).show();
        }
    }

    /**
     * ===========================================================================
     * Display a reminder about viewing page tutorials if they have been
     * inactive for longer than {@link Constants#REMIND_TUTORIAL_COOLDOWN}
     * ===========================================================================
     */
    private void initReminders() {
        long lastOpenedApp = getPref(LAST_OPEN_APP);

        // Already showing tutorial, don't remind about tutorial =====================
        if (getPref(SHOW_TUTORIAL)) {
            return;
        }

        if (MiscUtils.calcTimeDiff(lastOpenedApp) > REMIND_TUTORIAL_COOLDOWN) {
            ShowcaseFactory.detatchCurrentShowcase(this);

            ShowcaseFactory.getDefaultShowcase(this, (showCaseView, messageContainer, txtTitle, txtMessage, btnClose, btnNext) -> {
                addRelativeParamRule(messageContainer, RelativeLayout.CENTER_VERTICAL);

                txtTitle.setText("Tutorial Reminder");
                txtMessage.setText(
                        "Remember, you can tap the ? button on the top right of certain menus to get a tutorial for that screen including some useful information about the features it has."
                                + "\n\n" +
                                "Or you can long press it on the Home page to get a full basic app tutorial!"
                );

                btnClose.setVisibility(GONE);

                btnNext.setText("Okay");
                btnNext.setOnClickListener(v -> showCaseView.hide());
            }).focusOn(getView(this, R.id.btn_tutorial)).build().show();
        }

        putPref(LAST_OPEN_APP, System.currentTimeMillis());
    }

    /**
     * ===========================================================================
     * Handles App Repackaging (Incorrect preferences due to re-installations...)
     * ===========================================================================
     */
    private void initAppRepackaging() {
        // During re-installations or updates, the PackageName gets reset.
        // Check if the REPACKAGE_NAME Preference is correct
        String repkgNamePref = getPref(REPACKAGE_NAME);
        boolean isDefaultPkg = STApplication.PACKAGE.equals(MainActivity.class.getPackage().getName());
        boolean isPrefCorrect = repkgNamePref == null ? isDefaultPkg : STApplication.PACKAGE.equals(repkgNamePref);

        // if the Preference was not correct, update the Preference
        if (isDefaultPkg) {
            if (!(boolean) getPref(HAS_SHOWN_REPKG_DIALOG) || !isPrefCorrect) {
                Timber.d("Ask for App Repackaging");
                putPref(HAS_SHOWN_REPKG_DIALOG, true);
                RepackageManager.askUserDialog(this).show();
            }
        }

        // An old preference, usually from a previously installed SnapTools version
        // Update corrupt preference and check for duplicate --> uninstall duplicate
        if (!isPrefCorrect) {
            triggerKonfetti();
            SafeToast.show(
                    this,
                    "Successfully repackaged SnapTools"
            );
            Timber.d("User probably updated or re-installed the Application. Updating REPACKAGE_NAME Preference value.");
            putPref(REPACKAGE_NAME, (isDefaultPkg ? null : STApplication.PACKAGE));
            // This is probably caused by an update or re-installation
            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        repkgNamePref == null ?
                                MainActivity.class.getPackage().getName() :
                                repkgNamePref,
                        0
                );
                if (info != null) {
                    Timber.w("Found an installed ST duplicate, asking user to uninstall");
                    RepackageManager.getUninstallDuplicates(
                            this,
                            info
                    ).show();
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
    }

    /**
     * ===========================================================================
     * EventBus subscription to listen for master switch changes
     * ===========================================================================
     */
    @Subscribe
    public void handleMasterSwitchEvent(MasterSwitchEvent switchEvent) {
        if (txtModInactive.getVisibility() == View.VISIBLE) {
            txtMasterSwitchDisabled.setVisibility(GONE);
            return;
        }

        txtMasterSwitchDisabled.setVisibility(
                getPref(SYSTEM_ENABLED) ? GONE : VISIBLE
        );
    }

    /**
     * ===========================================================================
     * EventBus subscription to listen for Banner events.
     * E.g. Apk Update, Master Switch toggle, etc
     * ===========================================================================
     */
    @Subscribe
    public void handleBannerEvent(BannerUpdateEvent bannerEvent) {
        switch (bannerEvent.eventType) {
            case BannerUpdateEvent.MASTER_SWITCH:
                if (txtModInactive.getVisibility() == View.VISIBLE) {
                    txtMasterSwitchDisabled.setVisibility(GONE);
                    return;
                }

                txtMasterSwitchDisabled.setVisibility(
                        getPref(SYSTEM_ENABLED) ? GONE : VISIBLE
                );

                break;
            case BannerUpdateEvent.APK_UPDATE:
                txtBannerApkUpdate.setVisibility(
                        (int) getPref(LATEST_APK_VERSION_CODE) <= BuildConfig.VERSION_CODE ? GONE : VISIBLE
                );

                txtBannerApkUpdate.setOnClickListener(v -> CheckAPKUpdate.checkApkUpdate(this, true, true));

                break;
//			case BannerEvent.PACK_UPDATE:
//				txtBannerPackUpdate.setVisibility(
//						(int) getPref(LATEST_PACK_VERSION_NAME) <=  ? GONE : VISIBLE
//				);
//
//				break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Timber.d("Result: [Request: %s][Result: %s][Data: %s]", requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            if (navigationView != null) {
                FragmentHelper activeFragment = navigationView.getActiveFragment();

                if (activeFragment != null && activeFragment.onBackPressed()) {
                    return;
                }
            }
            if ((boolean) getPref(BACK_OPENS_MENU) && !drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.openDrawer(Gravity.START);
                return;
            }

            super.onBackPressed();
        } catch (IllegalArgumentException e) {
            Timber.w(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                DialogFactory.createErrorDialog(
                        this,
                        "Permission Request Denied",
                        "As you've declined a vital permission request the application will terminate",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                themedDialog.dismiss();
                                finish();
                            }
                        }
                ).setDismissable(false).show();

                return;
            }
        }

        initialiseApplication();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && drawerLayout != null && !(boolean) getPref(BACK_OPENS_MENU)) {
            drawerLayout.openDrawer(Gravity.START);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void moveToMenu(@IdRes int menuId) {
        if (navigationView == null) {
            Timber.w("Can't move to menu %s. Navigation View is null",
                    menuId);
            return;
        }

        navigationView.onNavigationItemSelected(menuId);
    }

    @Override
    public void fragmentSelected(FragmentHelper fragment) {
        drawerLayout.closeDrawers();
        replaceFragmentContainer(fragment);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setSubtitle(fragment.getName());

        btnTutorial.setVisibility(fragment.hasTutorial() ? View.VISIBLE : GONE);
    }

    private void replaceFragmentContainer(FragmentHelper newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleReqCheckApkUpdateEvent(ReqCheckApkUpdateEvent updateEvent) {
        CheckAPKUpdate.checkApkUpdate(this, false);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleShopPurchaseEvent(ShopPurchaseEvent shopPurchaseEvent) {
        Observable.create(
                e -> {
                    Timber.d("NEW ITEM PURCHASE");
                    putPref(LAST_CHECK_SHOP, 0L);
                    navigationView.onNavigationItemSelected(R.id.nav_shop);

                    if (shopPurchaseEvent.getState()) {
                        DialogFactory.createBasicMessage(
                                this,
                                shopPurchaseEvent.getTitle(),
                                shopPurchaseEvent.getMessage()
                        ).show();
                    } else {
                        DialogFactory.createErrorDialog(
                                this,
                                shopPurchaseEvent.getTitle(),
                                shopPurchaseEvent.getMessage()
                        ).show();
                    }

                    e.onComplete();
                }
        ).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<>());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handlePackLoadEvent(PackLoadEvent loadEvent) {
        try {
            ModulePack modulePack = loadEvent.getModulePack();

            if (navigationView == null) {
                Timber.w("Navigation view doesn't seem to exist");
                return;
            }

            Menu navMenu = navigationView.getMenu();

            Observable.create((ObservableOnSubscribe<Void>) e -> {
                if (modulePack == null) {
                    e.onComplete();
                    return;
                }

                Timber.d("Creating menu for %s",
                        modulePack.getPackName());

                String menuName = "Pack: "
                        + modulePack.getPackDisplayName();
                int menuId = getIdFromString(menuName);

                if (navMenu.findItem(menuId) != null)
                    navMenu.removeGroup(menuId);

                SubMenu packSubMenu = navMenu.addSubMenu(
                        menuId,
                        menuId,
                        Menu.NONE,
                        menuName
                );

                FragmentHelper[] staticFragments = modulePack.getStaticFragments();

                if (staticFragments != null) {
                    for (FragmentHelper staticFragment : staticFragments) {
                        packSubMenu.add(menuId, staticFragment.getMenuId(), Menu.NONE, staticFragment.getName());
                        navigationView.addFragment(staticFragment);
                    }
                }

                for (Module module : modulePack.getModules()) {
                    FragmentHelper[] moduleFragments;

                    try {
                        moduleFragments = module.getUIFragments();
                    } catch (Throwable t) {
                        Timber.w(t, "Incompatible modulepack");
                        SafeToast.show(this, "Current ModulePack not supported",
                                Toast.LENGTH_LONG, true);
                        e.onComplete();
                        return;
                    }

                    if (moduleFragments == null)
                        continue;

                    for (FragmentHelper fragment : moduleFragments) {
                        if (fragment == null)
                            continue;

                        packSubMenu.add(
                                Menu.NONE,
                                fragment.getMenuId(),
                                Menu.NONE,
                                fragment.getName()
                        );

                        navigationView.addFragment(fragment);
                    }

                }
            }).subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ErrorObserver<>("Error adding pack to menu"));
        } catch (Throwable t) {
            Timber.e(t, "Issue handling pack load event");
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handlePackUnloadEvent(PackUnloadEvent packUnloadEvent) {
        LocalPackMetaData packMetaData = packUnloadEvent.getPackMetaData();

        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if (item.getTitle().equals("Pack: "
                    + packMetaData.getDisplayName())) {
                item.getSubMenu().clear();
                navigationView.removeFragment(item.getItemId());
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleModuleEventRequest(ModuleEventRequest eventRequest) {
        Menu navMenu = navigationView.getMenu();

        if (navMenu == null) {
            Timber.w("Null NavMenu on module event");
            return;
        }

        ModulePack pack = FrameworkManager.getModulePack(eventRequest.getPackName());
        if (pack == null) {
            Timber.w("Pack not found... Not scanning fragments");
            return;
        }

        Module module = pack.getModule(eventRequest.getModuleName());
        if (module == null) {
            Timber.w("Module not found... Not scanning fragments");
            return;
        }

        String menuName = "Pack: "
                + pack.getPackDisplayName();
        int menuId = getIdFromString(menuName);

        SubMenu packSubMenu = navMenu.findItem(menuId).getSubMenu();

        FragmentHelper[] moduleFragments;

        try {
            moduleFragments = module.getUIFragments();
        } catch (Throwable t) {
            Timber.w(t, "Incompatible modulepack");
            return;
        }

        if (moduleFragments == null)
            return;

        switch (eventRequest.getEventRequest()) {
            case UNLOAD:
                for (FragmentHelper fragment : moduleFragments) {
                    if (fragment == null)
                        continue;

                    packSubMenu.removeItem(fragment.getMenuId());
                    navigationView.removeFragment(fragment.getMenuId());
                }
                break;
            case LOAD:

                for (FragmentHelper fragment : moduleFragments) {
                    if (fragment == null)
                        continue;

                    packSubMenu.add(
                            Menu.NONE,
                            fragment.getMenuId(),
                            Menu.NONE,
                            fragment.getName()
                    );

                    navigationView.addFragment(fragment);
                }
                break;
            default:
                Timber.d("Ignoring Unhandled Request");
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleLoadPackSettingsEvent(LoadPackSettingsEvent loadEvent) {
        String packName = loadEvent.getPackName();
        ModulePack pack = FrameworkManager.getModulePack(packName);

        if (pack == null) {
            Timber.e("No pack found");
            SafeToast.show(this, "Couldn't find pack settings page",
                    Toast.LENGTH_LONG, true);
            return;
        }

        FragmentHelper[] staticFragments = pack.getStaticFragments();

        if (staticFragments == null || staticFragments.length <= 0) {
            Timber.w("No settings page found");
            SafeToast.show(this, "Couldn't find pack settings page",
                    Toast.LENGTH_LONG, true);
            return;
        }

        Integer menuId = staticFragments[0].getMenuId();

        if (menuId == null || !navigationView.onNavigationItemSelected(menuId)) {
            Timber.e("No menu found with id: " + menuId);
            SafeToast.show(this, "Couldn't find pack settings page",
                    Toast.LENGTH_LONG, true);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTutorialFinishedEvent(TutorialFinishedEvent tutorialFinishedEvent) {
        if (tutorialFinishedEvent.isFullTutorial()) {
            int index = -1;
            for (Integer menuId : TUTORIAL_FRAGMENTS) {
                index++;

                if (menuId.equals(tutorialFinishedEvent.getMenuId()))
                    break;
            }

            Timber.d("Index: " + index);

            if (index == -1)
                throw new IllegalStateException("Couldn't figure out menu item: "
                        + tutorialFinishedEvent.getMenuId());

            if (index + 1 < TUTORIAL_FRAGMENTS.size()) {
                int nextMenuId = TUTORIAL_FRAGMENTS.get(index + 1);

                if (navigationView.onNavigationItemSelected(nextMenuId)) {
                    navigationView.getActiveFragment().triggerOnVisible(
                            (fragmentHelper, v) -> fragmentHelper.triggerTutorial(true)
                    );
                }
            } else {
                navigationView.onNavigationItemSelected(R.id.nav_home);

                putPref(SHOW_TUTORIAL, false);

                ShowcaseFactory.getDefaultShowcase(this, (showCaseView, messageContainer, txtTitle, txtMessage, btnClose, btnNext) -> {
                    addRelativeParamRule(messageContainer, RelativeLayout.CENTER_VERTICAL);

                    txtTitle.setText("End of Tutorial");
                    txtMessage.setText("Thanks for going through the Tutorial!"
                            + "\n" + "Enjoy SnapTools!");
                    btnClose.setVisibility(GONE);

                    btnNext.setText("Done");
                    btnNext.setOnClickListener(v -> navigationView.getActiveFragment().triggerOnVisible(
                            (fragmentHelper, v1) -> {
                                // Fixes Bug if tapping Next Button too fast
                                if (showCaseView.isAttachedToWindow())
                                    showCaseView.hide();
                                triggerKonfetti();
                            }, 250
                    ));
                }).build().show();

                Answers.safeLogEvent(
                        new CustomEvent("Finished Full Tutorial")
                );
            }
        }
    }

    public void triggerKonfetti() {
        konfettiView.build()
                .addColors(Color.YELLOW, Color.rgb(255, 175, 0))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .stream(300, 5000L);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleReqLoadFragmentEvent(ReqLoadFragmentEvent loadFragmentEvent) {
        if (!navigationView.onNavigationItemSelected(loadFragmentEvent.getFragmentId())) {
            Timber.e("Couldn't load fragment for id: "
                    + loadFragmentEvent);
            SafeToast.show(
                    this,
                    "Couldn't load fragment "
                            + loadFragmentEvent.getName(),
                    Toast.LENGTH_LONG,
                    true
            );
        }
    }

    @OnClick(R.id.btn_tutorial)
    public void tutorialClicked() {
        FragmentHelper activeFragment = navigationView.getActiveFragment();

        if (activeFragment == null) {
            Timber.w("Null active fragment!");
            return;
        }

        if (drawerLayout != null)
            drawerLayout.closeDrawers();

        activeFragment.triggerTutorial(false);

        Answers.safeLogEvent(
                new CustomEvent("Manual Tutorial")
                        .putCustomAttribute("Active Fragment",
                                activeFragment.getName())
        );
    }

    @OnLongClick(R.id.btn_tutorial)
    public boolean tutorialLongClick() {
        FragmentHelper activeFragment = navigationView.getActiveFragment();

        if (activeFragment != null && activeFragment instanceof HomeFragment) {
            activeFragment.triggerTutorial(true);
        }

        return true;
    }

//	@OnClick(R.id.btn_repackage) public void repackageClicked() {
//		try {
//			com.ljmu.andre.modulepackloader.ModulePack modulePack = new com.ljmu.andre.modulepackloader.ModulePack.Builder()
//					.setPackJarFile(new File(getExternalPath(), "STModulePack_9_Premium_10.26.5.0.jar"))
//					.setPackClassPath("com.ljmu.andre.snaptools.ModulePack.NewModulePack")
//					.setPackAttributes(new TestPackAttributes())
//					.setDexFileDir(getCodeCacheDir())
//					.setConstructorArguments(Arrays.asList("This is the base string", "This is the new pack string"))
//					.build();
//
//			Timber.d("NewMod: " + modulePack.toString());
//		} catch (IOException e) {
//			Timber.e(e);
//		} catch (PackSecurityException e) {
//			Timber.e(e);
//		} catch (PackBuildException e) {
//			Timber.e(e);
//		} catch (PackLoadException e) {
//			Timber.e(e);
//		}
//
////		SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
////
////		Timber.d("Starting preference benchmark");
////		long start = System.currentTimeMillis();
////
////		for(int i = 0; i < 100000; i++) {
////			getPref(FrameworkPreferencesDef.BACK_OPENS_MENU);
////		}
////
////		long end = System.currentTimeMillis() - start;
////
////		Timber.d("Benchmark time %sms", end);
//	}
}
