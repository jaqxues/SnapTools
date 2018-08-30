package com.ljmu.andre.snaptools.Repackaging;

import android.app.Activity;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.topjohnwu.superuser.io.SuFileOutputStream;
import io.reactivex.ObservableEmitter;
import timber.log.Timber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;

import static com.ljmu.andre.GsonPreferences.Preferences.*;
import static com.ljmu.andre.snaptools.Repackaging.RepackageUtil.findAndPatch;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CONTENT_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.REPACKAGE_NAME;
import static com.ljmu.andre.snaptools.Utils.ShellUtils.sendCommandSync;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class RepackageManager {

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
         * Use root to install the newly repackaged apk
         * ===========================================================================
         */
        emitter.onNext("Installing repackaged application");
        if (!sendCommandSync("pm install -t " + repackFile)) {
//			throw new RepackageException("Failed to install repackaged application");
            // Test on emulator: Successful installation threw this exception
            // Probably because the first line of output is "pkg...". Magisk not performing checks either
        }
        // ===========================================================================

        /**
         * ===========================================================================
         * Use root to remove any previously repackaged apps
         * This will likely fail as repackaging will generally only be required for
         * default distributed apk's
         * ===========================================================================
         */
        String previousRepackageName = getPref(REPACKAGE_NAME);
        putPref(REPACKAGE_NAME, obfusPackageName);

        if (previousRepackageName != null) {
            emitter.onNext("Uninstalling alternate repackages");

            if (!sendCommandSync("pm uninstall " + previousRepackageName)) {
                Timber.e("Couldn't find previously repackaged app!");
            }
        }
        // ===========================================================================

        /**
         * ===========================================================================
         * Use root to remove any previous default apks
         * ===========================================================================
         */
        // TODO: Reword
        emitter.onNext("Uninstalling original application");
        if (!sendCommandSync("pm uninstall " + activity.getPackageName())) {
//			throw new RepackageException("Failed to uninstall original application");
            // Test on emulator: Successful un-installation threw this exception
            // Probably because the first line of output is "pkg...". Magisk not performing checks either
        }
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
        } catch (IOException e) {
            // TODO: Reword error
            throw new RepackageException("Couldn't access file");
        } catch (GeneralSecurityException e) {
            throw new RepackageException("Couldn't create application signatures!");
        }
    }

    private static class RepackageException extends Exception {
        public RepackageException(String message) {
            super(message);
        }
    }
}
