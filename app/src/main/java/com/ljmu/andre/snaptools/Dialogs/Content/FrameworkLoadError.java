package com.ljmu.andre.snaptools.Dialogs.Content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.Utils.LoadState.State;
import com.ljmu.andre.snaptools.Framework.Utils.ModuleLoadState;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableListHelper.ItemLongClickListener;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FrameworkLoadError implements ThemedDialog.ThemedDialogExtension, ItemLongClickListener<PackLoadState, ModuleLoadState> {
    private List<PackLoadState> packLoadStates = new ArrayList<>();
    private StatefulEListAdapter<PackLoadState, ModuleLoadState> adapter;
    private ThemedDialog parentDialog;

    public FrameworkLoadError(List<PackLoadState> packLoadStates) {
        this.packLoadStates = packLoadStates;
    }

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context moduleContext = ContextHelper.getModuleContext(parent.getContext());
        parentDialog = themedDialog;

        inflater.inflate(getLayout(moduleContext, "dialog_framework_fail"), content, true);

        ExpandableListView list_packs = getView(content, "list");

        adapter = new StatefulEListAdapter<>(list_packs, packLoadStates, this);
        list_packs.setAdapter(adapter);

        Button btn_okay = getView(content, "btn_okay");

        btn_okay.setOnClickListener(v -> themedDialog.dismiss());
    }

    public FrameworkLoadError updateLoadStates(List<PackLoadState> loadStates) {
        packLoadStates.clear();
        packLoadStates.addAll(loadStates);

        refresh();

        return this;
    }

    private void refresh() {
        if (packLoadStates.isEmpty()) {
            parentDialog.dismiss();
            return;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildLongClick(ModuleLoadState child) {
        if (child.getState() == State.CUSTOM)
            return;

        new ThemedDialog(ContextHelper.getActivity())
                .setTitle("Confirmation")
                .setExtension(
                        new Confirmation()
                                .setMessage("Disable Module?")
                                .setYesClickListener(new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        PreferenceHelpers.addToCollection(DISABLED_MODULES, child.getName());

                                        themedDialog.dismiss();
                                        Timber.d("YES CLICKED");
                                    }
                                }))
                .show();
    }

    @Override
    public void onGroupLongClick(PackLoadState loadState) {
        new ThemedDialog(ContextHelper.getActivity())
                .setTitle("Module Pack Options")
                .setExtension(
                        new Options()
                                .addOption("Delete Pack", new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        showDeleteConfirmation(loadState, themedDialog);
                                    }
                                })
                                .addOption("Disable Pack", new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        PreferenceHelpers.removeFromCollection(SELECTED_PACKS, loadState.getName());
                                        removeState(loadState);
                                        themedDialog.dismiss();
                                    }
                                })
                )
                .show();
    }

    private void removeState(PackLoadState loadState) {
        packLoadStates.remove(loadState);
        refresh();
    }

    private void showDeleteConfirmation(PackLoadState loadState, ThemedDialog coreDialog) {
        new ThemedDialog(ContextHelper.getActivity())
                .setTitle("Confirmation")
                .setExtension(
                        new Confirmation()
                                .setMessage("Are you sure you want to delete Module Pack: " + loadState.getName())
                                .setYesClickListener(new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        FrameworkManager.deleteModPack(
                                                loadState.getName(),
                                                coreDialog.getOwnerActivity()
                                        );

                                        removeState(loadState);
                                        themedDialog.dismiss();
                                        coreDialog.dismiss();
                                    }
                                })
                )
                .show();
    }
}
