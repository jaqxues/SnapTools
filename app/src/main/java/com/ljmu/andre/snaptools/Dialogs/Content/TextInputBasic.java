package com.ljmu.andre.snaptools.Dialogs.Content;


import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedDialogExtension;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 * <p>
 * This class was modifed by Ethan on 8/16/2017
 */

public class TextInputBasic implements ThemedDialogExtension {
    private EditText txtInput;

    private ThemedClickListener okayClickListener;
    private Integer inputType;
    private String inputMessage;
    private Spanned message;
    private String hint;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context moduleContext = ContextHelper.getModuleContext(parent.getContext());
        inflater.inflate(getLayout(moduleContext, "dialog_text_input_basic"), content, true);

        Window window = themedDialog.getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        if (okayClickListener == null) {
            okayClickListener = new ThemedClickListener() {
                @Override
                public void clicked(ThemedDialog themedDialog1) {
                    themedDialog1.dismiss();
                }
            };
        }
        okayClickListener.setDialog(themedDialog);

        getView(content, "btn_okay").setOnClickListener(okayClickListener);

        if (message != null)
            ((TextView) getView(content, "txt_message")).setText(message);

        txtInput = getView(content, "txt_input");
        txtInput.setHint(hint);

        if (inputType != null)
            txtInput.setInputType(inputType);

        if (inputMessage != null)
            txtInput.setText(inputMessage);
    }


    public TextInputBasic setOkayClickListener(ThemedClickListener yesClickListener) {
        this.okayClickListener = yesClickListener;
        return this;
    }


    public TextInputBasic setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public TextInputBasic setMessage(String message) {
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

    public String getInputMessage() {
        return txtInput.getText().toString();
    }

    public TextInputBasic setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
        return this;
    }

    public TextInputBasic setInputType(Integer inputType) {
        if (inputType == null)
            return this;

        this.inputType = inputType;
        return this;
    }
}
