package com.ljmu.andre.snaptools.Framework.MetaData;

import android.content.res.Resources;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class PackUpdateMetaData extends PackMetaData {
    private String localVersion;
    private boolean hasFailed;
    private boolean hasUpdate;

    public PackUpdateMetaData(String name) {
        setName(name);
    }

    public abstract void result();

    public PackUpdateMetaData setHasFailed(boolean hasFailed) {
        this.hasFailed = hasFailed;
        return this;
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public PackUpdateMetaData setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
        return this;
    }

    @Override
    public void updateHeaderStateHolder(TextView stateHolder) {
        Resources moduleResource = getModuleResources(stateHolder.getContext());

        if (hasFailed) {
            stateHolder.setText("Fail");
            stateHolder.setBackgroundResource(R.color.error);
            int color = moduleResource.getColor(R.color.textPrimary);
            stateHolder.setTextColor(color);
        } else if (hasUpdate) {
            stateHolder.setText("Update");
            stateHolder.setBackgroundResource(R.color.success);
            int color = moduleResource.getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        } else {
            stateHolder.setText("Latest");
            stateHolder.setBackgroundResource(R.color.primary);
            int color = moduleResource.getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        }
    }

    @Override
    public void updateMessageStateHolder(TextView stateHolder, String child) {
        stateHolder.setVisibility(GONE);
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public List<String> getChildren() {
        if (hasFailed())
            return Collections.singletonList("Connection Fail");

        List<String> messages = new ArrayList<>();

        if (isDeveloper())
            messages.add("Developer Pack");

        messages.add("Pack Type: " + getType());
        messages.add("Current Version: " + getLocalVersion());

        if (hasUpdate)
            messages.add("Latest Version: " + getPackVersion());
        else
            messages.add("Latest Version Installed");

        return messages;
    }

    public boolean hasFailed() {
        return hasFailed;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public PackUpdateMetaData setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
        return this;
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {

    }
}
