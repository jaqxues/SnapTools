package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.HookClass;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.Hook;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;
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

	@Override public FragmentHelper[] getUIFragments() {
		return null;
	}

	// ===========================================================================

	/**
	 * ===========================================================================
	 * Begins Hook loading and Load State Updating
	 * ===========================================================================
	 */
	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		if (hookReferenceMap.size() > 0) {
			Timber.w(/*Tried to resolve hooks more than once!*/ decryptMsg(new byte[]{-74, 18, 61, 118, 82, 96, -87, -79, 28, 92, -66, 98, -78, 26, 126, -85, -64, -8, 45, 86, 61, 59, -35, -22, -18, 43, 2, -37, -22, -5, -86, -83, -52, -43, 103, -70, 112, -24, 99, -61, 84, 57, -92, 30, 40, 26, 28, 114}));
			return;
		}

		int failedClasses = buildClassMap(snapClassLoader);
		int failedHooks = buildHookMap(snapClassLoader);

		if (failedClasses > 0 || failedHooks > 0) {
			Timber.e(/*Failed to load [Classes: %s/%s][Hooks: %s/%s]*/ decryptMsg(new byte[]{-86, -61, 73, -120, 94, -15, 45, 36, -84, -79, 19, -7, 93, -36, 75, 64, 56, -112, 107, 65, 125, -116, -1, 87, -66, 28, 85, 10, 25, -71, 12, 105, -15, 37, -62, -119, 42, -100, -66, 51, 25, -3, 21, 100, 7, -106, -46, -93}),
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
				Timber.e(/*Error building class [Class:%s][Reason:%s]*/ decryptMsg(new byte[]{-80, 122, 62, -44, 113, -84, 126, -48, 126, 43, -21, -70, -67, -82, 69, -23, 79, -126, 31, -56, -99, 43, 68, -104, -43, 69, -2, 127, 12, 51, 7, 48, -85, -10, 77, -26, 12, -25, 64, -74, -99, -127, -36, 67, 24, 67, -97, 4}),
						hookClass, t.getMessage());
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
				Timber.e(/*Error building hook [Hook:%s][Reason:%s]*/ decryptMsg(new byte[]{-75, -126, 1, -48, -91, -4, 26, 98, -5, 95, -116, 24, 18, -81, 113, -3, 16, -67, 21, 30, -118, 52, 64, -59, 41, -8, 117, 21, 16, -77, 10, 0, 53, -6, 66, 19, -40, 16, -120, -77, -83, 47, -89, 25, 70, 56, 76, 97}),
						hook, t.getMessage());
				failedHooks++;
			}
		}

		return failedHooks;
	}

	/**
	 * ===========================================================================
	 * Attempt to find the HookReference associated with the input Hook
	 * ===========================================================================
	 *
	 * @param hook - The Hook to find the linked HookReference for
	 * @return The HookReference linked to Hook
	 * @throws HookNotFoundException - When no HookReference is found
	 */
	@NonNull public static HookReference resolveHook(@NonNull Hook hook) throws HookNotFoundException {
		HookReference hookReference = hookReferenceMap.get(hook.getName());

		if (hookReference == null) {
			throw new HookNotFoundException(
					String.format(/*Could not find hook [Name:%s][Class:%s][Method:%s]*/ decryptMsg(new byte[]{-21, 22, 66, 106, -30, -79, -41, -11, -106, -123, 55, 60, 68, 95, -66, 3, 77, -92, 0, 55, -92, 4, 83, -31, 18, 51, -56, -13, -13, -12, -15, 124, 28, 61, -15, 88, -81, -123, -3, 90, -45, -3, -73, 117, -58, 121, 24, 30, 40, -101, -16, -13, -30, -12, -44, 18, 68, 55, 25, -6, 31, 118, -112, -2}),
							hook.getName(), hook.getHookClass().getStrClass(), hook.getHookMethod()));
		}

		return hookReference;
	}

	/**
	 * ===========================================================================
	 * Attempt to find the Class associated with the input HookClass
	 * ===========================================================================
	 *
	 * @param hookClass - The HookClass to find the linked Class for
	 * @return The Class<?> linked to HookClass
	 * @throws HookNotFoundException - When no Class is found
	 */
	@NonNull public static Class<?> resolveHookClass(@NonNull HookClass hookClass) throws HookNotFoundException {
		Class<?> resolvedClass = hookClassMap.get(hookClass.getName());

		if (resolvedClass == null)
			throw new HookNotFoundException(
					String.format(
							/*Could not find HookClass [Class:%s]*/ decryptMsg(new byte[]{-11, 91, 101, 64, 36, -62, 72, 17, 21, 122, 76, 70, -112, 65, 43, 67, 87, -87, -50, -63, -87, -48, 124, -119, -84, -71, -110, 80, -7, 30, 35, -117, -114, -64, -85, 106, -34, -36, -114, -125, 78, 94, 44, -96, -45, 66, 124, 48}),
							hookClass.getStrClass()));

		return resolvedClass;
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