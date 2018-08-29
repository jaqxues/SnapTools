package com.ljmu.andre.snaptools.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.RedactedClasses.Answers;
import com.ljmu.andre.snaptools.RedactedClasses.CustomEvent;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import timber.log.Timber;

import java.lang.ref.WeakReference;

import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.*;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ThemedDialog extends AlertDialog {
    private WeakReference<Activity> activityWeakReference;
    private Integer headerId = -1;
    private String title;
    private ThemedDialogExtension extension;
    private View container;

    public ThemedDialog(Activity context) {
        super(context);
        activityWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Window window = getWindow();
            if (window != null) {
                window.setWindowAnimations(getStyle(getModuleContext(getContext()), "DialogAnimation"));
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            LayoutInflater inflater = LayoutInflater.from(
                    getModuleContext(getContext())
            );

            container = inflater.inflate(
                    getLayout(getModuleContext(getContext()),
                            "dialog_themed"),
                    null,
                    false
            );

            setContentView(container);

            if (headerId != -1) {
                ImageView img_header = getView(container, "img_header");
                img_header.setBackgroundResource(headerId);
            }

            TextView txt_title = getView(container, "txt_title");
            txt_title.setText(title);

            LinearLayout content = getView(container, "content");
            extension.onCreate(inflater, container, content, this);
        } catch (Throwable t) {
            Timber.e(t);
            handleMethodError();
        }
    }

    @Override
    public void show() {
        if (getContext() instanceof Activity && ((Activity) getContext()).isFinishing()) {
            Timber.w(new Exception("Tried to show dialog on Activity that is finishing"));
            return;
        }

        try {
            super.show();
        } catch (Throwable t) {
            Timber.e(t, "Failed to display ThemedDialog");
            handleMethodError();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Throwable t) {
            Timber.e(t);
            handleMethodError();
        }
    }

    private void handleMethodError() {
        try {
            if (isShowing())
                dismiss();
        } catch (Throwable e) {
            Timber.e(e);
        }

        try {
            if (activityWeakReference != null) {
                Activity activity = activityWeakReference.get();

                if (!activity.isFinishing()) {
                    SafeToast.show(activity, "Problems loading dialog... Issue will be reported", Toast.LENGTH_LONG, true);

                    Answers.safeLogEvent(
                            new CustomEvent("FailedDialogWarning")
                    );
                }
            }
        } catch (Throwable t2) {
            Timber.e(t2);
        }
    }

    public View getContainer() {
        return container;
    }

    public ThemedDialog setHeaderDrawable(Integer headerId) {
        this.headerId = headerId;
        return this;
    }

    public ThemedDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public <T extends ThemedDialogExtension> T getExtension() {
        return (T) extension;
    }

    public ThemedDialog setExtension(ThemedDialogExtension extension) {
        this.extension = extension;
        return this;
    }

    public ThemedDialog setDismissable(boolean state) {
        setCancelable(state);
        return this;
    }

    public interface ThemedDialogExtension {
        void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog);
    }

    public abstract static class ThemedClickListener implements View.OnClickListener {
        private ThemedDialog dialog;
        private Preference preference;
        private Object updateValue;

        /**
         * ===========================================================================
         * A constructor to update a preference with a specified value on click
         * ===========================================================================
         */
        public ThemedClickListener(Preference preference, Object updateValue) {
            this.preference = preference;
            this.updateValue = updateValue;
        }

        public ThemedClickListener() {
        }

        public void setDialog(ThemedDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            Assert.notNull("ThemedDialog not assigned!", dialog);

            if (preference != null)
                putPref(preference, updateValue);

            try {
                clicked(dialog);
            } catch (Throwable t) {
                Timber.e(t, "Error during Themed Click");
            }
        }

        public abstract void clicked(ThemedDialog themedDialog);
    }

    public static class ThemedClickWrapper extends ThemedClickListener {
        private OnThemedClick themedClick;

        @RequiresFramework(78)
        public ThemedClickWrapper(Preference preference, Object updateValue, OnThemedClick themedClick) {
            super(preference, updateValue);
            this.themedClick = themedClick;
        }

        @RequiresFramework(78)
        public ThemedClickWrapper(OnThemedClick themedClick) {
            super();
            this.themedClick = themedClick;
        }

        @RequiresFramework(78)
        @Override
        public void clicked(ThemedDialog themedDialog) {
            themedClick.onClick(themedDialog);
        }

        @RequiresFramework(78)
        public interface OnThemedClick {
            @RequiresFramework(78)
            void onClick(ThemedDialog themedDialog);
        }
    }
}
