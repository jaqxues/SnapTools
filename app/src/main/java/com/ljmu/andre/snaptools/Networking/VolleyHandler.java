package com.ljmu.andre.snaptools.Networking;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ljmu.andre.snaptools.STApplication;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class VolleyHandler {
    private static VolleyHandler mInstance;
    private Context context;
    private RequestQueue httpRequestQueue;

    private VolleyHandler(Context context) {
        this.context = context;
        httpRequestQueue = Volley.newRequestQueue(context);
    }

    @SuppressWarnings("unused")
    public <T> void addToRequestQueue(@NonNull Request<T> req) {
        addToRequestQueue(req, null);
    }

    public <T> void addToRequestQueue(@NonNull Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? STApplication.MODULE_TAG : tag);
        getHttpRequestQueue().add(req);
    }

    public RequestQueue getHttpRequestQueue() {
        if (httpRequestQueue == null)
            httpRequestQueue = Volley.newRequestQueue(context);

        return httpRequestQueue;
    }

    public void cancelPendingRequests(@NonNull Object tag) {
        if (httpRequestQueue != null)
            httpRequestQueue.cancelAll(tag);
    }

    public static void init(Context context) {
        mInstance = new VolleyHandler(context);
    }

    public static synchronized VolleyHandler getInstance() {
        if (mInstance == null)
            throw new IllegalStateException("VolleyHandler not Initialised!");

        return mInstance;
    }
}
