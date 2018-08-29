package com.ljmu.andre.snaptools.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Security {
    private static X509Certificate providedCertificate;

    private static boolean isInitialised;

    public static void init(Resources resources) throws IOException, CertificateException {
        if (isInitialised) {
            Timber.d("Security already initialised");
            return;
        }

        // Load the provided certificate \\
        InputStream providedCertInputStream = resources.getAssets().open("SnapToolsKeystore_Public.cer");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        providedCertificate = (X509Certificate)
                cf.generateCertificate(providedCertInputStream);

        providedCertInputStream.close();
        // Loaded certificate \\

        isInitialised = true;
    }

    /**
     * ===========================================================================
     * Verify that the JarFile and its Manifest have been signed by
     * {@link this#providedCertificate}, requires {@link this#init(Resources)}
     * to have been called on application start
     * ===========================================================================
     */
    public static void verifyJar(Manifest manifest, JarFile jarFile)
            throws IOException, SecurityException {
//		Vector<JarEntry> entriesVec = new Vector<>();
//
//		// Ensure the jar file is signed.
//		if (manifest == null) {
//			throw new SecurityException("The provider is not signed");
//		}
//
//		// Ensure all the entries' signatures verify correctly
//		byte[] buffer = new byte[8192];
//		Enumeration entries = jarFile.entries();
//
//		while (entries.hasMoreElements()) {
//			JarEntry je = (JarEntry) entries.nextElement();
//
//			// Skip directories.
//			if (je.isDirectory()) continue;
//			entriesVec.addElement(je);
//			InputStream is = jarFile.getInputStream(je);
//
//			// Read in each jar entry. A security exception will
//			// be thrown if a signature/digest check fails.
//			//noinspection StatementWithEmptyBody
//			while (is.read(buffer, 0, buffer.length) != -1) {
//				// Don't care
//			}
//			is.close();
//		}
//
//		// Get the list of signer certificates
//		Enumeration e = entriesVec.elements();
//
//		while (e.hasMoreElements()) {
//			JarEntry je = (JarEntry) e.nextElement();
//
//			// Every file must be signed except files in META-INF.
//			Certificate[] certs = je.getCertificates();
//			if ((certs == null) || (certs.length == 0)) {
//				if (!je.getName().startsWith("META-INF"))
//					throw new SecurityException("The provider has unsigned class files.");
//			} else {
//				// Check whether the file is signed by the expected
//				// signer. The jar may be signed by multiple signers.
//				// See if one of the signers is 'targetCert'.
//
//				if (!je.getName().equals("classes.dex"))
//					continue;
//
//				int startIndex = 0;
//				X509Certificate[] certChain;
//				boolean signedAsExpected = false;
//
//				while ((certChain = getAChain(certs, startIndex)) != null) {
//					if (certChain[0].equals(providedCertificate)) {
//						// Stop since one trusted signer is found.
//						try {
//							PackCertification.putPackSHA1(doFingerprint(certChain[0].getSignature(), "SHA1"));
//						} catch (Exception e1) {
//							Timber.e(e1);
//						}
//
//						signedAsExpected = true;
//						break;
//					}
//					// Proceed to the next chain.
//					startIndex += certChain.length;
//				}
//
//				if (!signedAsExpected)
//					throw new SecurityException("Jar Not Certified!");
//			}
//		}
    }

    /**
     * Extracts ONE certificate chain from the specified certificate array
     * which may contain multiple certificate chains, starting from index
     * 'startIndex'.
     */
    private static X509Certificate[] getAChain(Certificate[] certs,
                                               int startIndex) {
        if (startIndex > certs.length - 1)
            return null;

        int i;
        // Keep going until the next certificate is not the
        // issuer of this certificate.
        for (i = startIndex; i < certs.length - 1; i++) {
            if (!((X509Certificate) certs[i + 1]).getSubjectDN().
                    equals(((X509Certificate) certs[i]).getIssuerDN())) {
                break;
            }
        }
        // Construct and return the found certificate chain.
        int certChainSize = (i - startIndex) + 1;
        X509Certificate[] ret = new X509Certificate[certChainSize];
        for (int j = 0; j < certChainSize; j++) {
            ret[j] = (X509Certificate) certs[startIndex + j];
        }
        return ret;
    }

    public static class ApkCertification {
        private static String cachedSSH1;

        public static String getApkFingerprint(Context context) {
            if (cachedSSH1 != null)
                return cachedSSH1;

            try {
                if (context == null)
                    return "";

                PackageManager pm = context.getPackageManager();
                Signature sig = pm.getPackageInfo(STApplication.PACKAGE, PackageManager.GET_SIGNATURES).signatures[0];
                return cachedSSH1 = doFingerprint(sig.toByteArray(), "SHA1").toUpperCase();
            } catch (Throwable e) {
                Timber.e(e);
            }

            return "";
        }

        static String doFingerprint(byte[] certificateBytes, String algorithm)
                throws Exception {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(certificateBytes);
            byte[] digest = md.digest();

            StringBuilder toRet = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                if (i != 0)
                    toRet.append(":");
                int b = digest[i] & 0xff;
                String hex = Integer.toHexString(b);
                if (hex.length() == 1)
                    toRet.append("0");
                toRet.append(hex);
            }
            return toRet.toString();
        }
    }

    /**
     * ===========================================================================
     * Certification computation used for ServerSide fingerprint analysis
     * ===========================================================================
     */
    public static class PackCertification {
        private static final Object FINGERPRINT_LOCK = new Object();
        private static Set<String> cachedSHA1Set = new HashSet<>();
        private static String cachedFingerprint;

        public static String getFingerprint() {
            synchronized (FINGERPRINT_LOCK) {
                return cachedFingerprint;
            }
        }

        static void putPackSHA1(String sha1) {
            if (!cachedSHA1Set.contains(sha1)) {
                cachedSHA1Set.add(sha1);
                recomputeFingerprint();
            }
        }

        private static void recomputeFingerprint() {
            List<String> fingerprints = new ArrayList<>(cachedSHA1Set);

            Observable.fromCallable(() -> {
                Hasher hasher = Hashing.murmur3_128(2407468).newHasher();

                for (String cachedSHA1 : fingerprints)
                    hasher.putString(cachedSHA1, Charset.defaultCharset());

                return hasher.hash().toString();
            }).subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe(new SimpleObserver<String>() {
                        @Override
                        public void onNext(@NonNull String s) {
                            synchronized (FINGERPRINT_LOCK) {
                                cachedFingerprint = s;
                            }
                        }
                    });
        }
    }
}
