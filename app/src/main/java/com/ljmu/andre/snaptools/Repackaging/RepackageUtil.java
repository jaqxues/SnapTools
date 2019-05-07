package com.ljmu.andre.snaptools.Repackaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * ===========================================================================
 * STEALING MAGISK CODE YO
 * ===========================================================================
 */
public class RepackageUtil {
    static String genPackageName(String prefix, int length) {
        StringBuilder builder = new StringBuilder(length);
        builder.append(prefix);
        length -= prefix.length();
        SecureRandom random = new SecureRandom();
        String base = "abcdefghijklmnopqrstuvwxyz";
        String alpha = base + base.toUpperCase();
        String full = alpha + "0123456789..........";
        char next, prev = '\0';
        for (int i = 0; i < length; ++i) {
            if (prev == '.' || i == length - 1 || i == 0) {
                next = alpha.charAt(random.nextInt(alpha.length()));
            } else {
                next = full.charAt(random.nextInt(full.length()));
            }
            builder.append(next);
            prev = next;
        }
        return builder.toString();
    }

    static int findOffset(byte buf[], byte pattern[]) {
        int offset = -1;
        for (int i = 0; i < buf.length - pattern.length; ++i) {
            boolean match = true;
            for (int j = 0; j < pattern.length; ++j) {
                if (buf[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                offset = i;
                break;
            }
        }
        return offset;
    }

    /* It seems that AAPT sometimes generate another type of string format */
    static boolean fallbackPatch(byte xml[], String from, String to) {

        byte[] target = new byte[from.length() * 2 + 2];
        for (int i = 0; i < from.length(); ++i) {
            target[i * 2] = (byte) from.charAt(i);
        }
        int offset = findOffset(xml, target);
        if (offset < 0)
            return false;
        byte[] dest = new byte[target.length - 2];
        for (int i = 0; i < to.length(); ++i) {
            dest[i * 2] = (byte) to.charAt(i);
        }
        System.arraycopy(dest, 0, xml, offset, dest.length);
        return true;
    }

    static boolean findAndPatch(byte xml[], String from, String to) {
        byte target[] = (from + '\0').getBytes();
        int offset = findOffset(xml, target);
        if (offset < 0)
            return fallbackPatch(xml, from, to);
        System.arraycopy(to.getBytes(), 0, xml, offset, to.length());
        return true;
    }


    static int inToOut(InputStream in, OutputStream out) throws IOException {
        int read, total = 0;
        byte buffer[] = new byte[4096];
        while ((read = in.read(buffer)) > 0) {
            out.write(buffer, 0, read);
            total += read;
        }
        out.flush();
        return total;
    }

    static String fmt(String fmt, Object... args) {
        return String.format(Locale.US, fmt, args);
    }
}
