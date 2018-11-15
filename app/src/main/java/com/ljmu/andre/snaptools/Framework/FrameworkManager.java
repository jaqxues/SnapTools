package com.ljmu.andre.snaptools.Framework;

import android.app.Activity;
import android.content.Context;

import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackLoadEvent;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.ModulePack.ModulePackImpl;
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef;

import java.util.Map;

import timber.log.Timber;

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
    private static ModulePack modulePack;
    private static String packFailReason;

    /**
     * ===========================================================================
     * Check all active packs for updates
     * This function was originally used to handle UI events (Such as updates)
     * ===========================================================================
     */
    public static void checkPacksForUpdate(Activity activity) {
        // TODO
    }

    public static void injectAllHooks(ClassLoader snapClassLoader, Context snapContext) {
        if (modulePack == null)
            modulePack = new ModulePackImpl();
        modulePack.injectAllHooks(snapClassLoader, snapContext);
    }

    public static void prepareActivityAll(ClassLoader classLoader, Activity snapActivity) {
        if (modulePack != null) {
            modulePack.prepareActivity(classLoader, snapActivity);
        }
    }

    public static ModulePack getModulePack() {
        if (modulePack == null)
            modulePack = new ModulePackImpl();
        return modulePack;
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
     */
    public static void loadAllModulePacks(Context context) {

        PackLoadEvent packLoadEvent = null;
        String failReason = null;
        try {

            packLoadEvent = loadModPack(context);

        }
        // ===========================================================================
        catch (Throwable e) {
            Timber.e(e);
            packFailReason = e.getMessage();
        }

        if (packLoadEvent == null)
            packLoadEvent = new PackLoadEvent(packFailReason);

        EventBus.getInstance().post(packLoadEvent);
    }

    /**
     * ===========================================================================
     * Load the ModulePack with the corresponding 'packname'
     * This function is SYNCHRONOUS, should only return a successful
     * PackLoadEvent and throw an exception in any other circumstance
     * ===========================================================================
     *
     * @param context - Context used to retrieve the installed Snapchat version
     * @return PackLoadEvent - A successful event containing the ModulePack/File
     * that can be passed to an EventBus
     */
    public static PackLoadEvent loadModPack(Context context) {
        if (modulePack == null)
            modulePack = new ModulePackImpl();


        // Load the modules within the pack into memory ==============================
        Map<String, ModuleLoadState> moduleLoadStates = modulePack.loadModules();
        modulePack.setLoadStates(moduleLoadStates);

        // Remove previous load failures for this pack ===============================
        packFailReason = null;
        return new PackLoadEvent(modulePack);
    }
}
