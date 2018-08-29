package com.ljmu.andre.CBIDatabase.Annotations;

import com.ljmu.andre.CBIDatabase.Adapters.AdapterHandler.TypeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //on field level
public @interface Adapter {
    Class<? extends TypeAdapter> TYPE();
}
