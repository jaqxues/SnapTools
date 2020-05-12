package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.NonNull;
import android.util.Log;

import com.ljmu.andre.ErrorLogger.ErrorLogger;
import com.ljmu.andre.snaptools.STApplication;

import java.util.HashSet;

import de.robv.android.xposed.XposedBridge;
import timber.log.Timber;
import timber.log.Timber.DebugTree;
import timber.log.Timber.Tree;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TimberUtils {
    public static void plantAppropriateXposedTree() {
        if (STApplication.DEBUG)
            plantCheck(new XposedDebugTree(), "XposedDebug");
        else
            plantCheck(new XposedReleaseTree(), "XposedRelease");
    }

    private static void plantCheck(Tree tree, String treeName) {
        Timber.plant(tree);
        Timber.d("Planted [Tree:%s]", treeName);
    }

    public static void plantAppropriateTree() {
        if (STApplication.DEBUG) {
            plantCheck(new DebugTree() {
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format(
                            "%s-[%s ⇢ %s:%s]",
                            STApplication.MODULE_TAG,
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber());
                }

            }, "Debug");
        } else {
            plantCheck(new ReleaseTree(), "Release");
            Timber.plant(new ReporterTree());
        }
    }

    private static class ReporterTree extends ReleaseTree {
        private static final HashSet<Integer> priorityWhitelist;

        static {
            priorityWhitelist = new HashSet<>();
            priorityWhitelist.add(Log.WARN);
            priorityWhitelist.add(Log.ERROR);
            priorityWhitelist.add(Log.ASSERT);
        }

        @Override
        protected boolean isLoggable(String tag, int priority) {
            return priority >= Log.WARN;
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
//			if (message != null && priority >= Log.INFO
//					&& Fabric.isInitialized() && Crashlytics.getInstance() != null) {
//				Crashlytics.doLog(priority, tag, message);
//			}

            if (isLoggable(tag, priority)) {
                ErrorLogger errorLogger = ErrorLogger.getInstance();

                if (message != null && errorLogger != null) {
                    errorLogger.addError(priority, message);
                }

//				if (t != null && priority == Log.ERROR && Fabric.isInitialized() && Crashlytics.getInstance() != null) {
//					Crashlytics.logException(t);
//				}
            }
        }
    }

    private static class ReleaseTree extends DebugTree {

        @Override
        protected boolean isLoggable(String tag, int priority) {
            return priority >= Log.INFO;
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (isLoggable(tag, priority)) {
                super.log(priority, tag, message, t);
            }
        }

        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format(
                    "%s-[%s ⇢ %s:%s]",
                    STApplication.MODULE_TAG,
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber());
        }
    }

    private static class XposedReleaseTree extends ReleaseTree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (isLoggable(tag, priority)) {
                super.log(priority, tag, message, t);

                XposedBridge.log(tag + ": " + message);
                if (t != null)
                    XposedBridge.log(t);
            }
        }
    }

    private static class XposedDebugTree extends DebugTree {
        @Override
        protected String createStackElementTag(@NonNull StackTraceElement element) {
            return String.format(
                    "%s-[%s ⇢ %s:%s]",
                    STApplication.MODULE_TAG,
                    super.createStackElementTag(element),
                    element.getMethodName(),
                    element.getLineNumber());
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            super.log(priority, tag, message, t);

            XposedBridge.log(tag + ": " + message);
            if (t != null)
                XposedBridge.log(t);
        }
    }
}
