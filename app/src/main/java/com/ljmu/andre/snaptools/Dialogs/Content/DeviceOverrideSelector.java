package com.ljmu.andre.snaptools.Dialogs.Content;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedDialogExtension;
import com.ljmu.andre.snaptools.Networking.Packets.DevicePacket;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSplitter;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getStyle;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DeviceOverrideSelector implements ThemedDialogExtension {
    private Context moduleContext;
    private Callable<DevicePacket> callable;
    private DevicePacket[] devices;
    private LinearLayout deviceContainer;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        moduleContext = ContextHelper.getModuleContext(parent.getContext());
        inflater.inflate(getLayout(moduleContext, "dialog_device_override"), content, true);

        deviceContainer = getView(content, "device_container");

        for (DevicePacket device : devices)
            deviceContainer.addView(getDeviceRow(device));

        Button confirmSelection = getView(content, "btn_done");
        confirmSelection.setOnClickListener(v -> themedDialog.dismiss());
    }

    private LinearLayout getDeviceRow(DevicePacket device) {
        // ===========================================================================

        LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        containerParams.setMargins(5, 5, 5, 5);
        LinearLayout deviceRowContainer = new LinearLayout(moduleContext);
        deviceRowContainer.setOrientation(LinearLayout.HORIZONTAL);
        deviceRowContainer.setLayoutParams(
                containerParams
        );

        TextView txtDeviceName = new TextView(moduleContext);
        txtDeviceName.setTextAppearance(moduleContext, R.style.HeaderText);
        txtDeviceName.setText(device.device_name);
        txtDeviceName.setPadding(10, 0, 10, 0);
        txtDeviceName.setGravity(Gravity.CENTER_VERTICAL);
        txtDeviceName.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );
        deviceRowContainer.addView(
                txtDeviceName
        );

        // ===========================================================================

        LayoutParams buttonParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER_VERTICAL;

        Button logoutButton = new Button(moduleContext);
        logoutButton.setText("Logout");
        logoutButton.setBackgroundResource(getDrawable(moduleContext, "error_button"));
        logoutButton.setTextAppearance(moduleContext, getStyle(moduleContext, "ErrorButton"));
        logoutButton.setGravity(Gravity.CENTER_VERTICAL);
        logoutButton.setLayoutParams(buttonParams);
        logoutButton.setOnClickListener(v -> {
            Timber.d("Clicked logout button for: " + device.device_name);
            callable.call(device);
        });
        deviceRowContainer.addView(
                logoutButton
        );

        // The container that holds the entire device row contents ===================
        LinearLayout completeRowContainer = new LinearLayout(moduleContext);
        completeRowContainer.setOrientation(LinearLayout.VERTICAL);
        int id = Math.abs(device.device_id.hashCode());
        Timber.d("Setting device %s to id %s", device.device_name, id);
        completeRowContainer.setId(id);
        completeRowContainer.addView(deviceRowContainer);
        completeRowContainer.addView(
                getSplitter(moduleContext, getColor(moduleContext, "primaryWashed"))
        );

        return completeRowContainer;

        // ===========================================================================
    }

    public void removeDevice(String deviceId) {
        int id = Math.abs(deviceId.hashCode());
        Timber.d("removing device with id %s", id);
        View deviceView = deviceContainer.findViewById(id);

        if (deviceView == null) {
            Timber.w("Couldn't find device to remove: " + deviceId);
            return;
        }

        AnimationUtils.collapse(deviceView, false, 4);
    }

    public DeviceOverrideSelector setCallable(Callable<DevicePacket> callable) {
        this.callable = callable;
        return this;
    }

    public DeviceOverrideSelector setDevices(DevicePacket[] devices) {
        this.devices = devices;
        return this;
    }
}
