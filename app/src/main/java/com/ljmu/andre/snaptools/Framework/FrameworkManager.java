package com.ljmu.andre.snaptools.Framework;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackLoadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackUnloadEvent;
import com.ljmu.andre.snaptools.Exceptions.ModuleCertificateException;
import com.ljmu.andre.snaptools.Exceptions.ModulePackFatalError;
import com.ljmu.andre.snaptools.Exceptions.ModulePackLoadAborted;
import com.ljmu.andre.snaptools.Exceptions.ModulePackNotFound;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.MODULES_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;

/**
 * ===========================================================================
 * This framework was originally intended to use multiple ModulePacks
 * however due to how the project turned out it was more efficient and
 * convenient to use a single ModulePack I decided to remove some
 * parallel loading code (To reduce thread count) and altered the
 * {@link this#checkPacksForUpdate(Activity)} function to not handle
 * UI events but instead allow the pack to do so, for more flexibility.
 * ===========================================================================
 */
public class FrameworkManager {
    private static final Object LOAD_PACKS_LOCK = new Object();
    private static final Object INSERT_PACK_LOCK = new Object();
    private static Map<String, ModulePack> modulePackMap = new LinkedHashMap<>();
    private static Map<String, String> packFailReasons = new HashMap<>();

    /**
     * ===========================================================================
     * Check all active packs for updates
     * This function was originally used to handle UI events (Such as updates)
     * ===========================================================================
     */
    public static void checkPacksForUpdate(Activity activity) {
        if (modulePackMap.isEmpty())
            return;

        for (ModulePack pack : modulePackMap.values()) {
            // Perform an update check with a callback class which holds the results
            pack.checkForUpdate(
                    activity
            );
        }
    }

    /**
     * ===========================================================================
     * Attempt to inject all of the hooks from a list of successful PackLoadStates
     * This function will use the {@link this#modulePackMap} to fetch previously
     * loaded ModulePacks. It will NOT load them in if they're not present already
     * ===========================================================================
     */
    public static List<PackLoadState> injectAllHooks(List<PackLoadState> packLoadStates,
                                                     ClassLoader snapClassLoader, Context snapContext) {

        for (PackLoadState packLoadState : packLoadStates) {
            if (packLoadState.hasFailed())
                continue;

            ModulePack modulePack = FrameworkManager.getModulePack(packLoadState.getName());
            if (modulePack != null) {
                modulePack.injectAllHooks(snapClassLoader, snapContext);

                if (modulePack.isPremiumCheck().equals("A SnapTools Pack"))
                    AnimationUtils.shouldTriggerAuthVerifier = true;
            }
        }

        return packLoadStates;

    }

    public static void prepareActivityAll(List<PackLoadState> packLoadStates,
                                          ClassLoader classLoader, Activity snapActivity) {

        for (PackLoadState packLoadState : packLoadStates) {
            if (packLoadState.hasFailed())
                continue;

            ModulePack modulePack = FrameworkManager.getModulePack(packLoadState.getName());
            if (modulePack != null) {
                modulePack.prepareActivity(classLoader, snapActivity);
            }
        }
    }

    public static ModulePack getModulePack(String key) {
        return modulePackMap.get(key);
    }

    /**
     * ===========================================================================
     * Using the preference system, attempt to load all ModulePacks that are
     * present in
     * {@link FrameworkPreferencesDef#SELECTED_PACKS}
     * <p>
     * This function will call {@link EventBus#post(Object)} with a
     * {@link PackLoadEvent} for every ModulePack that gets loaded or fails to load.
     * This can be subscribed to in order to update UI elements based on loaded packs
     * ===========================================================================
     *
     * @param context - Context used for {@link this#loadModPack(Context, String, PackLoadState)}
     * @return A List of {@link PackLoadState}
     */
    public static List<PackLoadState> loadAllModulePacks(Context context) {
        Set<String> selectPackSet = getPref(SELECTED_PACKS);
        List<PackLoadState> packLoadStates = new ArrayList<>();
        modulePackMap.clear();
        packFailReasons.clear();

        for (String selectedPack : selectPackSet) {
            PackLoadState packLoadState = new PackLoadState(selectedPack);
            PackLoadEvent packLoadEvent = null;
            String failReason = null;

            synchronized (LOAD_PACKS_LOCK) {
                packLoadStates.add(packLoadState);
            }

            try {

                packLoadEvent = loadModPack(context, selectedPack, packLoadState);

            }
            // ===========================================================================
            catch (ModulePackLoadAborted e) {
                ModuleLoadState displayState = new ModuleLoadState(e.getMessage());
                displayState.setState(State.CUSTOM);
                packLoadState
                        .addModuleLoadState(displayState)
                        .setState(State.FAILED);
                Timber.w(e, "Pack Load Aborted");
                failReason = e.getMessage();
                packFailReasons.put(selectedPack, e.getMessage());
            }
            // ===========================================================================
            catch (ModuleCertificateException e) {
                ModuleLoadState displayState = new ModuleLoadState(e.getMessage());
                displayState.setState(State.CUSTOM);
                packLoadState
                        .addModuleLoadState(displayState)
                        .setState(State.FAILED);
                Timber.e(e, "Pack Certificate error!");
                failReason = e.getMessage();
                packFailReasons.put(selectedPack, e.getMessage());
            }
            // ===========================================================================
            catch (ModulePackFatalError e) {
                ModuleLoadState displayState = new ModuleLoadState(e.getMessage());
                displayState.setState(State.CUSTOM);
                packLoadState
                        .addModuleLoadState(displayState)
                        .setState(State.FAILED);

                Timber.e(e, "Fatal pack load error!");

                failReason = e.getMessage();
                packFailReasons.put(selectedPack, e.getMessage());
            }
            // ===========================================================================
            catch (ModulePackNotFound e) {
                ModuleLoadState displayState = new ModuleLoadState(e.getMessage());
                displayState.setState(State.CUSTOM);
                packLoadState
                        .addModuleLoadState(displayState)
                        .setState(State.FAILED);
                Timber.w(e, "Module pack not found!");
                failReason = e.getMessage();
                packFailReasons.put(selectedPack, e.getMessage());
            }
            // ===========================================================================
            catch (Throwable e) {
                ModuleLoadState displayState = new ModuleLoadState(e.getMessage());
                displayState.setState(State.CUSTOM);
                packLoadState
                        .addModuleLoadState(displayState)
                        .setState(State.FAILED);
                Timber.e(e, "Unknown Issue Occurred");
                failReason = e.getMessage();
                packFailReasons.put(selectedPack, e.getMessage());
            }

            if (packLoadEvent == null)
                packLoadEvent = new PackLoadEvent(selectedPack, failReason != null ? failReason : "Unknown issue occurred");

            EventBus.getInstance().post(packLoadEvent);
        }

        return packLoadStates;
    }

