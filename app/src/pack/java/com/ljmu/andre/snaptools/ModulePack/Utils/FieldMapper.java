package com.ljmu.andre.snaptools.ModulePack.Utils;

import com.ljmu.andre.snaptools.Utils.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

/**
 * ===========================================================================
 * Used explicitly for Serializable classes to map the @SerializedName
 * to the Field name.
 * <p>
 * For example, a class may follow the following structure:
 * <p>
 * // ===========================================================================
 * // @SerializedName("someJsonName")
 * // private int a;
 * // ===========================================================================
 * <p>
 * ^ This will be mapped to a key/value entry with the serialized name as the key
 * ===========================================================================
 */
@SuppressWarnings("SerializableHasSerializationMethods")
public class FieldMapper extends ConcurrentHashMap<String, String> {
    private static final long serialVersionUID = -4101144694754307740L;
    private static final Map<String, FieldMapper> classFieldMap = new HashMap<>();
    private final String linkName;
    private final Class linkClass;

    private FieldMapper(String link, Class linkClass) {
        this.linkName = link;
        this.linkClass = linkClass;
        classFieldMap.put(link, this);
    }

    public String getLinkName() {
        return linkName;
    }

    public Class getLinkClass() {
        return linkClass;
    }

    public void setField(Object linkInstance, String fieldTag, Object newValue) {
        String fieldName = get(fieldTag);
        Assert.notNull("Couldn't find field for " + fieldTag, fieldName);

        setObjectField(linkInstance, fieldName, newValue);
    }

    public <T> T getFieldVal(Object linkInstance, String fieldTag) {
        String fieldName = get(fieldTag);
        Assert.notNull("Couldn't find field for " + fieldTag, fieldName);

        return (T) getObjectField(linkInstance, fieldName);
    }

    public static FieldMapper initMapper(String linkName, Class<?> linkClass) {
        FieldMapper storedMapper = classFieldMap.get(linkName);

        if (storedMapper == null) {
            storedMapper = new FieldMapper(linkName, linkClass);
            storedMapper.buildFieldMap(linkClass);
        }

        return storedMapper;
    }

    private void buildFieldMap(Class lensClass) {
        Field[] fields = lensClass.getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();

            if (annotations.length <= 0)
                continue;

            String annotationName = (String) callMethod(annotations[0], "value");

            Timber.d("AnnotationName: " + annotationName + " FieldName: " + field.getName());
            put(annotationName, field.getName());
        }

        Timber.d("Built %s fields", size());
    }

    public static void removeMapper(String linkName) {
        classFieldMap.remove(linkName);
    }

    public static FieldMapper getMapper(String linkName) {
        return classFieldMap.get(linkName);
    }
}
