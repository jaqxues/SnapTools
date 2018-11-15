package com.ljmu.andre.snaptools.Framework;

import android.app.Activity;
import android.content.Context;

import com.ljmu.andre.snaptools.Exceptions.ModulePackLoadAborted;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.ModulePack.ModulePackImpl;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.StringEncryptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class ModulePack {
    public static final String VERSION_MISMATCH_ERROR = "Current Snapchat version not supported by this pack";

    // ===========================================================================

    protected List<Module> modules = new ArrayList<>();
    protected boolean hasLoaded;
    protected boolean hasInjected;
    protected Map<String, ModuleLoadState> moduleLoadStateMap;

    public void setLoadStates(Map<String, ModuleLoadState> moduleLoadStateMap) {
        this.moduleLoadStateMap = moduleLoadStateMap;
    }
// ===========================================================================

    protected ModulePack() {

    }

    // ===========================================================================

    public abstract boolean hasGeneralSettingsUI();

    public abstract FragmentHelper[] getStaticFragments();

    /**
     * ===========================================================================
     * An abstract function to allow the {@link ModulePack#PACK_CLASSNAME} class to
     * perform the loading phase of its contained modules.
     * ===========================================================================
     */
    public abstract Map<String, ModuleLoadState> loadModules();

    /**
     * ===========================================================================
     * An abstract function to allow the {@link this#PACK_CLASSNAME} class to
     * perform the hook injection phase of the previously loaded {@link this#modules}
     * ===========================================================================
     */
    public abstract List<ModuleLoadState> injectAllHooks(ClassLoader snapClassLoader, Context snapContext);

    /**
     * ===========================================================================
     * To avoid the Hangs for Snapchat, we hook as early as possible. Since we cannot provide an
     * activity at this point, it makes sense to provide an "Activity Hook" to the Modules to
     * initialize and prepare Fields etc.
     * ===========================================================================
     */
    public abstract void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity);
    // ===========================================================================

    public List<Module> getModules() {
        return modules;
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.name().equalsIgnoreCase(name))
                return module;
        }

        return null;
    }

    public String getPackDisplayName() {
        return "Implemented ST Pack";
    }

    public boolean isDevelopment() {
        return Constants.isApkDebug();
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    public boolean hasInjected() {
        return hasInjected;
    }

    /**
     * ===========================================================================
     * A function that can be used to determine whether the ModulePack is premium
     * or not based on the value that is returned.
     * <p>
     * The idea of this function is to disguise the return value as a ModulePack
     * tag which combined with {@link StringEncryptor#decryptMsg(byte[])}
     * can make it very difficult to reverse engineer its intent.
     * ===========================================================================
     */
    public String isPremiumCheck() {
        return "SnapTools Pack";
    }

    public String getPackType() {
        return "Premium";
    }

    protected abstract String getPackSCVersion();

    public String getPackName() {
        return "Implemented ST Pack";
    }

    public static ModulePack getInstance(
            Context context) throws ModulePackLoadAborted {

        // Ensure the installed SC version matches the packs =========================
        String installedVersion = MiscUtils.getInstalledSCVer(context);

        ModulePack modulePack = new ModulePackImpl();
        if (installedVersion == null || !modulePack.getPackSCVersion().equals(installedVersion))
            throw new ModulePackLoadAborted(VERSION_MISMATCH_ERROR);
        return modulePack;
    }
}
