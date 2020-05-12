package com.ljmu.andre.GsonPreferences;

import androidx.annotation.Nullable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class DeadlockItemModel {
    private String name;
    private StackTraceElement[] traceElements;

    private DeadlockItemModel(String name, StackTraceElement[] traceElements) {
        this.name = name;
        this.traceElements = traceElements;
    }

    static DeadlockItem generate(String name, StackTraceElement[] traceElements,
                                 @Nullable Throwable previousNode) {
        return new DeadlockItemModel(name, traceElements).new DeadlockItem(previousNode);
    }

    @SuppressWarnings("SerializableInnerClassWithNonSerializableOuterClass")
    class DeadlockItem extends Throwable {
        private static final long serialVersionUID = -2891934505164278935L;

        private DeadlockItem(Throwable previousNode) {
            super(name, previousNode);
        }

        /**
         * ===========================================================================
         * Overridden toString() to remove the original class names from the stack.
         * If not overridden each stack trace will be prepended with:
         * "com.ljmu.andre.GsonPreference.DeadlockItemModel$DeadlockItem"
         * followed by the full classname of the stacktrace item.
         * <p>
         * This function will only prepend the stacktrace with "Deadlock Item"
         * ===========================================================================
         */
        @Override
        public String toString() {
            String s = "Deadlock Item";
            String message = getLocalizedMessage();
            return (message != null) ? (s + ": " + message) : s;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            setStackTrace(traceElements);
            return this;
        }
    }
}
