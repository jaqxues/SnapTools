package com.ljmu.andre.snaptools.Utils;

import android.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.robv.android.xposed.XC_MethodHook;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.UnhookManager.addUnhook;
import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class XposedUtils {
    private static final Object LOG_STACK_LOCK = new Object();
    private static final Object LOG_CLASS_LOCK = new Object();

    public static void hookAllMethods(String className, ClassLoader cl, boolean hookSubClasses, boolean hookSuperClasses) {
        try {
            Timber.d("Starting allhook");
            Class targetClass = findClass(className, cl);

            triggerOnAll(targetClass, new ST_MethodHook() {
                @Override
                protected void before(MethodHookParam param) {
                    Timber.d(
                            "HookTrigger: %s \uD83E\uDC7A %s(%s) \uD83E\uDC7A %s",
                            targetClass.getSimpleName() +
                                    "(" +
                                    (param.thisObject == null ?
                                            "null" :
                                            param.thisObject.getClass().getSimpleName()) +
                                    ")",
                            param.method.getName(),
                            param.args != null ? Arrays.toString(param.args) : "",
                            ((Method) param.method).getReturnType()
                    );
                }

                @Override
                protected void after(MethodHookParam param) {
                    Timber.d(
                            "HookTrigger: %s \uD83E\uDC78 %s(%s) \uD83E\uDC80 %s",
                            targetClass.getSimpleName(),
                            param.method.getName(),
                            param.args != null ? Arrays.toString(param.args) : "",
                            param.getResult()
                    );
                }
            });

            if (hookSubClasses) {
                Class[] subClasses = targetClass.getClasses();

                Timber.d("Hooking Subclasses: " + subClasses.length);

                for (Class subClass : subClasses)
                    hookAllMethods(subClass.getName(), cl, true, hookSuperClasses);
            }

            if (hookSuperClasses) {
                Class superClass = targetClass.getSuperclass();
                if (superClass == null || superClass.getSimpleName().equals("Object"))
                    return;

                Timber.d("Found Superclass: " + superClass.getSimpleName());
                hookAllMethods(superClass.getName(), cl, false, true);
            }
        } catch (Throwable t) {
            Timber.e(t, "Error performing AllHook");
        }
    }

    public static void triggerOnAll(String clazz, ClassLoader classLoader, XC_MethodHook methodHook) {
        triggerOnAll(findClass(clazz, classLoader), methodHook);
    }

    public static void triggerOnAll(Class targetClass, XC_MethodHook methodHook) {
        Method[] allMethods = targetClass.getDeclaredMethods();

        Timber.d("Methods to hook: " + allMethods.length);
        for (Method baseMethod : allMethods) {
            Class<?>[] paramList = baseMethod.getParameterTypes();
            String fullMethodString = baseMethod.getName() + "(" + Arrays.toString(paramList) + ")";

            if (Modifier.isAbstract(baseMethod.getModifiers())) {
                Timber.w("Abstract method: " + targetClass.getSimpleName() + "." + fullMethodString);
                continue;
            }

            Object[] finalParam = new Object[paramList.length + 1];

            System.arraycopy(paramList, 0, finalParam, 0, paramList.length);

            finalParam[paramList.length] = methodHook;
            findAndHookMethod(targetClass, baseMethod.getName(), finalParam);
        }
    }

    public static void logAllStackTraces() {
        synchronized (LOG_STACK_LOCK) {
            try {
                for (Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
                    Thread stackThread = entry.getKey();
                    StackTraceElement[] stack = entry.getValue();

                    if (stackThread == null || stack == null)
                        continue;

                    Timber.d("THREAD STACK: [Name: %s][Id: %s]", stackThread.getName(), stackThread.getId());
                    logStackTrace(stack);
                    Timber.d("END THREAD STACK");
                }
            } catch (Throwable t) {
                Timber.e(t);
            }
        }
    }

    public static void logStackTrace(StackTraceElement[] stackTraceElements) {
        synchronized (LOG_STACK_LOCK) {
            try {
                Timber.d("### STARTING STACK TRACE ###");

                for (StackTraceElement traceElement : stackTraceElements)
                    Timber.d("Stack trace: [Class: %s] [Method: %s] [Line: %s]", traceElement.getClassName(), traceElement.getMethodName(), traceElement.getLineNumber());

                Timber.d("### FINISHED STACK TRACE ###");
            } catch (Throwable t) {
                Timber.e(t);
            }
        }
    }

    /**
     * Logs the current stack trace(ie. the chain of calls to get where you are now)
     */
    public static void logStackTrace() {
        synchronized (LOG_STACK_LOCK) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            logStackTrace(stackTraceElements);
        }
    }

    public static void logEntireClass(Object obj, int classCap) {
        synchronized (LOG_CLASS_LOCK) {
            if (obj == null) {
                Timber.d("Tried to log null object");
                return;
            }

            logEntireClass(obj.getClass(), obj, classCap);
        }
    }

    public static void logEntireClass(Class objClass, Object obj, int classCap) {
        synchronized (LOG_CLASS_LOCK) {
            try {
                for (int i = 0; i < classCap; i++) {
                    logEntireClass(objClass, obj);
                    objClass = objClass.getSuperclass();

                    if (objClass == null)
                        break;
                }
            } catch (Throwable t) {
                Timber.e(t);
            }
        }
    }

    public static void logEntireClass(Class objClass, Object obj) {
        synchronized (LOG_CLASS_LOCK) {
            try {
                if (objClass == null)
                    return;

                Field[] arrFields = objClass.getDeclaredFields();

                StringBuilder toStringBuilder = new StringBuilder(objClass.getSimpleName() + " {");

                for (Field field : arrFields) {
                    toStringBuilder.append("\n\t")
                            .append(field.getName())
                            .append(": ");

                    if (field.getName().equals("shadow$_klass_") || field.getName().equals("shadow$_monitor_"))
                        continue;

                    Object value = getObjectField(obj, field.getName());

                    if (value != null)
                        toStringBuilder.append(value.toString());
                    else
                        toStringBuilder.append("NULL");
                }

                toStringBuilder.append("}");

                Timber.d(toStringBuilder.toString());
            } catch (Throwable t) {
                Timber.e(t);
            }
        }
    }

    public abstract static class ST_MethodHook extends XC_MethodHook {
//		private static final Map<String, Long> collectiveHookTime = new ConcurrentHashMap<>();
//		private static Long appStartTime;
//		private static boolean isMapLocked;

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//			if (appStartTime == null)
//				appStartTime = System.currentTimeMillis();
//			long startTime = 0;
//			if (BuildConfig.DEBUG) {
//				startTime = System.nanoTime();
//			}

            try {
                before(param);
            } catch (Throwable t) {
                Timber.e(t);
                throw t;
            } /*finally {
				long timeDiff = System.nanoTime() - startTime;

				if (Looper.myLooper() == Looper.getMainLooper() && !isMapLocked && BuildConfig.DEBUG) {
					Member hookMember = param.method;

					Long currentHookTime = collectiveHookTime.get(hookMember.toString());

					if (currentHookTime == null)
						currentHookTime = 0L;

					currentHookTime += timeDiff;
					collectiveHookTime.put(hookMember.toString(), currentHookTime);
				}
			}*/
        }

        protected void before(MethodHookParam param) throws Throwable {
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//			if (appStartTime == null)
//				appStartTime = System.currentTimeMillis();
//
//			long startTime = 0;
//			if (BuildConfig.DEBUG) {
//				startTime = System.nanoTime();
//			}

            try {
                after(param);
            } catch (Throwable t) {
                Timber.e(t);
                throw t;
            } /*finally {
				long timeDiff = System.nanoTime() - startTime;

				if (Looper.myLooper() == Looper.getMainLooper() && !isMapLocked && BuildConfig.DEBUG) {
					Member hookMember = param.method;

					Long currentHookTime = collectiveHookTime.get(hookMember.toString());

					if (currentHookTime == null)
						currentHookTime = 0L;

					currentHookTime += timeDiff;
					collectiveHookTime.put(hookMember.toString(), currentHookTime);
				}
			}*/
        }

        protected void after(MethodHookParam param) throws Throwable {
        }

