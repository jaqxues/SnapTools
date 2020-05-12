package com.ljmu.andre.ConstantDefiner;

import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unused")
public abstract class ConstantDefiner<T extends Constant> {
    private final String NAME;
    private T[] CONST_CACHE;

    public ConstantDefiner() {
        NAME = getClass().getSimpleName();
    }

    public ConstantDefiner(String name) {
        NAME = name;
    }

    @RequiresFramework(73)
    public void purgeCache() {
        CONST_CACHE = null;
    }

    public T[] values() {
        if (CONST_CACHE != null) {
            //Timber.d("Using Cache [Values: %s]", CONST_CACHE.length);
            return CONST_CACHE;
        }

        boolean shouldSort = false;

        //Timber.d("Building values");
        List<T> constantList = new ArrayList<>();
        Class clazz = getClass();
        Class contentClass = null;

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (ConstantDefiner.class.isAssignableFrom(field.getType()))
                continue;

            if (!isPublicStaticFinal(field)) {
                String errMsg = String.format(
                        "Field '%s.%s' should be CONSTANT (public static final)",
                        NAME, field.getName()
                );

                throw new IllegalStateException(errMsg);
            }

            try {
                Object constantDef = field.get(this);

                if (!(constantDef instanceof Constant)) {
                    String errMsg = String.format(
                            "Field '%s.%s' not instance of Constant",
                            NAME, field.getName()
                    );

                    throw new IllegalStateException(errMsg);
                }

                if (contentClass == null)
                    contentClass = constantDef.getClass();

                boolean containsSortIndex = ((Constant) constantDef).getIndex() != null;

                if (!shouldSort && containsSortIndex)
                    shouldSort = true;
                else if (shouldSort && !containsSortIndex) {
                    String errMsg = String.format(
                            "%s is set to use Index Sorting but field %s is missing an index",
                            NAME, field.getName()
                    );

                    throw new IllegalStateException(errMsg);
                }
                //noinspection unchecked
                constantList.add((T) constantDef);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Field '" + field.getName() + "' should be accessible (Public)", e);
            }
        }

        if (shouldSort) {
            //Timber.d("CONSTANT: " + constantList.toString());
            //noinspection ConstantConditions
            Collections.sort(
                    constantList,
                    (o1, o2) -> o1.getIndex().compareTo(o2.getIndex())
            );
            //Timber.d("SORTED: " + constantList.toString());
        }

        Object[] genericTypeArray = (Object[]) Array.newInstance(contentClass, 0);

        //noinspection unchecked,SuspiciousToArrayCall
        return CONST_CACHE = (T[]) constantList.toArray(genericTypeArray);
    }

    private static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();

        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier
                .isFinal(modifiers));
    }

    @Nullable
    public T fromName(String name) {
        for (T constant : values()) {
            if (constant.getName() != null && constant.getName().equals(name))
                return constant;
        }

        return null;
    }

    public int size() {
        return values().length;
    }
}
