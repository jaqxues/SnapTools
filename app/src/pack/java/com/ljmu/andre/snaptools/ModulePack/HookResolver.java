package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;
import androidx.annotation.NonNull;

import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.Hook;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.StringUtils;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings({"WeakerAccess"})
public class HookResolver extends ModuleHelper {
    private static final Map<String, HookReference> hookReferenceMap = new HashMap<>();
    private static final Map<String, Class<?>> hookClassMap = new HashMap<>();

    // ===========================================================================

    public HookResolver(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * Attempt to find the HookReference associated with the input Hook
     * ===========================================================================
     *
     * @param hook - The Hook to find the linked HookReference for
     * @return The HookReference linked to Hook
     * @throws HookNotFoundException - When no HookReference is found
     */
    @NonNull
    public static HookReference resolveHook(@NonNull Hook hook) throws HookNotFoundException {
        HookReference hookReference = hookReferenceMap.get(hook.getName());

        if (hookReference == null) {
            throw new HookNotFoundException(
                    String.format("Could not find hook [Name:%s][Class:%s][Method:%s]",
                            hook.getName(), hook.getHookClass().getStrClass(), hook.getHookMethod()));
        }

        return hookReference;
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * Attempt to find the Class associated with the input HookClass
     * ===========================================================================
     *
     * @param hookClass - The HookClass to find the linked Class for
     * @return The Class<?> linked to HookClass
     * @throws HookNotFoundException - When no Class is found
     */
    @NonNull
    public static Class<?> resolveHookClass(@NonNull HookClass hookClass) throws HookNotFoundException {
        Class<?> resolvedClass = hookClassMap.get(hookClass.getName());

        if (resolvedClass == null)
            throw new HookNotFoundException(
                    String.format(
                            "Could not find HookClass [Class:%s]",
                            hookClass.getStrClass()));

        return resolvedClass;
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return null;
    }

    /**
     * ===========================================================================
     * Begins Hook loading and Load State Updating
     * ===========================================================================
     */
    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        if (hookReferenceMap.size() > 0) {
            Timber.w("Tried to resolve hooks more than once!");
            return;
        }

        int failedClasses = buildClassMap(snapClassLoader);
        int failedHooks = buildHookMap(snapClassLoader);

        if (failedClasses > 0 || failedHooks > 0) {
            Timber.e("Failed to load [Classes: %s/%s][Hooks: %s/%s]",
                    failedClasses, HookClassDef.INST.size(),
                    failedHooks, HookDef.INST.size());
            moduleLoadState.setState(State.ISSUES);
        }
    }

    /**
     * ===========================================================================
     * Iterate through the HookClass values and build their appropriate
     * ===========================================================================
     *
     * @return the number of failed Classes
     */
    private int buildClassMap(ClassLoader classLoader) {
        int failedClasses = 0;
        for (HookClass hookClass : HookClassDef.INST.values()) {
            try {
                Class<?> resolvedClass = findClass(hookClass.getStrClass(), classLoader);

                hookClassMap.put(hookClass.getName(), resolvedClass);
            } catch (Throwable t) {
                if (Constants.getApkVersionCode() >= 73 && Constants.isApkDebug()) {
                    Timber.e("Error building class [Class:%s][Reason:%s]",
                            hookClass, t.getMessage());
                } else {
                    Timber.e("Error building class: %s", StringUtils.obfus(hookClass.getStrClass()));
                }

                failedClasses++;
            }
        }

        return failedClasses;
    }

    /**
     * ===========================================================================
     * Attempt to build as many HookReferences using Hook.values()
     * ===========================================================================
     *
     * @return the number of failed HookReferences
     */
    private int buildHookMap(ClassLoader classLoader) {
        int failedHooks = 0;
        for (Hook hook : HookDef.INST.values()) {
            try {
                if (hook.getHookClass() == null)
                    continue;

                HookReference hookReference = new HookReference(hook, classLoader);
                hookReferenceMap.put(hook.getName(), hookReference);
            } catch (Throwable t) {
                Timber.e("Error building hook [Hook:%s][Reason:%s]",
                        hook, t.getMessage());

                if (Constants.getApkVersionCode() >= 73 && Constants.isApkDebug()) {
                    Timber.e("Error building hook [Hook:%s][Reason:%s]",
                            hook, t.getMessage());
                } else {
                    Timber.e("Error building hook: %s", StringUtils.obfus(hook.getHookMethod()));
                }
                failedHooks++;
            }
        }

        return failedHooks;
    }

    // ===========================================================================

    // ===========================================================================

    /**
     * ===========================================================================
     * Hook reference: Builds and stores the Member for hooking
     * ===========================================================================
     */
    public static class HookReference {
        private final Member hookMember;

        HookReference(Hook hook, ClassLoader classLoader) throws Throwable {
            Class hookClass = resolveHookClass(hook.getHookClass());
            String hookMethod = hook.getHookMethod();
            Class[] hookParams = resolveParams(hook.getHookParams(), classLoader);

            if (hookMethod != null)
                this.hookMember = XposedHelpers.findMethodExact(hookClass, hookMethod, hookParams);
            else
                this.hookMember = XposedHelpers.findConstructorExact(hookClass, hookParams);

            Timber.d(this.toString());
        }

        // ===========================================================================

        private Class[] resolveParams(Object[] unresolvedParams, ClassLoader classLoader) throws Throwable {
            ArrayList<Class> resolvedParamList = new ArrayList<>();

            for (Object param : unresolvedParams) {
                Class resolvedParam;

                if (param instanceof String)
                    resolvedParam = findClass((String) param, classLoader);
                else if (param instanceof Class)
                    resolvedParam = (Class) param;
                else
                    continue;

                resolvedParamList.add(resolvedParam);
            }

            return resolvedParamList.toArray(new Class[0]);
        }

        @Override
        public String toString() {
            if (getHookMember() == null)
                return "HookReference[Undefined]";

            return String.format("HookReference[Class:%s, Method:%s]",
                    getHookMember().getDeclaringClass(),
                    getHookMember().getName());
        }

        public Member getHookMember() {
            return hookMember;
        }
    }
}