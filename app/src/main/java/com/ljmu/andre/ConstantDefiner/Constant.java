package com.ljmu.andre.ConstantDefiner;

import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Constant {
    @SerializedName("index")
    @Nullable
    private Integer index;

    @SerializedName("name")
    @Nullable
    private String name;

    public Constant(int index, @Nullable String name) {
        this.index = index;
        this.name = name;
    }

    public Constant(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Integer getIndex() {
        return index;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("index", index)
                .add("name", name)
                .add("", super.toString())
                .toString();
    }
}
