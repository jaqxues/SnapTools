package com.ljmu.andre.snaptools.Dialogs.Content;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Networking.Helpers.GetPackHistory;
import com.ljmu.andre.snaptools.Networking.Packets.PackHistoryObject;
import com.ljmu.andre.snaptools.Networking.WebResponse.ServerListResultListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Collections;
import java.util.List;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.dp;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackHistory implements ThemedDialog.ThemedDialogExtension {
    private Activity activity;
    private String scVersion;
    private String packType;
    private String flavour;
    private LinearLayout packContainer;
    private AVLoadingIndicatorView loadingIndicator;
    private Callable<PackHistoryObject> selectedPackCallable;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        inflater.inflate(R.layout.dialog_pack_history, content, true);

        packContainer = (LinearLayout) parent.findViewById(R.id.pack_container);
        loadingIndicator = (AVLoadingIndicatorView) parent.findViewById(R.id.avi);

        String packName = packType + "Pack v" + scVersion;
        TextView txtMessage = (TextView) parent.findViewById(R.id.txt_message);
        txtMessage.setText(
                "Showing pack version history for: " + packName
                        + ".\nTap a pack to download and install."
        );

        Button confirmSelection = (Button) parent.findViewById(R.id.btn_okay);
        confirmSelection.setOnClickListener(v -> themedDialog.dismiss());

        generatePackHistory();
    }

    private void generatePackHistory() {
        GetPackHistory.getPacksFromServer(
                activity,
                scVersion,
                packType,
                flavour,
                new ServerListResultListener<PackHistoryObject>() {
                    @Override
                    public void success(List<PackHistoryObject> list) {
                        loadingIndicator.smoothToHide();
                        populatePackContainer(list);
                    }

                    @Override
                    public void error(String message, Throwable t, int responseCode) {
                        loadingIndicator.smoothToHide();
                        populatePackContainer(Collections.emptyList());

                        DialogFactory.createErrorDialog(
                                activity,
                                "Error Getting Packs",
                                message,
                                responseCode
                        ).show();
                    }
                }
        );
    }

    private void populatePackContainer(List<PackHistoryObject> historyObjects) {
        if (historyObjects == null || historyObjects.isEmpty()) {
            SafeToast.show(activity, "No pack history found", Toast.LENGTH_LONG, true);
            return;
        }

        Collections.sort(historyObjects);

        packContainer.removeAllViews();

        for (PackHistoryObject historyObject : historyObjects)
            packContainer.addView(buildPackView(historyObject));
    }

    private LinearLayout buildPackView(PackHistoryObject historyObject) {
        int sideMargin = dp(20, activity);
        LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(sideMargin, 0, sideMargin, 0);
        LinearLayout rowContainer = new LinearLayout(activity);
        rowContainer.setOrientation(LinearLayout.VERTICAL);
        rowContainer.setLayoutParams(rowParams);
        rowContainer.setBackgroundResource(FrameworkViewFactory.getSelectableBackgroundId(activity));

        int versionMargin = dp(10, activity);
        LayoutParams versionParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        versionParams.setMargins(versionMargin, versionMargin, 0, versionMargin);

        TextView txtPackVersion = new TextView(activity);
        txtPackVersion.setLayoutParams(versionParams);
        txtPackVersion.setText("Pack Version: " + historyObject.packVersion);
        txtPackVersion.setTextColor(ContextCompat.getColor(activity, R.color.primaryLight));
        txtPackVersion.setTypeface(null, Typeface.BOLD);
        txtPackVersion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        rowContainer.addView(txtPackVersion);
        rowContainer.addView(
                FrameworkViewFactory.getSplitter(
                        activity,
                        R.color.primaryWashed
                )
        );

        rowContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPackCallable.call(historyObject);
            }
        });

        return rowContainer;
    }

    public PackHistory setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public PackHistory setScVersion(String scVersion) {
        this.scVersion = scVersion;
        return this;
    }

    public PackHistory setPackType(String packType) {
        this.packType = packType;
        return this;
    }

    public PackHistory setFlavour(String flavour) {
        this.flavour = flavour;
        return this;
    }

    public PackHistory setSelectedPackCallable(Callable<PackHistoryObject> selectedPackCallable) {
        this.selectedPackCallable = selectedPackCallable;
        return this;
    }
}
