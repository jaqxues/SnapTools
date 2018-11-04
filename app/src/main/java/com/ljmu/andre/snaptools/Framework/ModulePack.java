package com.ljmu.andre.snaptools.Framework;

import android.app.Activity;
import android.content.Context;

import com.ljmu.andre.snaptools.Exceptions.ModuleCertificateException;
import com.ljmu.andre.snaptools.Exceptions.ModulePackFatalError;
import com.ljmu.andre.snaptools.Exceptions.ModulePackLoadAborted;
import com.ljmu.andre.snaptools.Exceptions.ModulePackNotFound;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.MainActivity;
import com.ljmu.andre.snaptools.Networking.Helpers.CheckPackUpdate;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.StringEncryptor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import dalvik.system.DexClassLoader;

import static com.ljmu.andre.snaptools.Utils.PackUtils.getFlavourFromAttributes;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class ModulePack {
    public static final String VERSION_MISMATCH_ERROR = "Current Snapchat version not supported by this pack";
    /**
     * ===========================================================================
     * A hardcoded path to the ModulePackImpl class (That should extend this class)
     * is required as reflection will need to be used to instantiate it
     * ===========================================================================
     */
    private static final String PACK_CLASSNAME = "com.ljmu.andre.snaptools.ModulePack.ModulePackImpl";

    // ===========================================================================

    protected List<Module> modules = new ArrayList<>();
    protected PackLoadState packLoadState;
    protected boolean hasLoaded;
    protected boolean hasInjected;
    private LocalPackMetaData packMetaData;

    // ===========================================================================

    protected ModulePack(LocalPackMetaData packMetaData, PackLoadState packLoadState) {
        this.packMetaData = packMetaData;
        this.packLoadState = packLoadState;
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
        return packMetaData.getDisplayName();
    }

    public boolean isDevelopment() {
        return packMetaData.isDeveloper();
    }

    /**
     * ===========================================================================
     * Retrieve the PackMetaData of this ModulePack.
     * See {@link PackMetaData} for available variables as {@link LocalPackMetaData}
     * is only used to differentiate between PackMetaData that was created from a
     * .jar file located on the device, compared to {@link ServerPackMetaData} that
     * has been generated by ServerSide data.
     * <p>
     * It should also be noted that {@link LocalPackMetaData} has a different
     * {@link StatefulListable} UI implementation and so will look different
     * to the server metadata equivalent.
     * ===========================================================================
     */
    public LocalPackMetaData getPackMetaData() {
        return packMetaData;
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

    /**
     * ===========================================================================
     * Retrieves the PackLoadState of ModulePack after {@link this#loadModules()}
     * has been called. It is used to determine whether the ModulePack and its
     * subsequent Modules have loaded successfully.
     * <p>
     * See {@link LoadState.State} for possible states.
     * ===========================================================================
     */
    public PackLoadState getPackLoadState() {
        return packLoadState;
    }

    /**
     * ===========================================================================
     * Utility function to fill in the data required for a pack update check
     * ===========================================================================
     */
    public void checkForUpdate(Activity activity) {
        CheckPackUpdate.performCheck(
                activity,
                getPackType(),
                getPackSCVersion(),
                getPackVersion(),
                getPackName(),
                getPackFlavour()
        );
    }

    public String getPackType() {
        return packMetaData.getType();
    }

    protected String getPackSCVersion() {
        return packMetaData.getScVersion();
    }

    public String getPackVersion() {
        return packMetaData.getPackVersion();
    }

    public String getPackName() {
        return packMetaData.getName();
    }

    public String getPackFlavour() {
        return packMetaData.getFlavour();
    }

    /**
     * ===========================================================================
     * Instantiate an implementation of this ModulePack class from within
     * a .jar file. This function will always return a valid ModulePack
     * object, otherwise it will throw a categorised exception.
     * <p>
     * This is a fairly heavy function (~200ms) and should be used sparingly
     * or asynchronously when possible (However when used for injecting hooks,
     * it is likely best done synchronously to not miss hooks on methods called
     * early)
     * <p>
     * The .jar file is required to be signed by either the release or debug
     * keystore (Based on the corresponding app build variant) so as to not
     * allow malicious code to be loaded by third party apps
     * ===========================================================================
     *
     * @param context
     * @param modulePackFile
     * @param packLoadState  - An object that will be updated to reflect the load state of this object
     * @return
     * @throws ModuleCertificateException
     * @throws ModulePackFatalError
     * @throws ModulePackNotFound
     * @throws ModulePackLoadAborted
     */
    public static ModulePack getInstance(
            Context context,
            File modulePackFile,
            PackLoadState packLoadState) throws ModuleCertificateException, ModulePackFatalError, ModulePackNotFound, ModulePackLoadAborted {
        Attributes mainAttributes = getAttributesAndVerify(modulePackFile);

        if (mainAttributes == null)
            throw new ModulePackFatalError("Module pack doesn't contain meta-data");

        // Load the metadata embedded into the manifest ==============================
        LocalPackMetaData packMetaData = (LocalPackMetaData) new LocalPackMetaData()
                .setName(modulePackFile.getName().replace(".jar", ""))
                .setType(mainAttributes.getValue("Type"))
                .setPackVersion(mainAttributes.getValue("PackVersion"))
                .setScVersion(mainAttributes.getValue("SCVersion"))
                .setDevelopment(Boolean.valueOf(mainAttributes.getValue("Development")))
                .setFlavour(getFlavourFromAttributes(mainAttributes, modulePackFile));

        // Ensure the installed SC version matches the packs =========================
        String installedVersion = MiscUtils.getInstalledSCVer(context);

        if (installedVersion == null || !packMetaData.getScVersion().equals(installedVersion))
            throw new ModulePackLoadAborted(VERSION_MISMATCH_ERROR);

        DexClassLoader dexClassLoader = createClassLoader(FileUtils.getCodeCacheDir(context), modulePackFile);
        return instantiatePack(dexClassLoader, packMetaData, packLoadState);
    }

    /**
     * ===========================================================================
     * Get the Attributes of a Jar file and verify that it has been signed
     * by the SnapToolsKeystore key
     * ===========================================================================
     */
    private static Attributes getAttributesAndVerify(File modulePackFile) throws ModulePackNotFound, ModuleCertificateException {
        JarFile jarFile = null;

        try {
            if (!modulePackFile.exists())
                throw new ModulePackNotFound("Pack not found: "
                        + modulePackFile.getName());

            jarFile = new JarFile(modulePackFile);

            Manifest manifest = jarFile.getManifest();

//			Security.verifyJar(manifest, jarFile);
            return manifest.getMainAttributes();
        } catch (IOException e) {
            throw new ModuleCertificateException("ModulePack Certification Failed", e);
        } catch (SecurityException e) {
            throw new ModuleCertificateException("Module Pack Not Certified!", e);
        } finally {
            try {
                if (jarFile != null)
                    jarFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ===========================================================================
     * Create a classloader that will be used to instantiate the implemented
     * version of this ModulePack class using {@link this#PACK_CLASSNAME}
     * ===========================================================================
     *
     * @param codeCacheDir   - A directory that is optimised for .dex files
     *                       see {@link FileUtils#getCodeCacheDir(Context)}
     * @param modulePackFile
     * @return
     * @throws ModulePackFatalError
     */
    private static DexClassLoader createClassLoader(File codeCacheDir, File modulePackFile)
            throws ModulePackFatalError {
        if (!codeCacheDir.exists() && !codeCacheDir.mkdirs())
            throw new ModulePackFatalError("Couldn't create optimised Code Cache");

        try {
            return new DexClassLoader(
                    modulePackFile.getAbsolutePath(),
                    codeCacheDir.getAbsolutePath(),
                    null,
                    MainActivity.class.getClassLoader());
        } catch (Throwable t) {
            throw new ModulePackFatalError("Issue loading ClassLoader",
                    t
            );
        }
    }

    /**
     * ===========================================================================
     * The final function in the getInstance stack.
     * <p>
     * Attempt to reflectively create a new instance of the {@link this#PACK_CLASSNAME}
     * class on the DexClassLoader.
     * <p>
     * Note: Error messages should be customised based on requirement.
     * ===========================================================================
     *
     * @param dexClassLoader
     * @param packMetaData
     * @param packLoadState
     * @return
     * @throws ModulePackFatalError
     * @throws ModulePackLoadAborted
     */
    @SuppressWarnings("unchecked")
    private static ModulePack instantiatePack(
            DexClassLoader dexClassLoader,
            LocalPackMetaData packMetaData,
            PackLoadState packLoadState)
            throws ModulePackFatalError, ModulePackLoadAborted {
        try {
            Class<?> packImpl = dexClassLoader.loadClass(PACK_CLASSNAME);

            Constructor<?> packConstructor = packImpl.getConstructor(
                    LocalPackMetaData.class,
                    PackLoadState.class
            );

            return (ModulePack) packConstructor.newInstance(packMetaData, packLoadState);
        }
        // ===========================================================================
        catch (ClassNotFoundException e) {
            throw new ModulePackFatalError(
                    "ModulePackImpl not found!" +
                            "\n" + "Pack not supported by currently installed apk, please update SnapTools",
                    e
            );
        }
        // ===========================================================================
        catch (NoSuchMethodException e) {
            throw new ModulePackFatalError(
                    "No Constructor found!" +
                            "\n" + "Pack not supported by currently installed apk, please update SnapTools",
                    e
            );
        }
        // ===========================================================================
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (cause instanceof ModulePackLoadAborted)
                throw (ModulePackLoadAborted) cause;

            throw new ModulePackFatalError(
                    "Error instantiating pack!" +
                            "\n" + "Pack not supported by currently installed apk, please update SnapTools",
                    e
            );
        }
        // ===========================================================================
        catch (Throwable e) {
            throw new ModulePackFatalError(
                    "Error instantiating pack!" +
                            "\n" + "Pack not supported by currently installed apk, please update SnapTools",
                    e
            );
        }
    }
}
