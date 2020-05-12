package com.ljmu.andre.snaptools.Dialogs.Content;

import android.content.Context;
import android.os.Build;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Networking.VolleyHandler;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Progress implements ThemedDialog.ThemedDialogExtension {
    private String volleyTag;
    private Spanned message;
    private boolean cancelable = true;
    private boolean showProgress;
    private ProgressBar progressBar;
    @Nullable
    private Callable<Void> cancelCallback;

    private TextView txtMessage;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context moduleContext = ContextHelper.getModuleContext(parent.getContext());
        inflater.inflate(getLayout(moduleContext, "dialog_progress"), content, true);

        txtMessage = getView(content, "txt_message");
        txtMessage.setText(message);

        progressBar = getView(content, "progress");

        if (showProgress)
            progressBar.setIndeterminate(false);

        Button okayButton = getView(content, "btn_okay");

        if (!cancelable)
            okayButton.setVisibility(View.GONE);
        else {
            okayButton.setOnClickListener(
                    v -> {
                        if (volleyTag != null)
                            VolleyHandler.getInstance().cancelPendingRequests(volleyTag);

                        if (cancelCallback != null)
                            cancelCallback.call(null);

                        themedDialog.dismiss();
                    }
            );
        }
    }

    public Progress setVolleyTag(String volleyTag) {
        this.volleyTag = volleyTag;
        return this;
    }

    public Progress setMessage(String message) {
        try {
            message = message.replaceAll("\n", "<br>");
            this.message = SDK_INT >= Build.VERSION_CODES.N
                    ?
                    Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
                    :
                    Html.fromHtml(message);
        } catch (Throwable e) {
            Timber.e(e);
            this.message = new SpannableString(message);
        }

        if (txtMessage != null) {
            txtMessage.setText(this.message);
        }
        return this;
    }

    public Progress setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public Progress setCancelCallback(@Nullable Callable<Void> cancelCallback) {
        this.cancelCallback = cancelCallback;
        return this;
    }

    public Progress enableProgress() {
        showProgress = true;
        return this;
    }

    public Progress setProgress(@IntRange(from = 0, to = 100) int progress) {
        Timber.d("Setting progress: " + progress);
        if (progressBar != null)
            progressBar.setProgress(progress);
        return this;
    }
}
