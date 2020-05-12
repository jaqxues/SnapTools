package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getButtonHeightAspect;
import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getButtonLocation;
import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getButtonOpacity;
import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getButtonWidth;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.dp;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SavingButton extends ImageButton {
    @Nullable
    private SnapType snapType;

    private SavingButton(Context context) {
        super(context);
    }

    public SavingButton(Context context, @Nullable SnapType snapType) {
        super(context);

        this.snapType = snapType;

        int drawableId = ResourceUtils.getDrawable(getModuleContext(context), "save_256");
        Drawable saveDrawable = getModuleResources(context).getDrawable(drawableId);
        setBackground(saveDrawable);

        setupLayoutParams();

        int padding = dp(5, getContext());
        setPadding(padding, padding, padding, padding);

        refresh();
    }

    private void setupLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
        ButtonLocation buttonLocation = getButtonLocation(snapType);
        for (int rule : buttonLocation.getRules())
            layoutParams.addRule(rule);
        setLayoutParams(layoutParams);
    }

    public void refresh() {
        Pair<Float, Float> btnSize = getButtonSize(getContext(), getButtonWidth(snapType), getButtonHeightAspect(snapType));

        ViewGroup.LayoutParams btnParams = getLayoutParams();
        btnParams.width = btnSize.first.intValue();
        btnParams.height = btnSize.second.intValue();
        setLayoutParams(btnParams);

        int opacityPref = getButtonOpacity(snapType);
        setAlpha(((float) opacityPref) / 100f);
    }

    public static Pair<Float, Float> getButtonSize(Context context, int widthPref, int heightAspectPref) {
        int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        Timber.d("Display: " + displayWidth);

        Timber.d("PrefSize: " + widthPref);
        float percentSize = ((float) widthPref) / 100f;
        Timber.d("PercentSize: " + percentSize);
        float btnWidth = ((float) displayWidth) * percentSize;

        float btnHeight = btnWidth * (heightAspectPref / 100f);
        return Pair.create(btnWidth, btnHeight);
    }

    public void setSnapType(@NonNull SnapType snapType) {
        if (this.snapType == snapType)
            return;

        this.snapType = snapType;
        setupLayoutParams();
        refresh();
    }

    public enum ButtonLocation {
        TOP_LEFT("Top Left", RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_LEFT),
        TOP_RIGHT("Top Right", RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.ALIGN_PARENT_RIGHT),
        BOTTOM_LEFT("Bottom Left", RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_LEFT),
        BOTTOM_RIGHT("Bottom Right", RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_RIGHT);

        private String displayText;
        private int[] rules;

        ButtonLocation(String displayText, int... rules) {
            this.displayText = displayText;
            this.rules = rules;
        }

        public String getDisplayText() {
            return displayText;
        }

        public int[] getRules() {
            return rules;
        }

        @Nullable
        public static ButtonLocation getFromDisplayText(String displayText) {
            for (ButtonLocation location : values()) {
                if (location.displayText.equals(displayText))
                    return location;
            }

            return null;
        }
    }
}
