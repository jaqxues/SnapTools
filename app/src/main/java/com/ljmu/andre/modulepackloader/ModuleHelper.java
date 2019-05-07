package com.ljmu.andre.modulepackloader;

import com.ljmu.andre.modulepackloader.Utils.Logger;
import com.ljmu.andre.modulepackloader.Utils.Module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findConstructorExact;
import static de.robv.android.xposed.XposedHelpers.findMethodExact;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 28.12.2018 - Time 18:53.
 * <p>
 *     A more extended version of what your Module Class could look like. This class implements the
 *     ModuleLoadState features and combines them with very easy Helper Methods. This also takes care
 *     of every Exception that might occur during Hooking ({@code ***NotFoundException...}) and
 *     logs it with the {@code logger}.<br>
 * </p>
 */

public abstract class ModuleHelper extends Module {
    private static final Map<String, Class<?>> classMap = new HashMap<>();
    private static final Map<String, Member> memberMap = new HashMap<>();
    private final Logger logger;

    /**
     * Creates a new "extended" Module
     *
     * @param name Name of the Module
     * @param canBeDisabled Determines if the Module can be loaded or unloaded by the User
     * @param logger The Logger that will log exceptions during the Hook Process
     */
    public ModuleHelper(String name, boolean canBeDisabled, Logger logger) {
        super(name, canBeDisabled);
        this.logger = logger;
    }

    /**
     * @return The Logger that every exception will be logged to.
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Convenience Method to improve readability and distinguishing between Method and Constructor
     * Hooks. Exact Same result than calling {@link #hookMethod(String, ClassLoader, String, Object...)}
     */
    protected XC_MethodHook.Unhook hookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        return hookMethod(className, classLoader, null, parameterTypesAndCallback);
    }

    /**
     * Convenience Method to improve readability and distinguishing between Method and Constructor
     * Hooks. Exact Same result than calling {@link #hookMethod(Class, String, Object...)}
     */
    protected XC_MethodHook.Unhook hookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) {
        return hookMethod(clazz, null, parameterTypesAndCallback);
    }

    /**
     *
     * First resolves the class, then calls {@link #hookMethod(Class, String, Object...)} with that
     * class
     *
     * @param className Name of the Class
     * @param classLoader ClassLoader to search the Class on
     * @param methodName Name of the Method that should be hooked
     * @param parameterTypesAndCallback ParameterTypes of the Method or Constructor and Callback
     *      *                                  as last argument
     * @return Unhook object as a result of {@code XposedBridge.hookMethod(Member, XC_MethodHook)}
     */
    protected XC_MethodHook.Unhook hookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            return hookMethod(resolveClass(className, classLoader), methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            getLogger().e("Unable to resolve Class " + className, t);
        }
        return null;
    }

    /**
     *
     * @param clazz Class of the Method or Constructor
     * @param methodName Name of the Method or Null in case of Constructor
     * @param parameterTypesAndCallback ParameterTypes of the Method or Constructor and Callback
     *                                  as last argument
     * @return Unhook object as a result of {@code XposedBridge.hookMethod(Member, XC_MethodHook)}
     */
    protected XC_MethodHook.Unhook hookMethod(Class<?> clazz, @Nullable String methodName, Object... parameterTypesAndCallback) {
        try {
            return XposedBridge.hookMethod(
                    resolveMember(
                            clazz,
                            methodName,
                            Arrays.copyOfRange(parameterTypesAndCallback, 0, parameterTypesAndCallback.length - 1)
                    ),
                    (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length - 1]
            );
        } catch (Throwable t) {
            getLogger().e("Unable to resolve Method " + clazz + "|" + methodName + "|"
                    + Arrays.toString(parameterTypesAndCallback), t);
        }
        return null;
    }

    /**
     * Calls a Method with the provided arguments on the provided Object. Handles exceptions.
     *
     * @param obj The object to perform the action on.
     * @param methodName The Name of the Method you want to call
     * @param args The Arguments with which you want to invoke the Method.
     * @param <T> The Result Type of the Method that the Return value will be casted to.
     * @return The (possibly casted) return value of the invoked Method or null if an exception
     * occurred
     */
    @Nullable
    @SuppressWarnings("unchecked")
    <T> T callMethod(@NotNull Object obj, String methodName, Object... args) {
        try {
            return (T) XposedHelpers.callMethod(obj, methodName, args);
        } catch (Throwable t) {
            getLogger().e("Unable to find or invoke Method "
                    + obj.getClass() + " | " + methodName + " | "
                    + Arrays.toString(XposedHelpers.getParameterTypes(args)), t);
        }
        return null;
    }

    /**
     * Calls a static Method and handles possible exceptions.
     *
     * @param clazz Class of the Method.
     * @param methodName Method Name that should be invoked.
     * @param args Tje Arguments with which you want to invoke the Method.
     * @param <T> The Result Type of the Method that the Return value will be casted to.
     * @return THe (possibly casted) return value of the invoked Method or null if an exception
     * occurred.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    <T> T callStaticMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            return (T) XposedHelpers.callStaticMethod(clazz, methodName, args);
        } catch (Throwable t) {
            getLogger().e("Unable to find or invoke Static Method "
                    + clazz + " | " + methodName + " | "
                    + Arrays.toString(XposedHelpers.getParameterTypes(args)), t);
        }
        return null;
    }


    /**
     * Resolves the Class by its name and ClassLoader. Handles Exceptions and updating the
     * ModuleLoadState.
     *
     * @param className The name of the Class
     * @param classLoader The class loader, or null for the boot class loader.
     * @return The resolved Class or null if an exception occurred (ClassNotFound...)
     */
    protected Class<?> resolveClass(String className, ClassLoader classLoader) {
        // If class has not been resolved successfully, this allows to return null without counting
        // it as a resolving issue so that the counter stays correct
        if (classMap.containsKey(className))
            return classMap.get(className);
        try {
            Class<?> resolvedClass = findClass(className, classLoader);
            classMap.put(className, resolvedClass);
            moduleLoadState.resolvedHook();
            return resolvedClass;
        } catch (Throwable t) {
            moduleLoadState.unresolvedHook();
            getLogger().e("Failed " + moduleLoadState.getBasicBreakdown() + ". Unable to resolve Class " + className, t);
        }
        return null;
    }

    /**
     * Resolves the Member by its Class, Name and ParameterTypes. Handles exceptions and updating
     * the ModuleLoadState.
     *
     * @param clazz The Class of the Member
     * @param methodName The Name of the Method or null if Constructor
     * @param parameterTypes The ParameterTypes of the Member
     * @return The resolved Member or null if an exception occurred (MethodNotFound...)
     */
    protected Member resolveMember(Class<?> clazz, @Nullable String methodName, Object... parameterTypes) {
        String key = clazz + "|" + methodName + "|" + Arrays.toString(parameterTypes);
        if (memberMap.containsKey(key))
            return memberMap.get(key);
        try {
            Member resolvedMember;
            if (methodName == null)
                resolvedMember = findConstructorExact(clazz, parameterTypes);
            else
                resolvedMember = findMethodExact(clazz, methodName, parameterTypes);
            memberMap.put(key, resolvedMember);
            moduleLoadState.resolvedHook();
            return resolvedMember;
        } catch (Throwable t) {
            moduleLoadState.unresolvedHook();
            getLogger().e("Failed " + moduleLoadState.getBasicBreakdown() + ". Unable to resolve Member " + key, t);
        }
        return null;
    }
}
