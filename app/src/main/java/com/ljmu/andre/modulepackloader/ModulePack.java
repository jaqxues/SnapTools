package com.ljmu.andre.modulepackloader;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljmu.andre.modulepackloader.Exceptions.PackBuildException;
import com.ljmu.andre.modulepackloader.Exceptions.PackLoadException;
import com.ljmu.andre.modulepackloader.Exceptions.PackSecurityException;
import com.ljmu.andre.modulepackloader.Listeners.PackSecurityListener;
import com.ljmu.andre.modulepackloader.Utils.Assert;
import com.ljmu.andre.modulepackloader.Utils.JarUtils;
import com.ljmu.andre.modulepackloader.Utils.Logger;
import com.ljmu.andre.modulepackloader.Utils.Logger.DefaultLogger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

import dalvik.system.DexClassLoader;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public abstract class ModulePack {
    @Nullable
    private PackAttributes packAttributes;

    @Nullable
    public <T extends PackAttributes> T getPackAttributes() {
        return (T) packAttributes;
    }

    private <T extends ModulePack> T setPackAttributes(PackAttributes packAttributes) {
        this.packAttributes = packAttributes;
        return (T) this;
    }

    private static <T extends ModulePack> T getInstance(File packFile, File dexFileDir, String packClassPath,
                                                        ClassLoader classLoader, @Nullable List<Object> constructorArguments,
                                                        @Nullable PackAttributes packAttributes)
            throws PackLoadException {
        DexClassLoader packClassLoader = createClassLoader(packFile, dexFileDir, classLoader);
        return instantiatePack(packClassLoader, packClassPath, constructorArguments)
                .setPackAttributes(packAttributes)
                .onInitialised();
    }

    /**
     * ===========================================================================
     * Create a classloader that will be used to instantiate the implemented
     * version of this ModulePack class using {@param packClassPath}
     * ===========================================================================
     *
     * @param dexFileDir - A directory that is optimised for .dex files.
     *                   Usually a code optimised directory {@link Activity#getCodeCacheDir()}
     * @return
     * @throws PackLoadException
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
     * ===========================================================================
     * Constructor has just been called and the ModulePack is ready to be used.
     * ===========================================================================
     *
     * @param <T>
     * @return
     */
    public <T extends ModulePack> T onInitialised() {
        return (T) this;
    }

    /**
     * ===========================================================================
     * The final function in the getInstance stack.
     * <p>
     * Attempt to reflectively create a new instance of the {@param }
     * class on the DexClassLoader.
     * <p>
     * Note: Error messages should be customised based on requirement.
     * ===========================================================================
     *
     * @param dexClassLoader
     * @param packClassPath
     * @param constructorArguments
     * @return
     * @throws PackLoadException
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
