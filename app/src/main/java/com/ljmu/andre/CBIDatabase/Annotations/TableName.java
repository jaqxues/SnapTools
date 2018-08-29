package com.ljmu.andre.CBIDatabase.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface TableName {
    String value();

    int VERSION() default 1;
}
