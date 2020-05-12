package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import android.view.View;

import static com.ljmu.andre.snaptools.Utils.ResourceMapper.getResId;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

/**
 * ===========================================================================
 * Helper methods to resolve string id's to their resource representation.
 * This is often required as the Framework/APK is likely to be a different
 * build version to the loaded ModulePack and so the resource ID's will almost
 * certainly mismatch.
 * This way the resources are dynamically fetched at runtime.
 * <p>
 * It is also extremely valuable to use generics in these helpers as automatic
 * casting makes inline functions much more simplistic.
 * E.g. ResourceUtils.<TextView>getView(root, "id").setText("Text");
 * ===========================================================================
 */
public class ResourceUtils {
    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(View root, String id) {
        return (T) getView(root, getId(root.getContext(), id));
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(View root, int id) {
        return (T) root.findViewById(id);
    }

    @IdRes
    public static int getId(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "id");
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(Activity activity, String id) {
        return (T) getView(activity, getId(activity, id));
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getView(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getDSLView(View root, String id) {
        return (T) root.findViewById(getIdFromString(id));
    }

    @IdRes
    public static int getIdFromString(String name) {
        return Math.abs(name.hashCode());
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getDSLView(Activity root, String id) {
        return (T) root.findViewById(getIdFromString(id));
    }

    @DrawableRes
    public static int getDrawable(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "drawable");
    }

    @StyleRes
    public static int getStyle(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "style");
    }

    @LayoutRes
    public static int getLayout(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "layout");
    }

    @StringRes
    public static int getString(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "string");
    }

    @ColorRes
    public static int getColor(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "color");
    }

    @DimenRes
    public static int getDimen(Context context, @Nullable String id) {
        if (id == null)
            return 0;

        return getResId(context, id, "dimen");
    }
}
