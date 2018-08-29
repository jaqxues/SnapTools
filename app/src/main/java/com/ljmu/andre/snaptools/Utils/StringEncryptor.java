package com.ljmu.andre.snaptools.Utils;

import timber.log.Timber;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StringEncryptor {
    private static final SecretKey SECRET = new SecretKeySpec(HashingSecrets.STRING_ENCRYPTOR_SECRET, "AES");
    private static final HashMap<byte[], String> decryptionCache = new LinkedHashMap<byte[], String>() {
        private static final long serialVersionUID = 2391777057366798881L;

        protected boolean removeEldestEntry(Map.Entry<byte[], String> eldest) {
            return size() > 100;
        }
    };
    private static Cipher cipher;

    /**
     * ===========================================================================
     * Used to hide string literals from decompiled source code...
     * Can make it significantly more difficult to reverse engineer sourcecode
     * ===========================================================================
     *
     * @deprecated Deprecated for open source :(
     */
    @Deprecated
    public static synchronized String decryptMsg(byte[] cipherText) {
        try {
            String cachedValue = decryptionCache.get(cipherText);

            if (cachedValue != null)
                return cachedValue;

            Cipher cipher = getCipher();
            String decryptedString = new String(cipher.doFinal(cipherText), "UTF-8");

            // Cache the result to speed up repetitive decrypts ==========================
            decryptionCache.put(cipherText, decryptedString);

            return decryptedString;
        } catch (Exception e) {
            Timber.e(e);

            return "FAILED_STRING_DECRYPT";
        }
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (cipher == null) {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, SECRET);
        }

        return cipher;
    }
}
