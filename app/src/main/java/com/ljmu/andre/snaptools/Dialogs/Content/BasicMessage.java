package com.ljmu.andre.snaptools.Dialogs.Content;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getStyle;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BasicMessage implements ThemedDialog.ThemedDialogExtension {
    private ThemedClickListener okayListener;
    private Spanned message;
    private Integer errorCode;
    private boolean isButtonError;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context moduleContext = ContextHelper.getModuleContext(parent.getContext());
        inflater.inflate(getLayout(moduleContext, "dialog_basic_message"), content, true);

        Button btn_okay = getView(parent, "btn_okay");

        if (isButtonError) {
            btn_okay.setBackgroundResource(getDrawable(moduleContext, "error_button"));
            btn_okay.setTextAppearance(moduleContext, getStyle(moduleContext, "ErrorButton"));
        }

        if (okayListener == null) {
            okayListener = new ThemedClickListener() {
                @Override
                public void clicked(ThemedDialog themedDialog) {
                    themedDialog.dismiss();
                }
            };
        }
        okayListener.setDialog(themedDialog);

        btn_okay.setOnClickListener(okayListener);

        TextView txt_message = getView(parent, "txt_message");

        if (txt_message != null)
            txt_message.setText(message);

        if (errorCode != null) {
            LinearLayout layoutErrorCode = getView(parent, "error_code_container");
            TextView txtErrorCode = getView(parent, "txt_error_code");
            txtErrorCode.setText(errorCode.toString());

            layoutErrorCode.setVisibility(View.VISIBLE);
        }
    }

    public BasicMessage setClickListener(ThemedClickListener clickListener) {
        this.okayListener = clickListener;
        return this;
    }

    public BasicMessage setMessage(String message) {
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

        return this;
    }

    public BasicMessage isButtonError(boolean isButtonError) {
        this.isButtonError = isButtonError;
        return this;
    }

    public BasicMessage setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
