package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
@Deprecated
public class CallbackHandler {
    private final String toString;
    @NonNull
    private final Object caller;
    @NonNull
    private final Method method;
    private Object[] parameters;

    private CallbackHandler(@NonNull Object caller, @NonNull Method method, @Nullable Object... parameters) {
        this.toString = String.format("[Caller:%s][Method:%s][Params:%s]", caller.getClass().getSimpleName(), method.getName(), Arrays.toString(parameters));
        Timber.d("Generating callback method %s", this.getToString());

        this.caller = caller;
        this.method = method;
        this.parameters = parameters;
    }

    public String getToString() {
        return toString;
    }

    @Nullable
    public Object invokeCallback(@Nullable Object... parameters) {
        if (parameters != null && parameters.length > 0)
            this.addParams(parameters);

        try {
            Timber.d("Invoking callback method %s", this.getToString());
            return this.getMethod().invoke(this.caller, this.parameters);
        } catch (Exception e) {
            Timber.e(e, "Exception invoking callback method");
            return null;
        }
    }

    @Nullable
    Object[] addParams(@NonNull Object... newParams) {
        int newParamLength = newParams.length;

        Object[] newParamList = new Object[parameters.length + newParamLength];

        System.arraycopy(parameters, 0, newParamList, 0, parameters.length);
        System.arraycopy(newParams, 0, newParamList, parameters.length, newParamLength);

        this.parameters = newParamList;

        return this.parameters;
    }

    @NonNull
    public Method getMethod() {
        return method;
    }

    /**
     * Usage: getCallback(ObjectInstance, "methodToCall", ParameterClassTypes...);
     *
     * @param clazz      - The Object instance containing the method
     * @param methodName - The name of the method to read
     * @param classType  - The list of Classes called as the method parameters
     * @return CallbackHandler - The object holding the callback data
     */
    @Nullable
    public static CallbackHandler getCallback(@NonNull Object clazz, @NonNull String methodName, @Nullable Class<?>... classType) {
        try {
            return new CallbackHandler(clazz, clazz.getClass().getMethod(methodName, classType));
        } catch (NoSuchMethodException e) {
            Timber.e(e, "Exception generating callback method");
            return null;
        }
    }
}
