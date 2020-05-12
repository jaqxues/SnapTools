package com.ljmu.andre.GsonPreferences;

import androidx.annotation.Nullable;

import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ljmu.andre.GsonPreferences.Preferences.Preference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class PreferenceUtils {
    private static final Object WRITE_LOCK = new Object();

    static void saveMap(File preferenceFile, PreferenceMap preferenceMap, Gson gson) {
        Closer closer = Closer.create();
        PreferenceMap copiedPrefs = new PreferenceMap(preferenceMap);

        Observable.fromCallable(
                () -> {
                    Timber.d("Entering write lock");
                    synchronized (WRITE_LOCK) {
                        Timber.d("Entered write lock");
                        BufferedWriter writer = closer.register(
                                new BufferedWriter(
                                        new FileWriter(preferenceFile)
                                )
                        );

                        gson.toJson(copiedPrefs, writer);
                        writer.flush();
                        Timber.d("Exiting write lock");
                    }
                    Timber.d("Exited write lock");

                    return new Object();
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(@NonNull Object object) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);

                        try {
                            closer.close();
                        } catch (IOException ignored) {
                            Timber.w("Couldn't close preference file?");
                        }
                    }

                    @Override
                    public void onComplete() {

                        try {
                            closer.close();
                            Timber.d("Closing preference file");
                        } catch (IOException ignored) {
                            Timber.w("Couldn't close preference file?");
                        }
                    }
                });
    }

    static class PreferenceMapDeserialiser implements JsonDeserializer<PreferenceMap>, JsonSerializer<PreferenceMap> {
        private static final Map<String, Class<?>> classCache = new HashMap<>(16);
        private static final Object SERIALIZE_LOCK = new Object();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        @Override
        public PreferenceMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Timber.d(json.toString());
            PreferenceMap rootMap = new PreferenceMap();

            JsonArray rootArray;

            try {
                rootArray = (JsonArray) json;
            } catch (ClassCastException cce) {
                Timber.e(new Exception("Malformed Preference Json: " + json));
                return rootMap;
            }

            for (Object aRootArray : rootArray) {
                try {
                    JsonObject jsonPreference = (JsonObject) aRootArray;
                    String name = jsonPreference.get("name").getAsString();
                    String strType = jsonPreference.get("type").getAsString();
                    Class<?> resolvedClass = getClassFromString(strType);

                    if (resolvedClass == null) {
                        Timber.e(new Exception("Couldn't resolve preference class: " + jsonPreference));
                        continue;
                    }

                    Object value = gson.fromJson(jsonPreference.get("value"), resolvedClass);
                    Preference preference = new Preference(name, value, null, resolvedClass);

                    rootMap.put(name, preference);
                } catch (Throwable e) {
                    Timber.e(new Exception("Failed on preference: " + aRootArray.toString(), e));
                }
            }

            return rootMap;
        }

        @Nullable
        private Class<?> getClassFromString(String type) {
            Class<?> resolvedClass = classCache.get(type);

            if (resolvedClass != null)
                return resolvedClass;

            try {
                resolvedClass = Class.forName(type);
                classCache.put(type, resolvedClass);
            } catch (ClassNotFoundException e) {
                Timber.e(e);
            }

            return resolvedClass;
        }

        @Override
        public JsonElement serialize(PreferenceMap src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray rootMap = new JsonArray();

            synchronized (SERIALIZE_LOCK) {
                PreferenceMap copyMap = new PreferenceMap(src);

                for (Entry<String, Preference> entry : copyMap.entrySet()) {
                    try {
                        Preference preference = entry.getValue();

                        JsonObject jsonPreference = new JsonObject();
                        jsonPreference.addProperty("name", entry.getKey());
                        Class preferenceType = preference.getType();

                        jsonPreference.addProperty("type", preferenceType.getName());

                        Object prefValue = preference.getValue();

//						if (prefValue instanceof Map)
                        jsonPreference.add("value", gson.toJsonTree(prefValue));
//						else
//							jsonPreference.add("value", context.serialize(preference.getValue()));

                        rootMap.add(jsonPreference);
                    } catch (Exception e) {
                        Timber.e(new Exception("Error serialising preference: " + entry, e));
                    }
                }
            }

            return rootMap;
        }
    }
}
