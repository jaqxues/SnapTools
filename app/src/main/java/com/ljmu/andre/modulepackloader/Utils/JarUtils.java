package com.ljmu.andre.modulepackloader.Utils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class JarUtils {
    @NonNull
    public static Attributes getMainAttributes(JarFile jarFile) throws IOException {
        Manifest manifest = Assert.notNull("No manifest found in jar!", jarFile.getManifest());
        return Assert.notNull("No main attributes found in manifest!", manifest.getMainAttributes());
    }
}