//		public static void printCollectedData() {
//			long appTime = System.currentTimeMillis() - appStartTime;
//			isMapLocked = true;
//
//			long totalTime = 0;
//			List<Entry<String, Long>> sortedData = new ArrayList<>(collectiveHookTime.entrySet());
//			Collections.sort(sortedData, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
//
//			Log.d("SnapTools_Stats", "Printing out collective hook time");
//			for (Entry<String, Long> hookTime : sortedData) {
//				Log.d("SnapTools_Stats", "Hook Time: " + hookTime.getKey() + " | " + (hookTime.getValue() / 1000000) + "ms");
//				totalTime += hookTime.getValue() / 1000000;
//			}
//			Log.d("SnapTools_Stats", "Printed out all hook times: " + totalTime + "ms | AppTime: " + appTime);
//		}
    }

    public static class ClassMethodHitCounter extends ST_MethodHook {
        private Class<?> linkedClass;
        private LinkedHashMap<Member, Integer> hitCountMap = new LinkedHashMap<>();
        private boolean isEnabled;

        public ClassMethodHitCounter(Class<?> linkedClass) {
            this.linkedClass = linkedClass;
        }

        public Map<Member, Integer> getStats() {
            return hitCountMap;
        }

        public void reset() {
            for (Entry<Member, Integer> entry : hitCountMap.entrySet())
                entry.setValue(0);
        }

        public void start() {
            isEnabled = true;
        }

        public void stop() {
            isEnabled = false;
        }

        public void logStats() {
            Timber.d("Logging stats for %s", linkedClass.getSimpleName());

            List<Pair<Member, Integer>> hitCountList = buildHitCountList();

            for (Pair<Member, Integer> pair : hitCountList) {
                Member member = pair.first;

                if (!(member instanceof Method))
                    continue;

                Method method = (Method) member;
                Integer hitCount = pair.second;

                Timber.d("Stat: [Class: %s][Method: %s(%s)][HitCount: %s]",
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        Arrays.toString(method.getParameterTypes()),
                        hitCount
                );
            }
            Timber.d("Displayed %s stats");
        }

        private List<Pair<Member, Integer>> buildHitCountList() {
            List<Pair<Member, Integer>> hitCountList = new ArrayList<>();

            for (Entry<Member, Integer> entry : hitCountMap.entrySet())
                hitCountList.add(new Pair<>(entry.getKey(), entry.getValue()));

            Collections.sort(
                    hitCountList,
                    (o1, o2) -> o1.second < o2.second ? -1 : o1.second.equals(o2.second) ? 0 : 1
            );

            return hitCountList;
        }

        public static ClassMethodHitCounter hookAllParents(Class<?> clazz) {
            ClassMethodHitCounter instance = hook(clazz);

            Class[] subClasses = clazz.getClasses();

            Timber.d("Hooking Subclasses: " + subClasses.length);

            for (Class subClass : subClasses)
                hook(subClass, instance);

            return instance;
        }

        public static ClassMethodHitCounter hook(Class<?> clazz) {
            ClassMethodHitCounter instance = new ClassMethodHitCounter(clazz);
            return hook(clazz, instance);
        }

        public static ClassMethodHitCounter hook(Class<?> clazz,
                                                 ClassMethodHitCounter instance) {
            if (clazz == null || clazz.getSimpleName().equals("Object"))
                return instance;

            Method[] allMethods = clazz.getDeclaredMethods();

            Timber.d("Methods to hook: " + allMethods.length);

            for (Method baseMethod : allMethods) {
                if (Modifier.isAbstract(baseMethod.getModifiers()))
                    continue;

                instance.hitCountMap.put(baseMethod, 0);

                addUnhook(hookMethod(
                        baseMethod,
                        instance
                ));
            }

            Timber.d("Collecting stats for %s methods", instance.hitCountMap.size());
            return instance;
        }

        @Override
        protected void before(MethodHookParam param) throws Throwable {
            int hitCount = hitCountMap.get(param.method);
            hitCountMap.put(param.method, ++hitCount);
        }
    }
}
