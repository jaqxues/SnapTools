package com.ljmu.andre.snaptools.Utils;

import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.ljmu.andre.snaptools.STApplication;
import timber.log.Timber;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 18.05.20 - Time 21:45.
 */
public class PathProvider {
    private static String cachedExternalDir;

    public static String getContentPath() {
        return getExternalPath() + "/" + STApplication.MODULE_TAG + "/";
    }

    public static String getContentDataPath() {
        return getContentPath() + "Data/";
    }

    public static String getDatabasesPath() {
        return getContentPath() + "Databases/";
    }

    public static String getModulesPath() {
        return getContentPath() + "ModulePack/";
    }

    public static String getSharedImagePath() {
        return getContentPath() + "SharedImages/";
    }

    public static String getDumpPath() {
        return getContentPath() + "Dumps/";
    }

    public static String getTempPath() {
        return getContentPath() + "Temp/";
    }

    public static String getLogsPath() {
        return getContentPath() + "ErrorLogs/";
    }

    public static String getCrashDumpPath() {
        return getContentPath() + "CrashDumps/";
    }

    public static String getBackupPath() {
        return getContentPath() + "Backups/";
    }

    public static String getTranslationsPath() {
        return getContentPath() + "Translations/";
    }

    public static String getExternalPath() {
        if (cachedExternalDir != null)
            return cachedExternalDir;

        cachedExternalDir = useExternalPathFallback();

        if (cachedExternalDir != null)
            return cachedExternalDir;

        try {
            cachedExternalDir = new MethodTimeout<String>() {
                @Override
                public String call() throws Exception {
                    String externalPath = null;
                    HashSet<String> externalPathSet = getExternalMounts();

                    if (!externalPathSet.isEmpty()) {
                        for (String externalMount : externalPathSet) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                externalPath = externalMount;
                                break;
                            } else {
                                if (isMounted(externalMount)) {
                                    return externalMount;
                                }
                            }
                        }
                    }

                    if (externalPath == null)
                        externalPath = useExternalPathFallback();

                    if (externalPath != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                                isMounted(externalPath))
                            return externalPath;
                    }

                    return null;
                }
            }.runWithTimeout(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Timber.e(e);
        }

        if (cachedExternalDir != null)
            return cachedExternalDir;

        throw new IllegalStateException("Mounted storage not found");
    }

    @NonNull
    private static HashSet<String> getExternalMounts() {
        HashSet<String> out = new HashSet<>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            InputStream is = process.getInputStream();
            byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (Exception e) {
            Timber.e(e);
        }

        // parse output
        String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }

        return out;
    }

    @Nullable
    private static String useExternalPathFallback() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            try {
                Class<?> environment_cls = Class.forName("android.os.Environment");
                Method setUserRequiredM = environment_cls.getMethod("setUserRequired", boolean.class);
                setUserRequiredM.invoke(null, false);
            } catch (Exception e) {
                Timber.e(e, "Get external path exception");
            }
        }

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            return null;

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isMounted(String path) {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState(new File(path))
        );
    }
}
