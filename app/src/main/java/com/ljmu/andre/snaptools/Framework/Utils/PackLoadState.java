package com.ljmu.andre.snaptools.Framework.Utils;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;


public class PackLoadState extends LoadState implements StatefulListable<ModuleLoadState> {
    private Map<String, ModuleLoadState> moduleLoadStates = new LinkedHashMap<>();
    private int failedModules;
    private int successfulModules;

    public PackLoadState(String name) {
        super(name);
    }

    public Map<String, ModuleLoadState> getModuleLoadStates() {
        return moduleLoadStates;
    }

    public PackLoadState setModuleLoadStates(Map<String, ModuleLoadState> moduleLoadStates) {
        this.moduleLoadStates = moduleLoadStates;
        refreshPackLoadState();
        return this;
    }

    /**
     * ===========================================================================
     * Refresh the load state of the object to ensure that it accurately reflects
     * the states of the child ModuleLoadStates.
     * This should be performed whenever an item in {@link this#moduleLoadStates}
     * is added, removed, or altered.
     * ===========================================================================
     */
    public PackLoadState refreshPackLoadState() {
        int moduleIssues = 0;
        failedModules = 0;
        successfulModules = 0;

        for (ModuleLoadState loadState : moduleLoadStates.values()) {
            if (loadState.hasFailed())
                failedModules++;
            else if (loadState.getState() == State.CUSTOM)
                moduleIssues++;
            else
                successfulModules++;
        }

        // If there were any failures in the children, the pack has failed ===========
        if (failedModules > 0)
            setState(State.FAILED);
        else if (moduleIssues > 0)
            setState(State.ISSUES);
        else
            setState(State.SUCCESS);

        return this;

    }

    public PackLoadState addModuleLoadState(ModuleLoadState loadState) {
        moduleLoadStates.put(loadState.getName(), loadState);

        refreshPackLoadState();

        return this;
    }

    public PackLoadState removeModuleLoadState(ModuleLoadState loadState) {
        moduleLoadStates.remove(loadState.getName());

        refreshPackLoadState();

        return this;
    }

    public void fail() {
        setState(State.FAILED);
    }

    @Override
    public List<ModuleLoadState> getChildren() {
        return new ArrayList<>(moduleLoadStates.values());
    }

    @Override
    public void updateHeaderText(View container, TextView headerHolder) {
        headerHolder.setText(getName());
    }

    @Override
    public void updateMessageText(View container, TextView messageHolder, ModuleLoadState child) {
        messageHolder.setText(child.getName());
    }

    @Override
    public void updateHeaderStateHolder(TextView stateHolder) {
        stateHolder.setText(getBasicBreakdown());

        if (hasFailed()) {
            stateHolder.setBackgroundResource(R.color.error);
            int color = ContextHelper.getModuleResources(stateHolder.getContext()).getColor(R.color.textPrimary);
            stateHolder.setTextColor(color);
        } else {
            stateHolder.setBackgroundResource(R.color.success);
            int color = ContextHelper.getModuleResources(stateHolder.getContext()).getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        }
    }

    /**
     * ===========================================================================
     * A breakdown of the LoadState that can be displayed to the user
     * to explain the reasoning.
     * ===========================================================================
     */
    @Override
    public String getBasicBreakdown() {
        if (failedModules > 0)
            return String.format("[%s/%s]", failedModules, moduleLoadStates.size());

        return getState().getDisplay();
    }

    @Override
    public boolean hasFailed() {
        return getState() == State.FAILED || getState() == State.ISSUES || failedModules > 0;
    }

    @Override
    public void updateMessageStateHolder(TextView stateHolder, ModuleLoadState child) {
        if (child.getState() == State.CUSTOM || !hasFailed()) {
            stateHolder.setVisibility(GONE);
            return;
        }

        stateHolder.setText(child.getBasicBreakdown());

        Resources moduleResources = ContextHelper.getModuleResources(stateHolder.getContext());

        if (child.hasFailed()) {
            stateHolder.setBackgroundResource(R.color.error);
            int color = moduleResources.getColor(R.color.textPrimary);
            stateHolder.setTextColor(color);
        } else if (child.getState() == State.SKIPPED) {
            stateHolder.setBackgroundResource(R.color.primary);
            int color = moduleResources.getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        } else {
            stateHolder.setBackgroundResource(R.color.success);
            int color = moduleResources.getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        }
    }
}
