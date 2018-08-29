package com.ljmu.andre.modulepackloader.Listeners;

import com.ljmu.andre.modulepackloader.Exceptions.PackSecurityException;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public interface PackSecurityListener {
    public void onSecurityCheck(JarFile jarFile) throws PackSecurityException, IOException;

    /**
     * ===========================================================================
     * Employs a slightly modified version of "sweetlilmre" JarVerifier.
     *
     * @see <a href="https://github.com/sweetlilmre/kezdet/blob/master/src/KezdetHostLib/src/org/whipplugin/data/bundle/JarVerifier.java">JarVerifier</a>
     * ===========================================================================
     */
    class DefaultSecurityListener implements PackSecurityListener {
        private X509Certificate certificate;

        public DefaultSecurityListener(X509Certificate certificate) {
            this.certificate = certificate;
        }

        @Override
        public void onSecurityCheck(JarFile jarFile) throws PackSecurityException, IOException {
            Manifest manifest = jarFile.getManifest();
            Vector<JarEntry> entriesVec = new Vector<>();

            // Ensure the jar file is signed.
            if (manifest == null) {
                throw new SecurityException("The provider is not signed");
            }

            // Ensure all the entries' signatures verify correctly
            byte[] buffer = new byte[8192];
            Enumeration entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry je = (JarEntry) entries.nextElement();

                // Skip directories.
                if (je.isDirectory()) continue;
                entriesVec.addElement(je);
                InputStream is = jarFile.getInputStream(je);

                // Read in each jar entry. A security exception will
                // be thrown if a signature/digest check fails.
                //noinspection StatementWithEmptyBody
                while (is.read(buffer, 0, buffer.length) != -1) {
                    // Don't care
                }
                is.close();
            }

            // Get the list of signer certificates
            Enumeration e = entriesVec.elements();

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();

                // Every file must be signed except files in META-INF.
                Certificate[] certs = je.getCertificates();
                if ((certs == null) || (certs.length == 0)) {
                    if (!je.getName().startsWith("META-INF"))
                        throw new SecurityException("The provider has unsigned class files.");
                } else {
                    // Check whether the file is signed by the expected
                    // signer. The jar may be signed by multiple signers.
                    // See if one of the signers is 'targetCert'.

                    if (!je.getName().equals("classes.dex"))
                        continue;

                    int startIndex = 0;
                    X509Certificate[] certChain;
                    boolean signedAsExpected = false;

                    while ((certChain = getAChain(certs, startIndex)) != null) {
                        if (certChain[0].equals(certificate)) {
                            signedAsExpected = true;
                            break;
                        }
                        // Proceed to the next chain.
                        startIndex += certChain.length;
                    }

                    if (!signedAsExpected)
                        throw new SecurityException("Jar Not Certified!");
                }
            }
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
    }
}
