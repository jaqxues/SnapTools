package com.ljmu.andre.ConstantDefiner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BasicConstant<T> extends Constant {
    @NonNull
    private final T value;

    public BasicConstant(int index, @Nullable String name, @NonNull T value) {
        super(index, name);
        this.value = value;
    }

    public BasicConstant(@Nullable String name, @NonNull T value) {
        super(name);
        this.value = value;
    }

    @NonNull
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("value", value)
                .add("", super.toString())
                .toString();
    }
}
