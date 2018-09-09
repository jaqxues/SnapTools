package com.ljmu.andre.snaptools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;

import com.ljmu.andre.ErrorLogger.ErrorLogger;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Dialogs.Content.FrameworkLoadError;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.Networking.VolleyHandler;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.ModuleChecker;
import com.ljmu.andre.snaptools.Utils.TimberUtils;
import com.ljmu.andre.snaptools.Utils.UnhookManager;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.ACCEPTED_TOS;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CHECK_PACK_UPDATES_SC;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.NOTIFY_ON_LOAD;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SYSTEM_ENABLED;
import static com.ljmu.andre.snaptools.Utils.NotificationUtils.showLoadedNotification;
import static com.ljmu.andre.snaptools.Utils.ResourceMapper.getResId;
import static com.ljmu.andre.snaptools.Utils.UnhookManager.addUnhook;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class HookManager implements IXposedHookLoadPackage {

    // ===========================================================================

    private static AtomicBoolean hasHooked = new AtomicBoolean(false);
    private Context snapContext;
    private Activity snapActivity;


    // ===========================================================================

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {


        /**
         * ===========================================================================
         * Self Hook -> forces isModuleActive() to return true
         * ===========================================================================
         */
        if (lpparam.appInfo != null && lpparam.appInfo.className != null && lpparam.appInfo.className.equals(STApplication.class.getName())) {
            try {
                findAndHookMethod(
                        ModuleChecker.class.getName(),
                        lpparam.classLoader,
                        "isModuleActive",
                        XC_MethodReplacement.returnConstant(true)
                );
                findAndHookMethod(
                        ModuleChecker.class.getName(),
                        lpparam.classLoader,
                        "getXposedVersion",
                        XC_MethodReplacement.returnConstant(XposedBridge.getXposedVersion())
                );
            } catch (Throwable t) {
                XposedBridge.log(t);
            }

            return;
        }

        /**
         * ===========================================================================
         * Check if the current loading process is Snapchat or if we have already
         * hooked the process
         * ===========================================================================
         */
        if (!lpparam.packageName.contains("com.snapchat.android")
                || hasHooked.getAndSet(true)) {
            // Stops multiple hooks from being applied
            if (hasHooked.get())
                Timber.d("Tried to reapply hooks!");

            return;
        }

        TimberUtils.plantAppropriateTree();

        Timber.d("PID: "
                + Process.myPid());

        ErrorLogger.init();

        Timber.d("Loading preferences");
        Preferences.init(
                Preferences.getExternalPath() + "/" + STApplication.MODULE_TAG + "/"
        );

        if (!(boolean) getPref(SYSTEM_ENABLED)) {
            Timber.w("System Disabled... Aborting initialisation");
            return;
        }

        String pkgName = getPref(REPACKAGE_NAME);
        STApplication.PACKAGE = (pkgName == null ? HookManager.class.getPackage().getName() : pkgName);

        Timber.d("Snapchat Is Loading!");

        Timber.d("Hooking Application Attach");
        addUnhook("System",
                findAndHookMethod(
                        "android.app.Application",
                        lpparam.classLoader,
                        "attach",
                        Context.class,
                        new ST_MethodHook() {
                            @Override
                            protected void after(MethodHookParam param) throws Throwable {

                                Timber.d("PID: "
                                        + Process.myPid());

                                Timber.d("Application Attach Called");
                                snapContext = (Context) param.args[0];

                                Application app = (Application) param.thisObject;
                                Context moduleContext = helpCreatePackageContext(app);
                                ContextHelper.set(moduleContext);

                                addUnhook("System",
                                        findAndHookMethod(
                                                "com.snapchat.android.LandingPageActivity",
                                                lpparam.classLoader,
                                                "onCreate",
                                                Bundle.class,
                                                new ST_MethodHook() {
                                                    @Override
                                                    protected void before(MethodHookParam param) {
                                                        snapActivity = (Activity) param.thisObject;
                                                        UnhookManager.unhookAll("System");
                                                        try {
                                                            if (moduleContext == null) {
                                                                new AlertDialog.Builder(app)
                                                                        .setTitle("Open " + STApplication.MODULE_TAG)
                                                                        .setMessage("You probably re-installed or installed an updated version of " + STApplication.MODULE_TAG +
                                                                                " and used the App Repackaging feature.\nUnfortunately, due to how App " +
                                                                                "Repackaging works, you need to open " + STApplication.MODULE_TAG + " once after Repackaging before you can use the Xposed Hooks." +
                                                                                "\nNo reboot should be required if you activated the correct App in Xposed and already rebooted.")
                                                                        .setPositiveButton("Close Snapchat", (dialog, which) -> snapActivity.finish());
                                                                return;
                                                            }
                                                            /**
                                                             * ===========================================================================
                                                             * Initialisation Stage
                                                             * ===========================================================================
                                                             */
                                                            snapActivity = (Activity) param.thisObject;
                                                            ContextHelper.setActivity(snapActivity);

//															initFabrics(snapActivity, moduleContext);

                                                            CacheDatabase.init(snapContext);
                                                            VolleyHandler.init(snapActivity);

                                                            if (!initTOS())
                                                                return;

                                                            // ===========================================================================

//															TrialUtils.endTrialIfExpired(snapActivity);

                                                            Timber.d("Loading FrameworkManager");

                                                            // Load the certificate required to validate the ModulePack ==================
//															Security.init(ContextHelper.getModuleResources(snapActivity));

                                                            List<PackLoadState> packLoadStates;

                                                            long packLoadStart = System.currentTimeMillis();
                                                            packLoadStates = FrameworkManager.loadAllModulePacks(snapActivity);
                                                            long packLoadEnd = System.currentTimeMillis();

                                                            Timber.d("Starting Inject");
                                                            FrameworkManager.injectAllHooks(packLoadStates, lpparam.classLoader, snapActivity);
                                                            long packInjectEnd = System.currentTimeMillis();

                                                            Timber.d("Load Times [Load: %s][Inject: %s]", (packLoadEnd - packLoadStart), (packInjectEnd - packLoadEnd));
                                                            // Just a quick toggle to test the hang system =================
                                                            //if(getPref(KILL_SC_ON_CHANGE))
                                                            //Thread.sleep(30000);

                                                            if (getPref(CHECK_PACK_UPDATES_SC))
                                                                FrameworkManager.checkPacksForUpdate(snapActivity);

                                                            boolean hasFailed = false;
                                                            for (PackLoadState loadState : packLoadStates) {
                                                                if (loadState.hasFailed())
                                                                    hasFailed = true;
                                                            }

                                                            if (hasFailed) {
                                                                new ThemedDialog(snapActivity)
                                                                        .setTitle("Framework Load Error")
                                                                        .setHeaderDrawable(
                                                                                getResId(
                                                                                        moduleContext,
                                                                                        "error_header",
                                                                                        "drawable"
                                                                                )
                                                                        )
                                                                        .setExtension(new FrameworkLoadError(packLoadStates))
                                                                        .show();
                                                                return;
                                                            }

//															BackgroundAuthVerifier.spoolVerifierThread();

                                                            Timber.d("Framework loaded successfully");

                                                            if ((boolean) getPref(NOTIFY_ON_LOAD) && packLoadStates.size() > 0)
                                                                showLoadedNotification(snapActivity);
                                                        } catch (NoSuchMethodError | NoClassDefFoundError e) {
                                                            Timber.wtf(e, "Error inside LandingPage Hook");
                                                            DialogFactory.createErrorDialog(
                                                                    snapActivity,
                                                                    "Error Loading SnapTools!",
                                                                    "Fatal error loading system!"
                                                                            + "\n" + "Please ensure you have performed a full reboot before seeking help"
                                                                            + "\n\n" + "There appears to be an invalid Pack/Apk combination installed. Please make sure your Pack and APK are both updated"
                                                            ).show();
                                                        } catch (Throwable t) {
                                                            Timber.wtf(t, "Error inside LandingPage Hook");
                                                            DialogFactory.createErrorDialog(
                                                                    snapActivity,
                                                                    "Error Loading SnapTools!",
                                                                    "Fatal error loading system!"
                                                                            + "\n" + "Please ensure you have performed a full reboot before seeking help"
                                                                            + "\n\n" + t.getMessage()
                                                            ).show();
                                                        }
                                                    }
                                                }
                                        ));
                            }
                        }
                ));
    }

//	private void initFabrics(Activity snapActivity, Context moduleContext) {
//		Timber.e("FABRICS ANALYTICS WAS REDACTED FROM PUBLIC SOURCE!");
//
//		try {
//			Fabric.with(snapActivity, moduleContext, new Crashlytics());
//			String email = getPref(ST_EMAIL);
//			if (email != null)
//				Crashlytics.setUserEmail(email);
//
//			String displayName = getPref(ST_DISPLAY_NAME_OBFUS);
//			if (displayName != null)
//				Crashlytics.setUserName(displayName);
//
//			Set<String> selectedPacks = getPref(SELECTED_PACKS);
//			Crashlytics.setString("Selected Packs",
//					String.valueOf(selectedPacks));
//			Crashlytics.setString("User",
//					email);
//		} catch (Throwable e) {
//			Timber.e(e);
//
//			try {
//				SlackUtils.uploadToSlack(
//						"FabricsInitError",
//						Log.getStackTraceString(e)
//				);
//
//				if (snapActivity != null && !snapActivity.isFinishing()) {
//					SafeToast.show(
//							snapActivity,
//							"Failed to load error reporting... Please contact developers",
//							Toast.LENGTH_LONG,
//							true
//					);
//				}
//			} catch (Throwable ignored) {
//			}
//		}
//	}

    private boolean initTOS() {
        if (!(boolean) getPref(ACCEPTED_TOS)) {
            Timber.w("Terms of service not accepted... Requesting acceptance");

            DialogFactory.createConfirmation(
                    snapActivity,
                    "Terms and Conditions",
                    "By pressing YES, you agree to our Terms of Service and our Privacy Policy, both of which can be viewed under the Legal page in the SnapTools app.",
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            putPref(ACCEPTED_TOS, true);
                            themedDialog.dismiss();

                            DialogFactory.createBasicMessage(
                                    snapActivity,
                                    "Restart Required",
                                    "You have accepted the ToS, a restart of Snapchat will be required!"
                            ).show();
                        }
                    },
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();

                            DialogFactory.createErrorDialog(
                                    snapActivity,
                                    "Terms of Service Rejected",
                                    "You have NOT accepted the ToS therefore functionality is disabled"
                                            + "\n\n"
                                            + "It is advised that you uninstall this application to not see this message every launch"
                            ).show();

                            UnhookManager.abortSystem();
                        }
                    }
            ).setDismissable(false).show();

            return false;
        }

        return true;
    }

    public Context helpCreatePackageContext(Application app) {
        try {
            return app.createPackageContext(STApplication.PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "ST PackageName invalid, probably due to App Repackaging. User is advised to open SnapTools once.");
        }
        return null;
    }
}