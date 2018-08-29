package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.util.Pair;
import com.ljmu.andre.snaptools.Exceptions.ModulePackLoadAborted;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.Fragments.GeneralSettingsFragment;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KnownBugsFragment;
import com.ljmu.andre.snaptools.ModulePack.ModulesDef.Modules;
import com.ljmu.andre.snaptools.Utils.Constants;
import timber.log.Timber;

import java.lang.reflect.Constructor;
import java.util.*;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTERS_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ModulePackImpl extends ModulePack {
    private static final int MINIMUM_FRAMEWORK_VERSION = 56;

    public ModulePackImpl(LocalPackMetaData packMetaData, PackLoadState loadState) throws ModulePackLoadAborted {
        super(packMetaData, loadState);

        checkFrameworkVersion();
    }

    private void checkFrameworkVersion() throws ModulePackLoadAborted {
        if (Constants.getApkVersionCode() < MINIMUM_FRAMEWORK_VERSION)
            throw new ModulePackLoadAborted("Pack requires newer APK version");
    }

    /**
     * ===========================================================================
     * Helper method to quickly determine if a settings UI can be expected
     * ===========================================================================
     */
    @Override
    public boolean hasGeneralSettingsUI() {
        return true;
    }

    /**
     * ===========================================================================
     * Pull the Settings UI for this Pack
     * ===========================================================================
     */
    @Override
    public FragmentHelper[] getStaticFragments() {
        GeneralSettingsFragment settingsFragment = new GeneralSettingsFragment();
        settingsFragment.setPackName(getPackName());

        for (Modules moduleData : ModulesDef.INST.values()) {
            if (moduleData.canBeDisabled()) {
                settingsFragment.addDisplayHolder(
                        Pair.create(
                                moduleData.getModuleName(),
                                moduleData.getDescription()
                        )
                );
            }
        }

        return new FragmentHelper[]{settingsFragment, new KnownBugsFragment().buildMetaData(this)};
    }

    /**
     * ===========================================================================
     * Attempt to load the contained Modules
     * ===========================================================================
     */
    @Override
    public Map<String, ModuleLoadState> loadModules() {
        getCreateDir(FILTERS_PATH);

        Map<String, ModuleLoadState> moduleLoadStates = new LinkedHashMap<>();
        HashSet<String> disabledModules = getPref(DISABLED_MODULES);

        for (Modules moduleData : ModulesDef.INST.values()) {
            ModuleLoadState loadState = new ModuleLoadState(moduleData.getModuleName());
            moduleLoadStates.put(loadState.getName(), loadState);

            // Check if module should be skipped \\
            if (moduleData.canBeDisabled() &&
                    collectionContains(
                            DISABLED_MODULES,
                            moduleData.getModuleName()
                    )) {
                loadState.setState(State.SKIPPED);
                continue;
            }

            // ===========================================================================

            try {
                Class<? extends Module> moduleClass = moduleData.getModuleClass();
                Constructor<? extends Module> constructor = moduleClass.getConstructor(String.class, boolean.class);
                Module module = constructor.newInstance(moduleData.getModuleName(), moduleData.canBeDisabled());
                modules.add(module);

                loadState.setState(State.SUCCESS);
            } catch (Throwable e) {
                Timber.e(e, /*Failed loading module: */ decryptMsg(new byte[]{-68, 2, -80, 8, -69, -76, -23, 16, -37, -64, -61, 107, -43, 110, 14, 103, 14, 36, 116, -62, -61, -37, -18, -88, -115, 27, 10, -30, 57, -101, 59, -5})
                        + moduleData.getClassName());
                loadState.setState(State.FAILED);
            }
        }

        hasLoaded = true;

        return moduleLoadStates;
    }

    @Override
    public List<ModuleLoadState> injectAllHooks(ClassLoader snapClassLoader, Activity snapActivity) {
        if (!hasLoaded)
            throw new IllegalStateException(/*Module Pack not loaded!*/ decryptMsg(new byte[]{-78, 103, -115, -64, 116, -43, -64, 119, 126, 71, 28, -47, 49, -26, -36, -97, -72, 59, 101, 54, 98, -9, 84, 83, 94, -120, 70, 76, 122, -120, -25, -97}));

        if (hasInjected) {
            Timber.d(/*Tried to re-inject all hooks*/ decryptMsg(new byte[]{100, 70, 6, -58, 83, -111, -116, -75, 12, -40, -121, -116, -7, 69, 122, -114, 58, -40, 113, -55, -45, 121, 67, -43, 22, -98, -87, -117, -45, 7, -7, -52}));
            return null;
        }

        SnapDiskCache.getInstance().destroyTempDir();

        List<ModuleLoadState> hookResults = new ArrayList<>();
        Map<String, ModuleLoadState> moduleLoadStateMap = getPackLoadState().getModuleLoadStates();

        for (ModuleLoadState moduleLoadState : moduleLoadStateMap.values()) {
            hookResults.add(moduleLoadState);

            if (moduleLoadState.getState() != State.SUCCESS)
                continue;

            Module module = getModule(moduleLoadState.getName());
            if (module == null) {
                moduleLoadState.setState(State.FAILED);
                continue;
            }

            try {
                module.injectHooks(snapClassLoader, snapActivity, moduleLoadState);
            } catch (Throwable t) {
                Timber.e(t);
                moduleLoadState.fail();
            }
        }

        packLoadState.refreshPackLoadState();

        hasInjected = true;
        return hookResults;
    }

    @Override
    public String isPremiumCheck() {
        return /*A SnapTools Pack*/ decryptMsg(new byte[]{92, -36, -124, 121, 54, 7, -12, 55, 118, -100, -19, -37, 1, -117, 98, 87, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66});
    }
}
