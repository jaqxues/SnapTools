package com.ljmu.andre.snaptools.Repackaging;

import com.topjohnwu.superuser.ShellUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static void unzip(File zip, File folder, String path, boolean junkPath) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(zip));
        unzip(in, folder, path, junkPath);
        in.close();
    }

    public static void unzip(InputStream zip, File folder, String path, boolean junkPath) throws Exception {
        try {
            ZipInputStream zipfile = new ZipInputStream(zip);
            ZipEntry entry;
            while ((entry = zipfile.getNextEntry()) != null) {
                if (!entry.getName().startsWith(path) || entry.isDirectory()) {
                    // Ignore directories, only create files
                    continue;
                }
                String name;
                if (junkPath) {
                    name = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
                } else {
                    name = entry.getName();
                }
                File dest = new File(folder, name);
                dest.getParentFile().mkdirs();
                try (FileOutputStream out = new FileOutputStream(dest)) {
                    ShellUtils.pump(zipfile, out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void signZip(X509Certificate certificate, PrivateKey privateKey, JarMap input, OutputStream output) throws Exception {
        SignAPK.signZip(certificate, privateKey, input, output);
    }
}