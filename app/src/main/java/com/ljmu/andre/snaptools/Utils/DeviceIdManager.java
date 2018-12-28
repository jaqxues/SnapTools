package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.Translation.Translator.translate;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.INSTALLATION_ID;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.APPLICATION_INITIALISATION_FAILED_MSG;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.APPLICATION_INITIALISATION_FAILED_TITLE;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.APPLICATION_INITIALISATION_SUCCESS_MSG;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.APPLICATION_INITIALISATION_SUCCESS_TITLE;

/**
 * ===========================================================================
 * The Device ID Manager uses persistent system properties to assign a
 * unique ID to the user which is hidden as an unassuming system property
 * <p>
 * Alternatively it falls back to using the old Android ID system for
 * extremely old or unsupporting devices
 * ===========================================================================
 */
public class DeviceIdManager {
    private static final String PERSIST_ID_KEY = "persist.encode.gkp";
    private static String cachedDeviceId;

    /**
     * ===========================================================================
     * Attempts to use the best method to assign the device with a completely
     * unique and relatively unchangeable identifier
     * ===========================================================================
     *
     * @return TRUE - If system id was assigned synchronously
     * FALSE - If user input is required before progressing
     */
    public static boolean assignSystemId(Activity activity) {
        // Generate a SystemID to be assigned ========================================
        String systemId = generateSystemId(activity);

        try {
            setSystemIdWReflection(systemId);
            return true;
        } catch (Throwable ignored) { }

        // If reflection fails, ask user for root ====================================
        DialogFactory.createConfirmation(
                activity,
                "Automatic Application Initialisation Failed",
                getRootConfirmationMessage(),
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        setSystemIDWRoot(activity, systemId, themedDialog);
                    }
                },
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        activity.finish();
                    }
                }
        ).setDismissable(false).show();

        return false;
    }

    /**
     * ===========================================================================
     * Generate a new SystemID using the AndroidID or a random hex value
     * ===========================================================================
     */
    private static String generateSystemId(Activity activity) {
        String androidId = getAndroidId(activity);

        // If we can't get the AndroidID generate a random Hex String ================
        if (androidId == null || androidId.isEmpty()) {
            androidId = Double.toHexString(Math.random() * ((float) Integer.MAX_VALUE / 2f));
        }

        Hasher hasher = Hashing.murmur3_128(6437401).newHasher();
        hasher.putString(
                androidId,
                Charset.defaultCharset()
        );

        return hasher.hash().toString();
    }

    /**
     * ===========================================================================
     * Set the SystemID using Reflection
     * - It's a best attempt method, if failed use ROOT
     * ===========================================================================
     *
     * @throws Throwable - When invocation failed
     */
    private static void setSystemIdWReflection(String systemId) throws Throwable {
        Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
        Method method = systemPropertiesClass.getDeclaredMethod(
                "set",
                String.class, String.class
        );

        method.invoke(
                null,
                PERSIST_ID_KEY,
                systemId.substring(0, Math.min(systemId.length(), 90))
        );
    }

    /**
     * ===========================================================================
     * This requires a lot of split string encryption
     * ===========================================================================
     *//*
	  Initialisation has failed and is <b>required</b> to use the application.
	  Would you like to use ROOT to continue the initialisation?

	  NO - Will cause the application to close
	  YES - Will request root access*/
    private static String getRootConfirmationMessage() {
        return "Initialisation has failed and is <b>required</b> to use the application." +
                "\n" + "Would you like to use "
                + htmlHighlight("ROOT")
                + " to continue the initialisation?"
                + "\n\n" + "NO"
                + " - Will cause the application to close"
                + "\n" + "YES"
                + " - Will request root access";
    }

    /**
     * ===========================================================================
     * Perform the Shell Command to assign the SystemID
     * ===========================================================================
     */
    private static void setSystemIDWRoot(Activity activity, String systemId, ThemedDialog rootConfirmationDialog) {
        ShellUtils.sendCommand(
                "setprop " + PERSIST_ID_KEY + " " + systemId
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        rootConfirmationDialog.dismiss();

                        // Check if SystemID has successfully assigned ===============================
                        if (aBoolean && isSystemIdAssigned()) {

                            // Dialog to confirm app restart =============================================
                            DialogFactory.createBasicMessage(
                                    activity,
                                    translate(APPLICATION_INITIALISATION_SUCCESS_TITLE),
                                    translate(APPLICATION_INITIALISATION_SUCCESS_MSG),
                                    new ThemedClickListener() {
                                        @Override
                                        public void clicked(ThemedDialog themedDialog) {
                                            themedDialog.dismiss();
                                            Intent intent = activity.getIntent();
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                    }
                            ).setDismissable(false).show();
                        } else {
                            // Dialog to confirm app ending ==============================================
                            DialogFactory.createErrorDialog(
                                    activity,
                                    translate(APPLICATION_INITIALISATION_FAILED_TITLE),
                                    translate(APPLICATION_INITIALISATION_FAILED_MSG),
                                    new ThemedClickListener() {
                                        @Override
                                        public void clicked(ThemedDialog themedDialog) {
                                            themedDialog.dismiss();
                                            activity.finish();
                                        }
                                    }
                            ).setDismissable(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        rootConfirmationDialog.dismiss();

                        // Dialog to confirm app ending ==============================================
                        DialogFactory.createErrorDialog(
                                activity,
                                translate(APPLICATION_INITIALISATION_FAILED_TITLE),
                                translate(APPLICATION_INITIALISATION_FAILED_MSG),
                                new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        themedDialog.dismiss();
                                        activity.finish();
                                    }
                                }
                        ).setDismissable(false).show();
                    }
                });
    }

    /**
     * ===========================================================================
     * Retrieve the android id of the device using the regular means
     * ===========================================================================
     */
    private static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * ===========================================================================
     * Check if the SystemID property has already been assigned
     * ===========================================================================
     *
     * @return TRUE - If SystemID has been assigned
     */
    public static boolean isSystemIdAssigned() {
        try {
            String systemId = getSystemId();
            return systemId != null && !systemId.isEmpty();
        } catch (Throwable throwable) {
            Timber.e(throwable);
        }

        return false;
    }

    /**
     * ===========================================================================
     * Efficiency method used for determining if system property has been assigned
     * ===========================================================================
     */
    private static String getSystemId() throws Throwable {
        Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");

        Method method = systemPropertiesClass.getDeclaredMethod(
                "get",
                String.class
        );

        return cachedDeviceId = (String) method.invoke(null, PERSIST_ID_KEY);
    }

    /**
     * ===========================================================================
     * Smart method for retrieving the system id
     * Uses the old device id method if the System Property method fails
     * ===========================================================================
     */
    public synchronized static String getDeviceId(Context context) {
        if (cachedDeviceId != null && !cachedDeviceId.isEmpty())
            return cachedDeviceId;

        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");

            Method method = systemPropertiesClass.getDeclaredMethod(
                    "get",
                    String.class
            );

            String deviceId = (String) method.invoke(null, PERSIST_ID_KEY);

            if (deviceId != null && !deviceId.isEmpty())
                return cachedDeviceId = deviceId;
        } catch (Throwable e) {
            Timber.e(e);
        }

        return cachedDeviceId = getDeviceIdOldMethod(context);
    }

    /**
     * ===========================================================================
     * An outdated method of retrieving a unique identifier.
     * Uses MurMurHash to attempt to further secure the ID
     * ===========================================================================
     */
    private static String getDeviceIdOldMethod(Context context) {

        // Hash the ID to make it harder to spoof/guess ==============================
        Hasher hasher = Hashing.murmur3_128(8435809).newHasher();
        if (Preferences.getIsInitialised().get())
            hasher.putString(getPref(INSTALLATION_ID), Charset.defaultCharset());

        hasher.putString(
                getAndroidId(context),
                Charset.defaultCharset()
        );

        return cachedDeviceId = hasher.hash().toString();
    }
}
