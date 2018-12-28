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
 */

public abstract class ModuleHelper extends Module {
    private static final Map<String, Class<?>> classMap = new HashMap<>();
    private static final Map<String, Member> memberMap = new HashMap<>();

    public ModuleHelper(String name, boolean canBeDisabled, Logger logger) {
        super(name, canBeDisabled, logger);
    }

    protected XC_MethodHook.Unhook hookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        return hookMethod(className, classLoader, null, parameterTypesAndCallback);
    }

    protected XC_MethodHook.Unhook hookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) {
        return hookMethod(clazz, null, parameterTypesAndCallback);
    }

    protected XC_MethodHook.Unhook hookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            return hookMethod(resolveClass(className, classLoader), methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            getLogger().e("Unable to resolve Class " + className, t);
        }
        return null;
    }

    protected XC_MethodHook.Unhook hookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
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
            getLogger().e("Failed " + moduleLoadState.getBasicBreakdown() + ".Unable to resolve Class " + className, t);
        }
        return null;
    }

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
