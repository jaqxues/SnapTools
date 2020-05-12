package com.ljmu.andre.GsonPreferences;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.common.base.MoreObjects;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.GsonPreferences.PreferenceUtils.PreferenceMapDeserialiser;
import com.ljmu.andre.snaptools.HangErrorActivity;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.MethodTimeout;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unchecked")
public class Preferences {

    static final Object LOCK = new Object();
    private static final String FILE_NAME = "SnapTools_Preferences.json";
    public static PreferenceObserver preferenceObserver;
    private static PreferenceMap preferenceMap;
    private static File preferenceFile;
    private static Gson gson;
    private static String cachedExternalDir;
    private static AtomicBoolean isInitialised = new AtomicBoolean(false);

    public static AtomicBoolean getIsInitialised() {
        return isInitialised;
    }

    public static boolean init(
            String prefsFilePath) throws IOException {
        if (isInitialised.get()) {
            Timber.w("Already initialised preferences");
            return true;
        }

        if (Constants.getApkFlavor().equals("beta")) {
            DeadlockMonitor.init();
        }

        Class type = new TypeToken<Map<String, Preference>>() {
        }.getClass();

        Timber.d("Type: " + type);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .setLenient()
                .registerTypeHierarchyAdapter(Map.class, new PreferenceMapDeserialiser())
                .create();

        synchronized (LOCK) {
            preferenceFile = FileUtils.createFile(prefsFilePath, FILE_NAME);

            if (preferenceFile == null) {
                Timber.e("Couldn't create Preference File");
                throw new IOException("Couldn't create Preference File");
            }

            Timber.d("Pref: " + preferenceFile.getAbsolutePath());

            loadPreferenceObserver();

            loadPreferenceMap();
            Timber.d("Initialised Preferences!");
        }

        isInitialised.set(true);
        return true;
    }

    public static void loadPreferenceObserver() {
        preferenceObserver = new PreferenceObserver(preferenceFile.getAbsolutePath());
        preferenceObserver.startWatching();
    }

    public static void loadPreferenceMap() {
        synchronized (LOCK) {
            Closer closer = Closer.create();

            try {
                BufferedReader reader = closer.register(
                        new BufferedReader(new FileReader(preferenceFile))
                );

                preferenceMap = gson.fromJson(reader, PreferenceMap.class);

                if (preferenceMap == null)
                    preferenceMap = new PreferenceMap();

                Timber.d("Loaded Preferences: " + preferenceMap);
            } catch (Throwable t) {
                Timber.e(t, "Failed to load preferences");
            } finally {
                try {
                    closer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @RequiresFramework(78)
    public static void forceReset() {
        synchronized (LOCK) {
            preferenceMap.clear();
            preferenceObserver.notifyLocalChange();
            PreferenceUtils.saveMap(preferenceFile, preferenceMap, gson);
        }
    }

    public static String getPreferenceFilename() {
        return FILE_NAME;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nullable
    public static File getCreateDir(Preference preference) {
        File dir = new File((String) getPref(preference));
        dir.mkdirs();

        if (!dir.exists())
            return null;

        return dir;
    }

    public static <T> T getPref(Preference preference) {
        initCheck();

        Object preferenceVal = null;
        Preference storedPreference;

        synchronized (LOCK) {
            storedPreference = preferenceMap.get(preference.getName());
        }

        if (storedPreference != null)
            preferenceVal = storedPreference.getValue();

        if (preference.getConditionalCheck() != null)
            preferenceVal = ConditionalCheck.triggerCheck(preference, preferenceVal);

        if (preferenceVal == null)
            return (T) preference.getDefaultVal();

        return (T) preferenceVal;
    }

    private static void initCheck() throws IllegalStateException {
        if (!isInitialised.get())
            throw new IllegalStateException("Preference system not Initialised!");
    }

    public static void putPref(Preference preference, Object value) {
        initCheck();
        Timber.d("Putting [Pref: %s] [Value: %s]", preference.getName(), String.valueOf(value));

        synchronized (LOCK) {
            preference.setValue(value);
            Object oldValue = preferenceMap.put(preference.getName(), preference);

            if (oldValue == null || !oldValue.equals(value)) {
                preferenceObserver.notifyLocalChange();
                PreferenceUtils.saveMap(preferenceFile, preferenceMap, gson);
            }
        }
    }

    public static void removePref(Preference preference) {
        removePref(preference.getName());
    }

    public static void removePref(String preferenceName) {
        initCheck();
        Timber.d("Removing [Pref: %s]", preferenceName);

        synchronized (LOCK) {
            Object oldValue = preferenceMap.remove(preferenceName);

            if (oldValue != null) {
                preferenceObserver.notifyLocalChange();
                PreferenceUtils.saveMap(preferenceFile, preferenceMap, gson);
            }
        }
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
                            if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
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
                        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP &&
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

        HangErrorActivity.start("Couldn't find a mounted storage path");
        throw new IllegalStateException("Mounted storage not found");
    }

    @Nullable
    private static String useExternalPathFallback() {

        if (VERSION.SDK_INT < VERSION_CODES.P) {
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

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    private static boolean isMounted(String path) {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState(new File(path))
        );
    }

    public static class Preference extends Constant {
        @SerializedName("type")
        private final Class type;
        private final Object defaultVal;
        private final ConditionalCheck conditionalCheck;
        @SerializedName("value")
        private Object value;

        public Preference(
                @NonNull String name,
                @Nullable Object defaultVal,
                @Nullable Class type) {
            this(name, null, defaultVal, type, null);
        }

        Preference(
                @NonNull String name,
                @Nullable Object value,
                @Nullable Object defaultVal,
                @Nullable Class type,
                @Nullable ConditionalCheck conditionalCheck) {
            super(name);
            this.value = value;
            this.defaultVal = defaultVal;
            this.type = type;
            this.conditionalCheck = conditionalCheck;
        }

        Preference(
                @NonNull String name,
                @Nullable Object value,
                @Nullable Object defaultVal,
                @Nullable Class type) {
            this(name, value, defaultVal, type, null);
        }

        public Preference(
                @NonNull String name,
                @Nullable Object defaultVal,
                @Nullable Class type,
                @Nullable ConditionalCheck conditionalCheck) {
            super(name);
            this.defaultVal = defaultVal;
            this.type = type;
            this.conditionalCheck = conditionalCheck;
        }

        Object getValue() {
            return value;
        }

        void setValue(Object value) {
            this.value = value;
        }

        public <T> T getDefaultVal() {
            return (T) defaultVal;
        }

        public Class getType() {
            return type;
        }

        ConditionalCheck getConditionalCheck() {
            return conditionalCheck;
        }

        @NonNull
        @Override
        public String getName() {
            return Assert.notNull("Null Preference Name", super.getName());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("name", getName())
                    .add("type", type)
                    .add("value", value)
                    .add("defaultVal", defaultVal)
                    .toString();
        }
    }

    public static abstract class ConditionalCheck {
        private static Object triggerCheck(@NonNull Preference preference, @Nullable Object preferenceVal) {
            try {
                if (preferenceVal != null &&
                        !preferenceVal.equals(preference.getDefaultVal()))
                    return preferenceVal;

                return preference.getConditionalCheck().performConditionCheck(preference, preferenceVal);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        }

        @NonNull
        protected abstract Object performConditionCheck(Preference preference, Object preferenceVal);
    }
}
