package com.ljmu.andre.snaptools.Framework.Utils;

import androidx.annotation.Nullable;

/**
 * ===========================================================================
 * An interface that can be used to redirect functions to a ModulePack
 * without requiring an APK or ModulePack combination to be enforced.
 * ===========================================================================
 */
public interface CompatibilityRedirector {
    @Nullable
    Object redirect(String id, Object defaultReturn, @Nullable Object... params);
}
