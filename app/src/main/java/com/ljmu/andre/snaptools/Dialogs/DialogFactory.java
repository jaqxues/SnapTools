package com.ljmu.andre.snaptools.Dialogs;

import android.app.Activity;
import androidx.annotation.CheckResult;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.Dialogs.Content.BasicMessage;
import com.ljmu.andre.snaptools.Dialogs.Content.Confirmation;
import com.ljmu.andre.snaptools.Dialogs.Content.Options;
import com.ljmu.andre.snaptools.Dialogs.Content.Options.OptionsButtonData;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.Content.TextInput;
import com.ljmu.andre.snaptools.Dialogs.Content.TextInputBasic;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DialogFactory {

    @CheckResult
    public static ThemedDialog createProgressDialog(Activity activity, String title, String message) {
        return createProgressDialog(activity, title, message, true);
    }

    @CheckResult
    public static ThemedDialog createProgressDialog(Activity activity, String title, String message,
                                                    boolean cancelable) {
        return createProgressDialog(activity, title, message, null, cancelable);
    }

    @CheckResult
    public static ThemedDialog createProgressDialog(Activity activity, String title, String message,
                                                    String volleyTag, boolean cancelable) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(
                        new Progress()
                                .setMessage(message)
                                .setVolleyTag(volleyTag)
                                .setCancelable(cancelable)
                );
    }

    @CheckResult
    public static ThemedDialog createTextInputDialog(Activity activity, String title, String message) {
        return createTextInputDialog(activity, title, message, null, null);
    }

    @CheckResult
    public static ThemedDialog createTextInputDialog(Activity activity, String title, String message,
                                                     String hint, String defaultText) {
        return createTextInputDialog(activity, title, message, hint, defaultText, null, null);
    }

    @CheckResult
    public static ThemedDialog createTextInputDialog(Activity activity, String title, String message,
                                                     String hint, String defaultText, Integer inputType,
                                                     ThemedClickListener yesListener) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(
                        new TextInput()
                                .setMessage(message)
                                .setHint(hint)
                                .setInputMessage(defaultText)
                                .setInputType(inputType)
                                .setYesClickListener(yesListener)
                );
    }

    ///////
    @CheckResult
    public static ThemedDialog createBasicTextInputDialog(Activity activity, String title, String message) {
        return createBasicTextInputDialog(activity, title, message, null, null);
    }

    @CheckResult
    public static ThemedDialog createBasicTextInputDialog(Activity activity, String title, String message,
                                                          String hint, String defaultText) {
        return createBasicTextInputDialog(activity, title, message, hint, defaultText, null, null);
    }

    @CheckResult
    public static ThemedDialog createBasicTextInputDialog(Activity activity, String title, String message,
                                                          String hint, String defaultText, Integer inputType,
                                                          ThemedClickListener okayListener) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(
                        new TextInputBasic()
                                .setMessage(message)
                                .setHint(hint)
                                .setInputMessage(defaultText)
                                .setInputType(inputType)
                                .setOkayClickListener(okayListener)
                );
    }

    ///////
    @CheckResult
    public static ThemedDialog createErrorDialog(Activity activity, String title, String message) {
        return createErrorDialog(activity, title, message, null, null);
    }

    @CheckResult
    public static ThemedDialog createErrorDialog(Activity activity, String title, String message,
                                                 ThemedClickListener clickListener, @Nullable Integer errorCode) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setHeaderDrawable(getDrawable(ContextHelper.getModuleContext(activity), "error_header"))
                .setExtension(
                        new BasicMessage()
                                .isButtonError(true)
                                .setMessage(message)
                                .setClickListener(clickListener)
                                .setErrorCode(errorCode)
                );
    }

    @CheckResult
    public static ThemedDialog createErrorDialog(Activity activity, String title, String message, Integer errorCode) {
        return createErrorDialog(activity, title, message, null, errorCode);
    }

    @CheckResult
    public static ThemedDialog createErrorDialog(Activity activity, String title, String message,
                                                 ThemedClickListener clickListener) {
        return createErrorDialog(activity, title, message, clickListener, null);
    }

    @CheckResult
    public static ThemedDialog createBasicMessage(Activity activity, String title, String message) {
        return createBasicMessage(activity, title, message, null);
    }

    @CheckResult
    public static ThemedDialog createBasicMessage(Activity activity, String title, String message, ThemedClickListener clickListener) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(
                        new BasicMessage()
                                .setMessage(message)
                                .setClickListener(clickListener)
                );
    }

    @CheckResult
    public static ThemedDialog createOptions(Activity activity, String title,
                                             OptionsButtonData... buttonDatas) {
        Options options = new Options();
        for (OptionsButtonData buttonData : buttonDatas)
            options.addOption(buttonData);

        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(options);
    }

    @CheckResult
    public static ThemedDialog createConfirmation(Activity activity, String title, String message, ThemedClickListener yesClickListener) {
        return createConfirmation(activity, title, message, yesClickListener, null);
    }

    @CheckResult
    public static ThemedDialog createConfirmation(Activity activity, String title, String message,
                                                  ThemedClickListener yesClickListener, @Nullable ThemedClickListener noClickListener) {
        return new ThemedDialog(activity)
                .setTitle(title)
                .setExtension(
                        new Confirmation()
                                .setMessage(message)
                                .setYesClickListener(yesClickListener)
                                .setNoClickListener(noClickListener)
                );
    }
}
