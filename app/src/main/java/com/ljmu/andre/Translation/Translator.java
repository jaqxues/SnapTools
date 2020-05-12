package com.ljmu.andre.Translation;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Networking.Helpers.GetTranslations;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener.ErrorObjectResultListener;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.RemoteConfig;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Translator {
    private static final String TRANSLATION_GITHUB_URL = "https://raw.githubusercontent.com/Andrerm124/SnapToolsTranslations/master/";
    private static final List<String> AVAILABLE_TRANSLATIONS = Arrays.asList("English", "German", "French", "Polski", "TestLanguage");
    private static final Object MAP_LOCK = new Object();
    private static final Map<String, Translation> TRANSLATION_MAP = new HashMap<>(64);
    private static final Set<Class<? extends ConstantDefiner<Translation>>> INITIALISED_DEFINERS = new HashSet<>(6);
    private static GetTranslations translationProvider;
    private static AtomicBoolean atomicHasInitialised = new AtomicBoolean(false);

    @RequiresFramework(73)
    public static void reset() {
        TRANSLATION_MAP.clear();
        atomicHasInitialised.set(false);
        INITIALISED_DEFINERS.clear();
        translationProvider = null;
    }

    @RequiresFramework(73)
    public static void init(Activity activity, @Nullable String translationFilename, Callable<Boolean> completionCallback) {
        init(
                activity,
                TRANSLATION_GITHUB_URL,
                translationFilename,
                completionCallback
        );
    }

    @RequiresFramework(73)

    public static void init(Activity activity, String translationRootUrl, @Nullable String translationFilename, Callable<Boolean> completionCallback) {
        if (atomicHasInitialised.get()) {
            Timber.w("Already initialised translations");
            completionCallback.call(true);
            return;
        }

        if (translationFilename == null || translationFilename.equals("English.xml")) {
            Timber.d("Translation set to English, using hardcoded values");
            TRANSLATION_MAP.clear();
            completionCallback.call(true);
            return;
        }

        translationProvider = new GetTranslations(translationRootUrl, translationFilename);

        translationProvider
                .setShouldPrioritiseCache(true)
                .smartFetch(
                        activity,
                        new ObjectResultListener<byte[]>() {
                            @Override
                            public void success(String message, byte[] object) {
//								Timber.d("Translation file: " + new String(object));
                                TranslationXmlParser.readXmlFile(new ByteArrayInputStream(object), translation -> {
                                    Timber.d("New translation inbound: "
                                            + translation);

                                    synchronized (MAP_LOCK) {
                                        if (TRANSLATION_MAP.containsKey(translation.getName())) {
//											Timber.d("Translation already exists... Reassigning text: " + translation.getName());
                                            Translation existingTranslation = TRANSLATION_MAP.get(translation.getName());
                                            TRANSLATION_MAP.put(translation.getName(), translation.mergeConstantMetaData(activity, existingTranslation));
                                        } else {
//											Timber.d("Translation doesn't exist... Creating new: " + translation.getName());
                                            TRANSLATION_MAP.put(translation.getName(), translation);
                                        }
                                    }
                                });

                                synchronized (MAP_LOCK) {
                                    Timber.d("Translation map size: "
                                            + TRANSLATION_MAP.size());
                                }

                                translationsFetched(activity);
                                completionCallback.call(true);
                            }

                            @Override
                            public void error(String message, Throwable t, int errorCode) {
                                completionCallback.call(false);
                            }
                        }
                );
    }

    private static void translationsFetched(Activity activity) {
        atomicHasInitialised.set(true);

        if (translationProvider.shouldUseCacheExposed()) {
            translationProvider.getFromServer(
                    activity,
                    new ErrorObjectResultListener<byte[]>() {
                        @Override
                        public void error(String message, Throwable t, int errorCode) {
                            Timber.e(t, message);
                        }
                    }
            );
        }
    }

    @RequiresFramework(73)
    public static List<String> getAvailableTranslations() {
        return AVAILABLE_TRANSLATIONS;
    }

    @RequiresFramework(73)

    public static <T extends ConstantDefiner<Translation>> void initTranslationDefinitions(Context context, T translationConstantDefiner, boolean shouldPurgeAfter) {
        synchronized (MAP_LOCK) {
            //noinspection SuspiciousMethodCalls
            if (INITIALISED_DEFINERS.contains(translationConstantDefiner.getClass())) {
                Timber.w("Already initialised definer: "
                        + translationConstantDefiner.getClass());
                return;
            }

            Timber.d("Initialising existing translation definitions");

            for (Translation translation : translationConstantDefiner.values()) {
                if (TRANSLATION_MAP.containsKey(translation.getName())) {
//					Timber.d("Translation already exists... Merging metadata text: " + translation.getName());
                    Translation existingTranslation = TRANSLATION_MAP.get(translation.getName());
                    TRANSLATION_MAP.put(existingTranslation.getName(), existingTranslation.mergeConstantMetaData(context, translation));
                } else {
//					Timber.d("Translation doesn't exist... Creating new: " + translation.getName());
                    TRANSLATION_MAP.put(translation.getName(), translation);
                }
            }

            if (shouldPurgeAfter)
                translationConstantDefiner.purgeCache();

            //noinspection unchecked
            INITIALISED_DEFINERS.add((Class<? extends ConstantDefiner<Translation>>) translationConstantDefiner.getClass());
        }
    }

    @RequiresFramework(73)

    public static void translateFragment(Fragment fragment) {
        Class<? extends ComponentCallbacks> fragmentClass = fragment.getClass();
        Timber.d("Attempting to translate fragment: "
                + fragmentClass.getSimpleName());

        synchronized (MAP_LOCK) {
            for (Translation translation : TRANSLATION_MAP.values()) {
                Class<? extends ComponentCallbacks> activityOrFragmentClass = translation.getActivityOrFragmentClass();

                if (activityOrFragmentClass != null && activityOrFragmentClass.equals(fragmentClass)) {
                    int translationResourceId = translation.getResourceId(fragment.getContext());
//					Timber.d("Attempting to translate resource %s to text %s", translationResourceId, translation.getText());

                    bindTranslationToView(translation, getView(fragment.getView(), translationResourceId));
                }
            }
        }
    }

    private static void bindTranslationToView(Translation translation, View view) {
        if (view == null) {
            Timber.e(new Exception("Tried to bind translation to null view:"
                    + translation.getName()));
            return;
        }

        if (view instanceof TextView) {
            Timber.d("Binding trnslation: " + translation.toString());
            ((TextView) view).setText(translate(translation));
        } else {
            Timber.e(new Exception(
                    String.format("Unknown view type when binding translation [ViewType: %s][Translation: %s]",
                            view.getClass().getSimpleName(), translation.getName())
            ));
        }
    }

    @RequiresFramework(73)

    public static String translate(Translation translation) {
        Translation mappedTranslation;

        synchronized (MAP_LOCK) {
            mappedTranslation = TRANSLATION_MAP.get(translation.getName());
        }

        if (mappedTranslation == null)
            return translation.getText();

        return mappedTranslation.getText();
    }

    @RequiresFramework(73)

    public static void translateActivity(Activity activity) {
        Class<? extends ComponentCallbacks> activityClass = activity.getClass();
        Timber.d("Attempting to translate activity: "
                + activityClass.getSimpleName());

        synchronized (MAP_LOCK) {
            for (Translation translation : TRANSLATION_MAP.values()) {
                Class<? extends ComponentCallbacks> activityOrFragmentClass = translation.getActivityOrFragmentClass();

                if (activityOrFragmentClass != null && activityOrFragmentClass.equals(activityClass)) {
                    int translationResourceId = translation.getResourceId(activity);
//					Timber.d("Attempting to translate resource %s to text %s", translationResourceId, translation.getText());
                    bindTranslationToView(translation, getView(activity, translationResourceId));
                }
            }
        }
    }

    public static class Translation extends Constant {
        private String text;
        private String packVersion;
        private String packFlavour;
        private Class<? extends ComponentCallbacks> activityOrFragmentClass;
        private Integer resourceId;
        private String resourceIdString;

        public Translation(@Nullable String name, String text) {
            super(name);
            this.text = text;
        }

        public Translation(@Nullable String name, String text,
                           Class<? extends ComponentCallbacks> activityOrFragmentClass, int resourceId) {
            super(name);
            this.text = text;
            this.activityOrFragmentClass = activityOrFragmentClass;
            this.resourceId = resourceId;
        }

        public String getText() {
            return text;
        }

        public Translation setText(String text) {
            this.text = text;
            return this;
        }

        public String getPackVersion() {
            return packVersion;
        }

        public Translation setPackVersion(String packVersion) {
            this.packVersion = packVersion;
            return this;
        }

        public String getPackFlavour() {
            return packFlavour;
        }

        public Translation setPackFlavour(String packFlavour) {
            this.packFlavour = packFlavour;
            return this;
        }

        public Translation mergeConstantMetaData(Context context, Translation constantTranslation) {
            this.activityOrFragmentClass = constantTranslation.getActivityOrFragmentClass();
            this.resourceId = constantTranslation.getResourceId(context);
            return this;
        }

        public Class<? extends ComponentCallbacks> getActivityOrFragmentClass() {
            return activityOrFragmentClass;
        }

        public Translation setActivityOrFragmentClass(Class<? extends ComponentCallbacks> activityOrFragmentClass) {
            this.activityOrFragmentClass = activityOrFragmentClass;
            return this;
        }

        public int getResourceId(@Nullable Context context) {
            if (resourceId == null) {
                if (resourceIdString == null)
                    return -1;

                Context moduleContext = getModuleContext(context, true);

                if (moduleContext == null)
                    return -1;

                return resourceId = getId(moduleContext, resourceIdString);
            }

            return resourceId;
        }

        public Translation setResourceId(int resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Translation setResourceIdString(String resourceIdString) {
            this.resourceIdString = resourceIdString;
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("name", getName())
                    .add("text", text)
                    .add("packVersion", packVersion)
                    .add("packFlavour", packFlavour)
                    .add("activityOrFragmentClass", activityOrFragmentClass)
                    .add("resourceId", resourceId)
                    .toString();
        }
    }

    public static void fetchedRemoteConfig(Activity activity, RemoteConfig config) {
        List<String> fetched = Arrays.asList(config.getString("translation_files").split(", "));
        if (!AVAILABLE_TRANSLATIONS.containsAll(fetched)) {
            String locale = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            if (!AVAILABLE_TRANSLATIONS.contains(locale) && fetched.contains(locale)) {
                DialogFactory.createBasicMessage(
                        activity,
                        "New " + locale + "Translation",
                        "New Translations have just been fetched. " + locale + "has been uploaded. You can change the Translation in Settings."
                ).show();
            }
            AVAILABLE_TRANSLATIONS.clear();
            AVAILABLE_TRANSLATIONS.addAll(fetched);
        }
    }
}