    /**
     * ===========================================================================
     * Load the ModulePack with the corresponding 'packname'
     * This function is SYNCHRONOUS, should only return a successful
     * PackLoadEvent and throw an exception in any other circumstance
     * ===========================================================================
     *
     * @param context       - Context used to retrieve the installed Snapchat version
     * @param packname      - The filename of the pack to be loaded (Sans .jar extension)
     * @param packLoadState - An object to be assigned load state details
     * @return PackLoadEvent - A successful event containing the ModulePack/File
     * that can be passed to an EventBus
     * @throws ModuleCertificateException
     * @throws ModulePackFatalError
     * @throws ModulePackNotFound
     * @throws ModulePackLoadAborted
     */
    public static PackLoadEvent loadModPack(Context context, String packname,
                                            PackLoadState packLoadState)
            throws ModuleCertificateException, ModulePackFatalError, ModulePackNotFound, ModulePackLoadAborted {
        // Get an instance of ModulePackImpl from within the .jar file ===============
        File modulePackFile = new File((String) getPref(MODULES_PATH), packname + ".jar");
        ModulePack modulePack = ModulePack.getInstance(context, modulePackFile, packLoadState);

        synchronized (INSERT_PACK_LOCK) {
            modulePackMap.put(
                    modulePack.getPackName(),
                    modulePack
            );
        }

        // Load the modules within the pack into memory ==============================
        Map<String, ModuleLoadState> moduleLoadStates = modulePack.loadModules();
        packLoadState.setModuleLoadStates(moduleLoadStates);

        // Remove previous load failures for this pack ===============================
        packFailReasons.remove(packname);

//		try {
//			Crashlytics.setString("Pack Version", modulePack.getPackVersion());
//		} catch (Throwable ignored) {
//		}

        return new PackLoadEvent(modulePack, modulePackFile);
    }

    public static void deleteModPack(String name, Activity shouldKill) {
        PreferenceHelpers.removeFromCollection(SELECTED_PACKS, name, shouldKill);
        FileUtils.deletePack(ContextHelper.getActivity(), name);

        unloadModPack(name);
        EventBus.getInstance().post(new PackDeleteEvent(name));
    }

    public static boolean unloadModPack(String name) {
        synchronized (INSERT_PACK_LOCK) {
            ModulePack removedPack = modulePackMap.remove(name);
            packFailReasons.remove(name);

            if (removedPack != null)
                EventBus.getInstance().post(new PackUnloadEvent(removedPack.getPackMetaData()));

            return removedPack != null;
        }
    }

    public static void disableAllPacks() {
        synchronized (INSERT_PACK_LOCK) {
            for (ModulePack pack : modulePackMap.values())
                disableModPack(pack.getPackName());
        }
    }

    public static boolean disableModPack(String name) {
        PreferenceHelpers.removeFromCollection(
                SELECTED_PACKS,
                name
        );

        return unloadModPack(name);
    }

    public static Map<String, ModulePack> getModulePackList() {
        synchronized (INSERT_PACK_LOCK) {
            return modulePackMap;
        }
    }

    @Nullable
    public static String getFailReason(String packName) {
        return packFailReasons.get(packName);
    }

    public static void addFailReason(String packName, String failReason) {
        packFailReasons.put(packName, failReason);
    }
}
