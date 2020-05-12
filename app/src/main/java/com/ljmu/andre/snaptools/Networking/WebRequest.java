package com.ljmu.andre.snaptools.Networking;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView.ScaleType;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import org.json.JSONException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class WebRequest {
    private final int method;
    private final boolean clearCache;
    private final boolean hideAuthTest;
    private final boolean disableIntegrityPinning;
    @NonNull
    private final RequestType type;
    @Nullable
    private final Class<? extends Packet> packetClass;
    @NonNull
    private final String url;
    @Nullable
    private final String tag;
    @NonNull
    private final Map<String, String> headers;
    @NonNull
    private final Map<String, String> params;
    @Nullable
    private final byte[] body;
    @Nullable
    private final RetryPolicy retryPolicy;
    @NonNull
    private final WebResponseListener callback;

    @NonNull
    private final ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            callback.error(new WebResponse("Error with Volley Request", error, url));
        }
    };

    private Request<?> request;

    private WebRequest(
            int method,
            boolean clearCache,
            boolean hideAuthTest,
            boolean disableIntegrityPinning,
            @Nullable Context context,
            @NonNull RequestType type,
            @Nullable Class<? extends Packet> packetClass,
            @NonNull String url,
            @Nullable String tag,
            @NonNull Map<String, String> headers,
            @NonNull Map<String, String> params,
            @Nullable byte[] body,
            @Nullable RetryPolicy retryPolicy,
            @NonNull WebResponseListener callback
    ) {
        this.method = method;
        this.clearCache = clearCache;
        this.hideAuthTest = hideAuthTest;
        this.disableIntegrityPinning = disableIntegrityPinning;
        this.type = type;
        this.packetClass = packetClass;
        this.url = url;
        this.tag = tag;
        this.headers = headers;
        this.params = params;
        this.body = body;
        this.retryPolicy = retryPolicy;
        this.callback = callback;

        if (context != null && !MiscUtils.isNetworkAvailable(context)) {
            this.callback.error(new WebResponse("No Network Connection!", null, -1, this.url));
            return;
        }

        Timber.d("Making Request: " + this.toString());

        buildRequest();
        performRequest();
    }

    public static String assertParam(Class callingClass, String errorMessage, String param) throws IllegalArgumentException {
        return Assert.stringExists(formatParamError(callingClass, errorMessage), param);
    }

    public static String formatParamError(Class callingClass, String errorMessage) {
        return String.format("[%s]: %s", callingClass.getSimpleName(), errorMessage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("method", method)
                .add("type", type)
                .add("url", url)
                .add("name", tag)
                .add("headers", headers)
                .add("params", params)
                .add("body", body)
                .add("retryPolicy", retryPolicy)
                .add("callback", callback)
                .toString();
    }

    private void buildRequest() {
        switch (type) {
            case STRING:
            case PACKET:
                buildStringRequest();
                break;
            case STREAM:
                buildByteRequest();
                break;
            case BITMAP:
                buildBitmapRequest();
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("[Type: %s] is not defined in the WebRequest System", type.getClass()));
        }

        if (!disableIntegrityPinning) {
            try {
                byte[] body = request.getBody();
                int bodyLength = -1;
                if (body != null)
                    bodyLength = body.length;

                headers.put("Encode", String.valueOf(bodyLength));
            } catch (AuthFailureError authFailureError) {
                Timber.e(authFailureError);
            }
        }
    }

    private void performRequest() {
        Timber.d("Adding Request to Queue");

        if (clearCache)
            VolleyHandler.getInstance().getHttpRequestQueue().getCache().clear();

        VolleyHandler.getInstance().addToRequestQueue(request, tag);
    }

    private void buildStringRequest() {
        Timber.d("Building String Request");

        request = new StringRequest(method, url, response -> {
            try {
                Timber.d("WebResponse: " + response);

                Object payload;

                if (type == RequestType.STRING)
                    payload = response;
                else if (type == RequestType.PACKET) {
                    Assert.notNull("Null PacketClass", packetClass);
                    Packet packet = new Gson().fromJson(response, packetClass);

                    if (packet == null) {
                        Timber.w("No Response Packet");
                        callback.error(new WebResponse("No response packet", null, 203, url));
                        return;
                    }

                    Timber.d("Response Packet: " + packet.toString());

                    if (packet.error) {
                        callback.error(new WebResponse(packet.getErrorMessage(), null, packet.getErrorCode(), url));
                        return;
                    }

                    payload = packet;

                } else
                    throw new IllegalArgumentException("Unhandled response type in StringRequest: " + type.getClass());

                callback.success(new WebResponse(payload, url));

            } catch (JSONException exception) {

                callback.error(new WebResponse("Error parsing JSON", exception, url));

            } catch (Throwable throwable) {

                callback.error(new WebResponse("Unknown Error", throwable, url));

            }
        }, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body != null ? body : super.getBody();
            }
        };

        if (retryPolicy != null)
            request.setRetryPolicy(retryPolicy);
    }

    private void buildByteRequest() {
        Timber.d("Building Byte Request");

        request = new Request<byte[]>(method, url, errorListener) {
            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
                return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
            }


            @Override
            protected void deliverResponse(byte[] response) {
                Timber.d("Successfully received [%s] bytes", response.length);

                try {

                    callback.success(new WebResponse(response, url));

                } catch (Throwable throwable) {

                    callback.error(new WebResponse("Unknown Error", throwable, url));

                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body != null ? body : super.getBody();
            }
        };

        if (retryPolicy != null)
            request.setRetryPolicy(retryPolicy);
    }

    private void buildBitmapRequest() {
        Timber.d("Building bitmap request");

        request = new ImageRequest(url, response -> {
            try {

                callback.success(new WebResponse(response, url));

            } catch (Throwable throwable) {

                callback.error(new WebResponse("Unknown Error", throwable, url));

            }
        }, 1024, 1024, ScaleType.CENTER, null, errorListener);

        if (retryPolicy != null)
            request.setRetryPolicy(retryPolicy);
    }

    public void cancel() {
        if (tag == null)
            throw new IllegalArgumentException("Tried to Cancel WebRequest with no Tag");

        VolleyHandler.getInstance().cancelPendingRequests(tag);
    }

    public enum RequestType {
        PACKET, STREAM, STRING, BITMAP
    }

    public interface WebResponseListener {
        void success(WebResponse webResponse) throws Throwable;

        void error(WebResponse webResponse);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private static final RetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(5000, 6, 1);
        private int method = Method.POST;
        private boolean clearCache = true; //TODO Remove this on release
        private boolean hideAuthTest;
        private boolean disableIntegrityPinning;
        private Context context;
        private RequestType type;
        private Class<? extends Packet> packetClass;
        private String url;
        private String tag;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> params = new HashMap<>();
        private byte[] body;
        private RetryPolicy retryPolicy;
        private WebResponseListener callback;

        public int getMethod() {
            return method;
        }

        public Builder setMethod(int method) {
            this.method = method;
            return this;
        }

        public boolean getShouldClearCache() {
            return clearCache;
        }

        public Builder shouldClearCache(boolean clearCache) {
            this.clearCache = clearCache;
            return this;
        }

        public Builder hideAuthTest() {
            this.hideAuthTest = true;
            return this;
        }

        public Builder disableIntegrityPinning() {
            disableIntegrityPinning = true;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public RequestType getType() {
            return type;
        }

        public Builder setType(RequestType type) {
            this.type = type;
            return this;
        }

        public Class<? extends Packet> getPacketClass() {
            return packetClass;
        }

        public Builder setPacketClass(Class<? extends Packet> packetClass) {
            this.packetClass = packetClass;
            setType(RequestType.PACKET);
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getTag() {
            return tag;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Builder setHeaders(Map<String, String> headers) {
            if (!checkNotNull(headers.values())) {
                Timber.e(new Throwable("Supplied null header value"));
                return this;
            }

            this.headers = headers;
            return this;
        }

        public boolean checkNotNull(Collection<?> collection) {
            for (Object obj : collection) {
                if (obj == null)
                    return false;
            }

            return true;
        }

        public Builder addHeader(String key, String header) {
            if (header == null) {
                Timber.e(new Throwable("Supplied null header value for " + key));
                return this;
            }

            this.headers.put(key, header);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            if (!checkNotNull(headers.values())) {
                Timber.e(new Throwable("Supplied null header value"));
                return this;
            }

            this.headers.putAll(headers);
            return this;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public Builder setParams(Map<String, String> params) {
            if (!checkNotNull(params.values())) {
                Timber.e(new Throwable("Supplied null param value"));
                return this;
            }

            this.params = params;
            return this;
        }

        public Builder addParam(Map<String, String> params) {
            if (!checkNotNull(params.values())) {
                Timber.e(new Throwable("Supplied null param value"));
                return this;
            }

            this.params.putAll(params);
            return this;
        }

        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder setRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder useDefaultRetryPolicy() {
            this.retryPolicy = DEFAULT_RETRY_POLICY;
            return this;
        }

        public WebResponseListener getCallback() {
            return callback;
        }

        public Builder setCallback(WebResponseListener callback) {
            this.callback = callback;
            return this;
        }

        public WebRequest performRequest() {
            Assert.notNull("No URL, CALLBACK, (or CONTEXT) provided! " +
                            "[Url: " + url + "] [Callback: " + callback + "] [Context: " + context + "]",
                    url, callback);

            addParam("code", String.valueOf(BuildConfig.VERSION_CODE));
            addParam("flavour", BuildConfig.FLAVOR);
            addParam("auth_bypass", String.valueOf(true));

            return new WebRequest(
                    method,
                    clearCache,
                    hideAuthTest,
                    disableIntegrityPinning,
                    context,
                    type,
                    packetClass,
                    url,
                    tag,
                    headers,
                    params,
                    body,
                    retryPolicy,
                    callback);
        }

        public Builder addParam(String key, String param) {
            if (param == null) {
                Timber.e(new Throwable("Supplied null param value for: " + key));
                return this;
            }

            this.params.put(key, param);
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("url", url)
                    .add("name", tag)
                    .add("headers", headers)
                    .add("params", params)
                    .add("body", body)
                    .add("retryPolicy", retryPolicy)
                    .add("callback", callback)
                    .toString();
        }
    }
}
