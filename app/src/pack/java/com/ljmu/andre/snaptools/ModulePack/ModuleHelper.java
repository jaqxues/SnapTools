package com.ljmu.andre.snaptools.ModulePack;

import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.Hook;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.HookVariable;
import com.ljmu.andre.snaptools.ModulePack.HookResolver.HookReference;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.UnhookManager.addUnhook;
import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ModuleHelper extends Module {
    public ModuleHelper(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * A function to help distinguish between Constructors and Methods
     * ===========================================================================
     */
    void hookConstructor(Hook hook, XC_MethodHook xc_methodHook) {
        hookMethod(hook, xc_methodHook);
    }

    /**
     * ===========================================================================
     * Attempt to perform the requested hook... Mark a failure on error
     * ===========================================================================
     */
    void hookMethod(Hook hook, XC_MethodHook xc_methodHook) {
        try {
            HookReference hookReference = HookResolver.resolveHook(hook);
            addUnhook(
                    name(),
                    XposedBridge.hookMethod(
                            hookReference.getHookMember(),
                            xc_methodHook
                    )
            );

            moduleLoadState.success();
        } catch (HookNotFoundException e) {
            Timber.e("Hook Failed: %s", e.getMessage());
            moduleLoadState.fail();
        }
    }


    /**
     * ===========================================================================
     * Attempt to hook all of the Constructors of a given HookClass
     * ===========================================================================
     */
    void hookAllConstructors(HookClass hookClass, XC_MethodHook xc_methodHook) {
        try {
            Class<?> resolvedClass = HookResolver.resolveHookClass(hookClass);
            addUnhook(
                    name(),
                    XposedBridge.hookAllConstructors(
                            resolvedClass,
                            xc_methodHook)
            );

            moduleLoadState.success();
        } catch (HookNotFoundException e) {
            Timber.e("Hook Failed: %s", e.getMessage());
            moduleLoadState.fail();
        }
    }

    /**
     * ===========================================================================
     * Utility method to get an object field with a generic type
     * ===========================================================================
     */
    @SuppressWarnings("unchecked")
    <T> T getObjectField(HookVariable hookVariable, Object obj) {
        return (T) XposedHelpers.getObjectField(obj, hookVariable.getVarName());
    }

    /**
     * ===========================================================================
     * Utility method to get an object field with a generic type
     * ===========================================================================
     */
    @SuppressWarnings("unchecked")
    <T> T getObjectFieldWithType(HookVariable hookVariable, Object obj, Class<?> type) {
        Field objectField = XposedHelpers.findFirstFieldByExactType(obj.getClass(), type);

        try {
            return (T) objectField.get(obj);
        } catch (IllegalAccessException e) {
            // should not happen
            XposedBridge.log(e);
            throw new IllegalAccessError(e.getMessage());
        }
    }


    /**
     * ===========================================================================
     * Utility method to set an object field with a hook variable
     * ===========================================================================
     */
    void setObjectField(HookVariable hookVariable, Object obj, Object value) {
        XposedHelpers.setObjectField(obj, hookVariable.getVarName(), value);
    }

    /**
     * ===========================================================================
     * Call a Hook's Method from the linked Obj with the supplied Params
     * ===========================================================================
     */
    @SuppressWarnings("unchecked")
    <T> T callHook(Hook hook, Object obj, Object... params) throws HookNotFoundException {
        try {
            return (T) callMethod(obj, hook.getHookMethod(), params);
        } catch (Throwable e) {
            throw new HookNotFoundException(e);
        }
    }

    /**
     * ===========================================================================
     * Call a Hook's Static Method with the supplied Params
     * ===========================================================================
     */
    @SuppressWarnings("unchecked")
    <T> T callStaticHook(Hook hook, Object... params) throws HookNotFoundException {
        try {
            return (T) ((Method) HookResolver.resolveHook(hook).getHookMember()).invoke(null, params);
        } catch (Throwable e) {
            if (e instanceof HookNotFoundException)
                throw (HookNotFoundException) e;

            throw new HookNotFoundException(e);
        }
    }

    public interface HookBefore {
        void before(MethodHookParam param) throws Throwable;
    }

    public interface HookAfter {
        void after(MethodHookParam param) throws Throwable;
    }

    public static class HookWrapper extends ST_MethodHook {
        private HookBefore hookBefore;
        private HookAfter hookAfter;

        public HookWrapper(@Nullable HookBefore hookBefore, @Nullable HookAfter hookAfter) {
            this.hookBefore = hookBefore;
            this.hookAfter = hookAfter;
        }

        public HookWrapper(HookBefore hookBefore) {
            this.hookBefore = hookBefore;
        }

        public HookWrapper(HookAfter hookAfter) {
            this.hookAfter = hookAfter;
        }

        @Override
        protected void before(MethodHookParam param) throws Throwable {
            if (hookBefore != null)
                hookBefore.before(param);
        }

        @Override
        protected void after(MethodHookParam param) throws Throwable {
            if (hookAfter != null)
                hookAfter.after(param);
        }
    }
}
