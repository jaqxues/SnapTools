package com.ljmu.andre.snaptools.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.ljmu.andre.snaptools.Dialogs.Content.ModularDialogContainer;
import com.ljmu.andre.snaptools.R;

import java.util.ArrayList;
import java.util.List;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModularDialog extends AlertDialog {
    private Activity activity;
    private List<DialogComponent> componentList = new ArrayList<>();

    public ModularDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getWindow() != null) {
            getWindow().setWindowAnimations(R.style.DialogAnimation);
            getWindow().setBackgroundDrawableResource(R.color.backgroundPrimary);
        }

        ViewGroup container = (ViewGroup) new ModularDialogContainer().bind(activity, this);
        setContentView(container);

        LinearLayout componentContainer = getView(container, getIdFromString("ModularDialogContainer"));

        for (DialogComponent binder : componentList) {
            componentContainer.addView(
                    binder.bind(activity, this)
            );
        }
    }

    public ModularDialog addComponent(DialogComponent component) {
        componentList.add(component);
        return this;
    }
}
