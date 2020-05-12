package com.ljmu.andre.snaptools.Repackaging;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.CheckResult;

import com.github.javiersantos.appupdater.objects.Version;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.MainActivity;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.CustomObservers;
import com.ljmu.andre.snaptools.Utils.InstallUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.io.SuFileOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Repackaging.RepackageUtil.findAndPatch;
import static com.ljmu.andre.snaptools.Utils.Constants.getApkVersionName;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CONTENT_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.PLACE_HOLDER_UNINSTALL_REPKG;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class RepackageManager {

    public static void initRepackaging(Activity activity) {
        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                activity,
                "Repackaging SnapTools",
                "Initialising App Repackaging",
                false
        );

        progressDialog.show();

        // ===========================================================================
        Observable.<String>create(e ->
                RepackageManager.repackageApplication(activity, e))
                // ===========================================================================
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObservers.SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        progressDialog.<Progress>getExtension()
                                .setMessage(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressDialog.dismiss();

                        DialogFactory.createErrorDialog(
                                activity,
                                "Error Repackaging Application",
                                e.getMessage()
                        ).show();
                    }
                });
    }

    public static void repackageApplication(Activity activity, ObservableEmitter<String> emitter)
            throws RepackageException {
        emitter.onNext("Generating package name...");

        // ===========================================================================
        Timber.d("Retrieving Repackage Observable");

        String obfusPackageName = RepackageUtil.genPackageName("com.", Constants.ORIG_PACKAGE.length());
        Timber.d("Obfuscating package to: " + obfusPackageName);

        File contentPath = Assert.notNull("Null content path for repackaging", getCreateDir(CONTENT_PATH));
        File repackFile = new File(contentPath, "repack.apk");
        Timber.d("Repacking apk to file: " + repackFile);
        // ===========================================================================

        /**
         * ===========================================================================
         * Patch Manifest and Resign apk into the repackFile variable
         * ===========================================================================
         */
        getPatchObservable(activity, emitter, obfusPackageName, repackFile);
        // ===========================================================================

        /**
         * ===========================================================================
         * Install the newly repackaged apk
         * ===========================================================================
         */
        boolean rootAccess = Shell.rootAccess();
        emitter.onNext("Installing repackaged application");
        putPref(REPACKAGE_NAME, obfusPackageName);
        putPref(PLACE_HOLDER_UNINSTALL_REPKG, activity.getPackageName());
        InstallUtils.install(activity, repackFile, rootAccess);
        // ===========================================================================

        /**
         * ===========================================================================
         * Remove any previous default apks
         * ===========================================================================
         */
        emitter.onNext("Uninstalling original application");
        InstallUtils.uninstallPackage(activity, activity.getPackageName(), rootAccess);
        // ===========================================================================

        emitter.onNext("Completing");
    }

    private static void getPatchObservable(Activity activity, ObservableEmitter<String> emitter,
                                           String obfusPackageName, File repackFile) throws RepackageException {
        Timber.d("Calling patch observable");

        /**
         * ===========================================================================
         * STEALING MAGISK CODE YO
         * ===========================================================================
         */
        InputStream codePathStream;
        JarMap apk;

        try {
            // Read whole APK into memory
            codePathStream = new FileInputStream(activity.getPackageCodePath());
            apk = new JarMap(codePathStream);
            JarEntry je = new JarEntry(Constants.ANDROID_MANIFEST);
            byte xml[] = apk.getRawData(je);

            emitter.onNext("Rewriting manifest...");

            Timber.d("Manifest: " + new String(xml, "UTF-8"));

            if (!findAndPatch(xml, Constants.ORIG_PACKAGE, obfusPackageName))
                throw new RepackageException("Couldn't find/patch original package");

            Timber.d("Patching original package complete");

            if (!findAndPatch(xml, Constants.ORIG_PACKAGE + ".provider", obfusPackageName + ".provider"))
                throw new RepackageException("Couldn't find/patch original package provider");

            if (!findAndPatch(xml, Constants.ORIG_PACKAGE + ".apk_provider", obfusPackageName + ".apk_provider"))
                throw new RepackageException("Couldn't find/patch original package apk provider");

            Timber.d("Patching original package provider complete");
            apk.getOutputStream(je).write(xml);

            emitter.onNext("Resigning application...");
            InputStream providedCertInputStream = activity.getResources().getAssets().open("certificate.pem");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate providedCertificate = (X509Certificate)
                    cf.generateCertificate(providedCertInputStream);

            providedCertInputStream.close();

            InputStream privateKeyStream = activity.getResources().getAssets().open("key.pk8");
            PrivateKey privateKey = CryptoUtils.readPrivateKey(privateKeyStream);

            privateKeyStream.close();

            Timber.d("Provided Cert: " + providedCertificate.getSigAlgName());
            Timber.d("Provided Key: " + privateKey.getAlgorithm());

            // Sign the APK
            try {
                ZipUtils.signZip(providedCertificate, privateKey, apk, new SuFileOutputStream(repackFile));
            } catch (Exception e) {
                throw new RepackageException("Couldn't sign repackaged application!");
            }

            Timber.d("Repackage success");
            emitter.onNext("Successfully signed apk!");
            codePathStream.close();
            apk.close();
        } catch (IOException e) {
            throw new RepackageException("Couldn't access file");
        } catch (GeneralSecurityException e) {
            throw new RepackageException("Couldn't create application signatures!");
        }
    }

    @CheckResult
    public static ThemedDialog askUserDialog(Activity activity) {
        return DialogFactory.createConfirmation(
                activity,
                "Application repackaging?",
                "This is an attempt to circumvent Snapchat's malicious app detection." +
                        "\n\nYou are " + htmlHighlight("NOT") + " forced to use Application Repackaging. Every feature will be usable with or without Application Repackaging." +
                        "\nDo " + htmlHighlight("NOT") + " rely on App Repackaging if you are scared to be banned." +
                        "\n\nThis will install a new " + STApplication.MODULE_TAG + " Application with a different Package Name, only the repackaged App should remain." +
                        "\n\n" + htmlHighlight("IMPORTANT: ") + "\nAfter the Repackaging Process completed, do the following:" +
                        "\n- Open repackaged " + STApplication.MODULE_TAG + " once" +
                        "\n- Activate repackaged " + STApplication.MODULE_TAG + " in Xposed" +
                        "\n- Reboot",
                new ThemedDialog.ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        initRepackaging(activity);
                    }
                });
    }

    /**
     * Helper Method if multiple SnapTools Applications have been detected.
     *
     * @param activity Context used for Dialogs
     * @param info     PackageInfo of the detected Duplicate.
     * @return Uninstall Dialog with ClickListener
     */
    @CheckResult
    public static ThemedDialog getUninstallDuplicates(Activity activity, PackageInfo info) {
        int comparedVersions = new Version(getApkVersionName()).compareTo(new Version(info.versionName));
        String message;
        boolean targetDuplicate;
        ThemedDialog.ThemedClickListener listener;
        if (comparedVersions > 0) {
            message = "Another (less up-to-date) " + STApplication.MODULE_TAG + " Application has been found.";
            targetDuplicate = true;
        } else if (comparedVersions == 0) {
            message = "Both Duplicates are the same version. ";
            boolean otherObfus = !info.packageName.equals(MainActivity.class.getPackage().getName());
            boolean thisObfus = !activity.getPackageName().equals(MainActivity.class.getPackage().getName());
            if (thisObfus) {
                message += "This App is already repackaged.";
                targetDuplicate = true;
            } else if (otherObfus) {
                message += "The duplicate is already repackaged.";
                targetDuplicate = false;
            } else {
                message += "None of these Apps are repackaged.";
                targetDuplicate = true;
            }
        } else {
            message = "This App is outdated. A more up-to-date App Version has been found on this phone.";
            targetDuplicate = false;
        }
        if (targetDuplicate) {
            message += " Do you want to uninstall the duplicate?";
            listener = new ThemedDialog.ThemedClickListener() {
                @Override
                public void clicked(ThemedDialog themedDialog) {
                    themedDialog.dismiss();
                    Timber.w("Uninstalling the duplicate %s application (Version: \"%s\", PackageName: \"%s\"", STApplication.MODULE_TAG, getApkVersionName(), activity.getPackageName());
                    InstallUtils.uninstallPackage(activity, info.packageName, Shell.rootAccess());
                    SafeToast.show(
                            activity,
                            "Successfully uninstalled " + info.packageName,
                            false);
                }
            };
        } else {
            message += " Do you want to uninstall this App?";
            listener = new ThemedDialog.ThemedClickListener() {
                @Override
                public void clicked(ThemedDialog themedDialog) {
                    themedDialog.dismiss();
                    Timber.w("Uninstalling this %s application (Version: \"%s\", PackageName: \"%s\"", STApplication.MODULE_TAG, getApkVersionName(), activity.getPackageName());
                    InstallUtils.uninstallPackage(activity, activity.getPackageName(), Shell.rootAccess());
                    DialogFactory.createProgressDialog(
                            activity,
                            "Uninstall Application",
                            "Uninstalling this Application...",
                            false
                    ).show();
                }
            };
        }
        message += "\n\nThis App (Version: \"" + getApkVersionName() + "\", PackageName : \"" + activity.getPackageName() + "\")" +
                "\nDuplicate (Version: \"" + info.versionName + "\", PackageName : \"" + info.packageName + "\")" +
                "\n\nThis can happen when you try to update, re-install or repackage " + STApplication.MODULE_TAG + ".";
        return DialogFactory.createConfirmation(
                activity,
                "Found Duplicates",
                message,
                listener
        );
    }

    public static boolean checkDuplicate(Activity activity, String pkg) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    pkg,
                    0
            );
            if (info != null) {
                Timber.w("Found an installed SnapTools duplicate (Package: %s), asking user to uninstall", info.packageName);
                RepackageManager.getUninstallDuplicates(
                        activity,
                        info
                ).show();
                return true;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static class RepackageException extends Exception {
        public RepackageException(String message) {
            super(message);
        }
    }
}
