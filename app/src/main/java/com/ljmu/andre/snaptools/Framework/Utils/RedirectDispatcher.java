package com.ljmu.andre.snaptools.Framework.Utils;

import timber.log.Timber;

import java.util.Arrays;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public interface RedirectDispatcher {
    @SuppressWarnings("unchecked")
    default <T> T dispatchRedirection(String id, T defaultValue, Object... params) {
        CompatibilityRedirector redirector = getRedirector();

        if (redirector == null) {
            Timber.w("Tried to redirect with no redirector [Id: %s][Params: %s]", id, Arrays.toString(params));
            return defaultValue;
        }

        return (T) redirector.redirect(id, defaultValue, params);
    }

    CompatibilityRedirector getRedirector();
}
