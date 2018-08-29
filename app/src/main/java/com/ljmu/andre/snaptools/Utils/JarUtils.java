package com.ljmu.andre.snaptools.Utils;

import timber.log.Timber;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class JarUtils {
    public static Attributes getAttributesFromJar(File file) {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(file);
            return getAttributesFromJar(jarFile);

        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                if (jarFile != null)
                    jarFile.close();
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    public static Attributes getAttributesFromJar(JarFile jarFile) {
        try {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();
        } catch (IOException e) {
            Timber.e(e);
        }

        return null;
    }
}
