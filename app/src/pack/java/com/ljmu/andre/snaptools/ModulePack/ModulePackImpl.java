package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;

import com.ljmu.andre.snaptools.Exceptions.ModulePackLoadAborted;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.Fragments.GeneralSettingsFragment;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KnownBugsFragment;
import com.ljmu.andre.snaptools.ModulePack.ModulesDef.Modules;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTERS_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ModulePackImpl extends ModulePack {
    private static final String SC_VERSION = "10.26.5.0";
    private static final int MINIMUM_FRAMEWORK_VERSION = 108;

    public ModulePackImpl() {
        super();
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
                switch (moduleData.getModuleName()) {
                    case "Hook Resolver":
                        modules.add(new HookResolver("Hook Resolver", false));
                        break;
                    case "Saving":
                        modules.add(new Saving("Saving", true));
                        break;
                    case "Lens Collector":
                        modules.add(new LensCollector("Lens Collector", true));
                        break;
                    case "Chat Manager":
                        modules.add(new ChatSaving("Chat Manager", true));
                        break;
                    case "Misc Changes":
                        modules.add(new MiscChanges("Misc Changes", true));
                        break;
                    case "Remove Snap Timer":
                        modules.add(new RemoveSnapTimer("Remove Snap Timer", true));
                        break;
                    case "Screenshot Bypass":
                        modules.add(new ScreenshotBypass("Screenshot Bypass", true));
                        break;
                    case "Story Blocker":
                        modules.add(new StoryBlocker("Story Blocker", true));
                        break;
                    case "Unlimited Viewing":
                        modules.add(new UnlimitedViewing("Unlimited Viewing", true));
                        break;
                    case "Sharing":
                        modules.add(new Sharing("Sharing", true));
                        break;
                    case "Custom Filters":
                        modules.add(new CustomFilters("Custom Filters", true));
                        break;
                    case "Stealth Viewing":
                        modules.add(new StealthViewing("Stealth Viewing", true));
                        break;
                    case "Account Manager":
                        modules.add(new AccountManager("Account Manager", true));
                        break;
                    case "Forced Hooks":
                        modules.add(new ForcedHooks("Forced Hooks", false));
                        break;
                    default:
                        throw new ModulePackLoadAborted("Someone did a typo");
                }

                loadState.setState(State.SUCCESS);
            } catch (Throwable e) {
                Timber.e(e, "Failed loading module: %s", moduleData.getClassName());
                loadState.setState(State.FAILED);
            }
        }

        hasLoaded = true;

        return moduleLoadStates;
    }

    @Override
    public List<ModuleLoadState> injectAllHooks(ClassLoader snapClassLoader, Context snapContext) {
        if (!hasLoaded)
            throw new IllegalStateException("Module Pack not loaded!");

        if (hasInjected) {
            Timber.d("Tried to re-inject all hooks");
            return null;
        }

        SnapDiskCache.getInstance().destroyTempDir();

        List<ModuleLoadState> hookResults = new ArrayList<>();

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
                module.injectHooks(snapClassLoader, snapContext, moduleLoadState);
            } catch (Throwable t) {
                Timber.e(t);
                moduleLoadState.fail();
            }
        }

        hasInjected = true;
        return hookResults;
    }

    @Override
    public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {

        for (ModuleLoadState state : moduleLoadStateMap.values()) {
            if (state.getState() != State.SUCCESS)
                continue;

            Module module = getModule(state.getName());
            if (module == null) {
                state.setState(State.FAILED);
                continue;
            }

            try {
                module.prepareActivity(snapClassLoader, snapActivity);
            } catch (Throwable t) {
                Timber.e(t);
                state.fail();
            }
        }
    }

    @Override
    public String isPremiumCheck() {
        return "A SnapTools Pack";
    }

    @Override
    protected String getPackSCVersion() {
        return SC_VERSION;
    }
}
