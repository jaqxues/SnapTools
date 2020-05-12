package com.ljmu.andre.snaptools.Dialogs.Content;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import java.util.ArrayList;
import java.util.List;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Options implements ThemedDialog.ThemedDialogExtension {
    private ThemedClickListener okayListener;

    private List<OptionsButtonData> options = new ArrayList<>();

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context moduleContext = ContextHelper.getModuleContext(parent.getContext());
        LinearLayout container = (LinearLayout) inflater.inflate(getLayout(moduleContext, "dialog_options"), content, true);

        for (OptionsButtonData buttonData : options) {
            View child = inflater.inflate(getLayout(moduleContext, "item_option"), container, false);

            Button optionButton = getView(child, "btn_option");
            optionButton.setText(buttonData.message);

            if (buttonData.tag != null)
                optionButton.setTag(buttonData.tag);

            if (buttonData.clickListener != null) {
                buttonData.clickListener.setDialog(themedDialog);
                optionButton.setOnClickListener(buttonData.clickListener);
            }

            container.addView(child, 0);
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

        getView(container, "btn_okay").setOnClickListener(okayListener);
    }

    public Options addOption(String message, @Nullable ThemedClickListener clickListener) {
        return addOption(null, message, clickListener);
    }

    public Options addOption(@Nullable String tag, String message, @Nullable ThemedClickListener clickListener) {
        return addOption(new OptionsButtonData(tag, message, clickListener));
    }

    public Options addOption(OptionsButtonData buttonData) {
        options.add(buttonData);
        return this;
    }

    public static class OptionsButtonData {
        @Nullable
        private String tag;
        private String message;
        @Nullable
        private ThemedClickListener clickListener;

        public OptionsButtonData(@Nullable String tag, String message, @Nullable ThemedClickListener clickListener) {
            this.tag = tag;
            this.message = message;
            this.clickListener = clickListener;
        }

        public OptionsButtonData(String message, @Nullable ThemedClickListener clickListener) {
            this.message = message;
            this.clickListener = clickListener;
        }

        public OptionsButtonData(String message) {
            this.message = message;
        }
    }
}
