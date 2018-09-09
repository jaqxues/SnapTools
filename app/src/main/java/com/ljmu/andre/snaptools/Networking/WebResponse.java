package com.ljmu.andre.snaptools.Networking;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;

import java.net.UnknownHostException;
import java.util.List;

import javax.net.ssl.SSLException;

import timber.log.Timber;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class WebResponse {
    private final String message;
    private final String failedUrl;
    private Object result;
    private Throwable exception;
    private int responseCode = 0;
    private int webStatusCode;

    WebResponse(Object result, String failedUrl) {
        this(result, "SUCCESS", failedUrl);
    }

    private WebResponse(Object result, String message, String failedUrl) {
        this.message = message;
        this.result = result;
        this.failedUrl = failedUrl;
        Timber.d("Web Response [Msg: %s]", message);
    }

    WebResponse(String message, Throwable exception, String failedUrl) {
        this(message, exception, -1, failedUrl);
    }

    WebResponse(String message, Throwable exception, int errorCode, String failedUrl) {
        this.message = message;
        this.exception = exception;
        this.responseCode = errorCode;
        this.failedUrl = failedUrl;

        if (exception == null)
            return;

        if (exception instanceof VolleyError) {
            VolleyError volleyError = (VolleyError) exception;
            if (volleyError.networkResponse != null) {
                webStatusCode = volleyError.networkResponse.statusCode;
            }
        }

        if (isNetworkProblem(exception) || isCronetError(exception)) {
            Timber.w(exception, message);

            this.exception = null;
            if (responseCode == -1 || responseCode == 0)
                responseCode = 1;
        } else if (isServerProblem(exception)) {
            this.exception = transformServerException(exception);

            if (responseCode == -1 || responseCode == 0)
                responseCode = 2;
        }
    }

    private static boolean isNetworkProblem(Object error) {
        return error instanceof NetworkError || error instanceof TimeoutError
                || error instanceof UnknownHostException || error instanceof SSLException;
    }

    private static boolean isCronetError(Object error) {
        String errorClassName = error.getClass().getSimpleName();
        return errorClassName.equals("QuicException") ||
                errorClassName.equals("UrlRequestException");
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private Throwable transformServerException(Throwable exception) {
        if (exception instanceof VolleyError) {
            VolleyError volleyError = (VolleyError) exception;
            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                return new VolleyError(
                        "Url: " + failedUrl + " | Status: " + volleyError.networkResponse.statusCode
                                + " | " + new String(volleyError.networkResponse.data),
                        exception
                );
            }
        }

        return exception;
    }

    public <T> T getResult() {
        return (T) result;
    }

    public String getMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Throwable getException() {
        return exception;
    }

    public int getWebStatusCode() {
        return webStatusCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("message", message)
                .add("success", result)
                .add("exception", exception)
                .add("responseCode", responseCode)
                .toString();
    }

    public interface ServerListResultListener<T> {
        void success(List<T> list);

        void error(String message, Throwable t, int errorCode);
    }

    public interface PacketResultListener<T extends Packet> {
        void success(String message, T packet);

        void error(String message, Throwable t, int errorCode);
    }

    public interface ObjectResultListener<T> {
        void success(String message, T object);

        void error(String message, Throwable t, int errorCode);

        abstract class ErrorObjectResultListener<T> implements ObjectResultListener<T> {
            @Override
            public void success(String message, Object object) {
            }
        }
    }
}
