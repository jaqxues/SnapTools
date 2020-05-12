package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.Nullable;

import com.android.volley.Request.Method;
import com.google.gson.Gson;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SlackUtils {
    public static void uploadToSlack(String username, String message) {
        new WebRequest.Builder()
                .setMethod(Method.POST)
                .addParam("payload", new WebhookHolder().getJson(username, message))
                .setType(RequestType.STRING)
                .setUrl("https://discordapp.com/api/webhooks/373887141828100097/wQQxWKvnQVu-V5swMemxABNUvkWoajqZDk87pJVL9NSN2ODkmVuCraOwB9Qnj6oUZwQL/slack")
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                    }
                })
                .performRequest();
    }

    private static class WebhookHolder {
        String channel = "#crashlogs";
        String username = "CrashLog";
        String text;
        String icon_emoji = ":bangbang:";

        String getJson(@Nullable String username, String message) {
            if (username != null)
                this.username = username;

            text = "```" + message + "```";

            return new Gson().toJson(this);
        }
    }
}
