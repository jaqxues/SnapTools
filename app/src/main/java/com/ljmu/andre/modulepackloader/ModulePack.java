package com.ljmu.andre.modulepackloader;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ljmu.andre.modulepackloader.Exceptions.PackBuildException;
import com.ljmu.andre.modulepackloader.Exceptions.PackLoadException;
import com.ljmu.andre.modulepackloader.Exceptions.PackSecurityException;
import com.ljmu.andre.modulepackloader.Listeners.PackSecurityListener;
import com.ljmu.andre.modulepackloader.Utils.Assert;
import com.ljmu.andre.modulepackloader.Utils.JarUtils;
import com.ljmu.andre.modulepackloader.Utils.LoadState;
import com.ljmu.andre.modulepackloader.Utils.Logger;
import com.ljmu.andre.modulepackloader.Utils.Logger.DefaultLogger;
import com.ljmu.andre.modulepackloader.Utils.Module;
import com.ljmu.andre.modulepackloader.Utils.PackLoadState;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 *
 * <h1>Structure of a ModulePack</h1>
 * <p>
 *     A ModulePack contains different Modules. These Modules are where the actual actions take
 *     place. The ModulePack's only charge is to load these Modules in and allow manipulating all
 *     these Modules at once. This can reduce the amount of code significantly while still providing
 *     great structure and readability.
 * </p>
 * <h1>Usage of a ModulePack</h1>
 * <p>
 *     This class is only the very basic structure of a ModulePack. You should extend this BaseClass
 *     to allow your app-specific Actions (Load Hooks or even load Resource Hooks). The code that
 *     you will use in your Xposed-Class (which you specify in the <i>xposed_init</i> file) will be
 *     very simple:
 *     <ol>
 *         <li>Use the Builder class to instantiate the Module Pack</li>
 *         <li>Call the {@link #loadModules()} Method to initialize the Modules</li>
 *         <li>Call your own methods that allow you to perform the actions you (possibly) need:</li>
 *         <ul>
 *             <li>Loading Hooks</li>
 *             <li>Loading Resource Hooks</li>
 *             <li>Perform some actions once the Activity of the Target Application has been loaded</li>
 *         </ul>
 *     </ol>
 * </p>
 */
public abstract class ModulePack {
    /** @hide */
    @Nullable
    private PackAttributes packAttributes;
    /**
     * {@link PackLoadState} which allows easy access to get Load Issues etc
     */
    protected PackLoadState packLoadState = new PackLoadState();
    /**
     * Contains the {@link Module} instances of this Pack
     */
    protected final List<Module> modules = new ArrayList<>();

    /**
     * Allows simple access to ModulePack information.
     *
     * @param <T> Implementation of the {@code PackAttributes} class.
     * @return The PackAttributes associated with this ModulePack.
     */
    @Nullable
    public <T extends PackAttributes> T getPackAttributes() {
        return (T) packAttributes;
    }

    /**
     * Allows setting an implementation of {@link PackAttributes} for this Pack in order to easier
     * access information about the ModulePack
     *
     * @param packAttributes PackAttributes to be set.
     * @param <T> Implementation of this ModulePack class.
     * @return Current ModulePack implementation
     */
    private <T extends ModulePack> T setPackAttributes(PackAttributes packAttributes) {
        this.packAttributes = packAttributes;
        return (T) this;
    }

    /**
     * Abstract method that should return the Modules with their LoadState in a HashMap. Here is
     * some advice which LoadState you should use for this HashMap
     * <ul>
     *     <li>
     *         If the Module is disabled by the user, {@link LoadState.State#SKIPPED} should be used.<br>
     *         (Make use of {@link Module#canBeDisabled()} to determine if the user is allowed to disable it)
     *     </li>
     *     <li>
     *         Use {@link LoadState.State#SUCCESS} if the Module is enabled and has been loaded successfully
     *     </li>
     *     <li>
     *         Use {@link LoadState.State#FAILED} if the Module is enabled but did not load successfully (For some strange reason)
     *     </li>
     * </ul>
     *
     * @return HashMap that will be used to in {@link #loadModules()}.
     */
    public abstract Map<Module, LoadState.State> getModules();

    /**
     * Called immediately after instantiation of the ModulePack. You can use {@link #onInitialised()}
     * to perform actions before loading modules or. This method calls {@link #getModules()} to get
     * a list of the available Modules.
     */
    private <T extends ModulePack> T loadModules() {
        Map<Module, LoadState.State> moduleMap = getModules();
        for (Map.Entry<Module, LoadState.State> entry : moduleMap.entrySet())
            entry.getKey().getModuleLoadState().setState(entry.getValue());
        addModules(moduleMap.keySet().toArray(new Module[0]));
        //noinspection unchecked
        return (T) this;
    }

    /**
     * Add a Module to the List of active Modules.
     *
     * @param module Module to be added
     * @see #loadModules()
     */
    protected void addModule(Module module) {
        packLoadState.addModuleLoadState(module.getModuleLoadState());
        modules.add(module);
    }

    /**
     * Adds multiple Modules to the List of active Modules.
     *
     * @param modules Modules to be added
     * @see #loadModules()
     */
    protected void addModules(Module... modules) {
        for (Module module : modules) {
            packLoadState.addModuleLoadState(module.getModuleLoadState());
        }
        this.modules.addAll(Arrays.asList(modules));
    }

    /**
     * @return List of the loaded Modules in this Pack
     */
    public List<Module> getModuleList() {
        return modules;
    }

    /**
     * Get a specific Module of a ModulePack by its name.
     *
     * @param name Name of the Module
     * @return THe requested module if present - else null.
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name))
                return module;
        }

        return null;
    }

    /**
     * @return The {@link PackLoadState} of this Module Pack
     */
    public PackLoadState getPackLoadState() {
        return packLoadState;
    }

    /**
     * Method called by {@link Builder#build()}
     *
     * @param packFile The .jar file that should be loaded by the ClassLoader
     * @param dexFileDir An optimized CodeCacheDir to call {@link DexClassLoader#DexClassLoader(String, String, String, ClassLoader)})}
     * @param packClassPath Name of a Subclass of this Class that should be instantiated
     * @param classLoader The Parent ClassLoader of the Pack. Optional, only use in special Cases
     * @param constructorArguments Values that can be instantiated to the custom constructor to
     *                             instantiate the ModulePack
     * @param packAttributes PackAttributes that will be set for the target ModulePack (optional)
     * @param <T> Given Type Parameter that the ModulePack should be casted to.
     * @return Instance of {@code T} where T is a subclass of this class.
     * @throws PackLoadException Wraps every exception that can occur to instantiate a Pack
     */
    private static <T extends ModulePack> T getInstance(File packFile, File dexFileDir, String packClassPath,
                                                        @Nullable ClassLoader classLoader, @Nullable List<Object> constructorArguments,
                                                        @Nullable PackAttributes packAttributes)
            throws PackLoadException {
        DexClassLoader packClassLoader = createClassLoader(packFile, dexFileDir, classLoader);
        return instantiatePack(packClassLoader, packClassPath, constructorArguments)
                .setPackAttributes(packAttributes)
                .onInitialised()
                .loadModules();
    }

    /**
     *
     * Create a classloader that will be used to instantiate the implemented
     * version of this ModulePack class using {@param packClassPath}
     *
     * @param packFile .jar File from where the Classes will be loaded
     * @param dexFileDir A directory that is optimised for .dex files.
     *                   Usually a code optimised directory {@link Activity#getCodeCacheDir()}
     * @param classLoader Optional ClassLoader (just in case someone would ever need that)
     * @return ClassLoader instance that should be able to load the ModulePack Classes
     * @throws PackLoadException In case the CodeCacheDir does not exist or the ClassLoader could not
     * be created
     */
    private static DexClassLoader createClassLoader(File packFile, File dexFileDir, @Nullable ClassLoader classLoader)
            throws PackLoadException {
        if (!dexFileDir.exists() && !dexFileDir.mkdirs())
            throw new PackLoadException("Couldn't create optimised Code Cache");

        if (classLoader == null) {
            classLoader = ModulePack.class.getClassLoader();
        }

        try {
            return new DexClassLoader(
                    packFile.getAbsolutePath(),
                    dexFileDir.getAbsolutePath(),
                    null,
                    classLoader
            );
        } catch (Throwable t) {
            throw new PackLoadException("Issue loading ClassLoader",
                    t
            );
        }
    }

    /**
     * Constructor has just been called and the ModulePack is ready to be used. The next action that
     * will be performed just after this is {@link #loadModules()}.
     *
     * @param <T> Given Type Parameter that the ModulePack should be casted to.
     * @return The ModulePack instance
     */
    public <T extends ModulePack> T onInitialised() {
        return (T) this;
    }

    /**
     * The final function in the getInstance stack.
     * <p>
     *     Attempt to reflectively create a new instance of the {@code T} class on the
     *     DexClassLoader.
     * <p>
     * Note: Error messages should be customised based on requirement.
     *
     * @param dexClassLoader The ClassLoader of the Module so that the Pack can use all of its classes
     *                       and methods seamlessly (with a CacheDirectory that can be chosen by the user)
     * @param packClassPath Subclass of this class that is instantiated by calling its constructor.
     * @param constructorArguments Arguments to invoke a matching constructor in the Class {@code packClassPath}.
     * @param <T> Given Type Parameter that the ModulePack should be casted to.
     * @return The instance of {@code T} returned by invoking the given constructor.
     * @throws PackLoadException Wrapper for various exception that can occur during this
     * reflection-based method.
     */
    @SuppressWarnings("unchecked")
    private static <T extends ModulePack> T instantiatePack(@NonNull DexClassLoader dexClassLoader,
                                                            @NonNull String packClassPath,
                                                            @Nullable List<Object> constructorArguments)
            throws PackLoadException {
        Class<?>[] parameterTypes;
        Object[] parameters;

        if (constructorArguments == null) {
            parameterTypes = new Class[0];
            parameters = new Object[0];
        } else {
            parameterTypes = new Class[constructorArguments.size()];
            parameters = new Object[constructorArguments.size()];

            int index = -1;
            for (Object param : constructorArguments) {
                index++;

                parameterTypes[index] = param.getClass();
                parameters[index] = param;
            }
        }

        try {
            Class<?> packImpl = dexClassLoader.loadClass(packClassPath);

            Constructor<?> packConstructor = packImpl.getConstructor(parameterTypes);

            return (T) packConstructor.newInstance(parameters);
        }
        // ===========================================================================
        catch (ClassNotFoundException e) {
            throw new PackLoadException(
                    "ModulePack implementation not found for PackClassPath: " + packClassPath,
                    e
            );
        }
        // ===========================================================================
        catch (NoSuchMethodException e) {
            throw new PackLoadException(
                    "No ModulePack constructor found with arguments: " + Arrays.toString(parameterTypes),
                    e
            );
        }
        // ===========================================================================
        catch (InvocationTargetException e) {
            throw new PackLoadException(
                    "Exception occurred when calling ModulePack constructor!",
                    e
            );
        }
        // ===========================================================================
        catch (Throwable e) {
            throw new PackLoadException(
                    "Unknown exception occurred when instantiating ModulePack implementation",
                    e
            );
        }
    }

    /**
     * Builder Class as instantiating the Module Pack Implementation Class takes quite a few
     * parameters
     */
    @SuppressWarnings("NullableProblems")
    public static class Builder {
        @NonNull
        private final Logger logger;
        @NonNull
        private JarFile packJarFile;
        @NonNull
        private File packFile;
        @NonNull
        private File dexFileDir;
        @NonNull
        private String packClassPath;
        @Nullable
        private ClassLoader classLoader;
        @Nullable
        private PackAttributes packAttributes;
        @Nullable
        private List<Object> constructorArguments;

        /**
         * ===========================================================================
         * Listeners
         * ===========================================================================
         */
        @Nullable
        private PackSecurityListener packSecurityListener;

        public Builder() {
            this(null);
        }

        public Builder(@Nullable Logger logger) {
            if (logger == null) {
                logger = new DefaultLogger();
            }

            this.logger = logger;
        }

        /**
         * Build Method that creates the instance of {@code T} with the given properties of the
         * Builder instance.
         *
         * @param <T> Given Type Parameter that the ModulePack should be casted to.
         * @return The created ModulePack if successful
         * @throws PackBuildException In case the Pack Attributes are not supplied and could not be built by the builder
         * @throws PackSecurityException Can be caused by {@link PackSecurityListener#onSecurityCheck(JarFile)}
         * @throws PackLoadException Wrapper for every exception that could occur during the actual
         * instantiation of the ModulePack
         */
        public <T extends ModulePack> T build() throws PackBuildException, PackSecurityException, PackLoadException {
            logger.d("Building ModulePack");

            Assert.notNull("No pack file supplied!", packFile);
            Assert.notNull("No pack jar file supplied!", packJarFile);
            Assert.notNull("No Pack ClassPath supplied!", packClassPath);
            Assert.notNull("No dex file supplied!", dexFileDir);

            if (packSecurityListener != null) {
                logger.d("Checking pack security");

                try {
                    packSecurityListener.onSecurityCheck(packJarFile);
                } catch (IOException e) {
                    throw new PackSecurityException("IOException when checking pack security!", e);
                }

                logger.d("Pack properly secured");
            }

            if (packAttributes != null) {
                logger.d("Building PackAttributes");

                try {
                    packAttributes.onBuild(JarUtils.getMainAttributes(packJarFile));
                    logger.d("PackAttributes built successfully");
                } catch (IOException e) {
                    throw new PackBuildException("Couldn't build Pack Attributes", e);
                }
            } else {
                logger.d("No PackAttributes supplied");
            }

            return ModulePack.getInstance(
                    getPackFile(),
                    getDexFileDir(),
                    getPackClassPath(),
                    getClassLoader(),
                    getConstructorArguments(),
                    getPackAttributes()
            );
        }

        @NonNull
        public File getPackFile() {
            return packFile;
        }

        public File getDexFileDir() {
            return dexFileDir;
        }

        public String getPackClassPath() {
            return packClassPath;
        }

        public Builder setPackClassPath(String packClassPath) {
            this.packClassPath = packClassPath;
            return this;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        @Nullable
        public List<Object> getConstructorArguments() {
            return constructorArguments;
        }

        public Builder setConstructorArguments(@Nullable List<Object> constructorArguments) {
            this.constructorArguments = constructorArguments;
            return this;
        }

        public PackAttributes getPackAttributes() {
            return packAttributes;
        }

        public Builder setPackAttributes(PackAttributes packAttributes) {
            this.packAttributes = packAttributes;
            return this;
        }

        public Builder setClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder setDexFileDir(File dexFileDir) {
            this.dexFileDir = dexFileDir;
            return this;
        }

        public JarFile getPackJarFile() {
            return packJarFile;
        }

        public Builder setPackJarFile(File file) throws IOException {
            this.packFile = file;
            this.packJarFile = new JarFile(file);
            return this;
        }

        public Builder setJarFile(String jarFilePath) throws IOException {
            return setPackJarFile(new File(jarFilePath));
        }

        @Nullable
        public PackSecurityListener getPackSecurityListener() {
            return packSecurityListener;
        }

        public Builder setPackSecurityListener(@Nullable PackSecurityListener packSecurityListener) {
            this.packSecurityListener = packSecurityListener;
            return this;
        }
    }
}
