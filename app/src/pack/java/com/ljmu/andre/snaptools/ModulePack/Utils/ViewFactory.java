package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.LinearLayoutCompat.OrientationMode;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ReSpinner;
import com.ljmu.andre.snaptools.R;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getStyle;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ViewFactory {
    public static GradientDrawable getBorderedDrawable(@ColorInt Integer bgColor, @ColorInt Integer strokeColor, int strokeWidth) {
        return getBorderedDrawable(bgColor, strokeColor, strokeWidth, 0);
    }

    public static GradientDrawable getBorderedDrawable(@ColorInt Integer bgColor, @ColorInt Integer strokeColor, int strokeWidth,
                                                       int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        if (bgColor != null)
            drawable.setColor(bgColor);

        if (strokeColor != null)
            drawable.setStroke(strokeWidth, strokeColor);

        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    public static GradientDrawable getBorderedDrawable(@ColorInt Integer bgColor, @ColorInt Integer strokeColor, int strokeWidth,
                                                       float[] cornerRadii) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        if (bgColor != null)
            drawable.setColor(bgColor);

        if (strokeColor != null)
            drawable.setStroke(strokeWidth, strokeColor);

        drawable.setCornerRadii(cornerRadii);
        return drawable;
    }

    public static StateListDrawable getSelectableBorderedDrawable(@ColorInt Integer bgColor, @ColorInt Integer selectedBgColor,
                                                                  float[] cornerRadii) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(200);
        res.setEnterFadeDuration(200);
        res.addState(new int[]{android.R.attr.state_pressed}, getBorderedDrawable(selectedBgColor, null, 0, cornerRadii));
        res.addState(new int[]{}, getBorderedDrawable(bgColor, null, 0, cornerRadii));

        return res;
    }

    public static SwitchCompat getSwitch(Context context, String text, boolean checked,
                                         OnCheckedChangeListener changeListener) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        int sideMargin = dp(10, context);
        layoutParams.setMargins(sideMargin, 0, sideMargin, 0);

        return getSwitch(
                context,
                text,
                checked,
                changeListener,
                layoutParams
        );
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    public static int dp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) Math.ceil(px * metrics.density);
    }

    public static SwitchCompat getSwitch(Context context, String text, boolean checked,
                                         OnCheckedChangeListener changeListener,
                                         ViewGroup.LayoutParams layoutParams) {

        SwitchCompat switchCompat = new SwitchCompat(context);
        switchCompat.setSwitchMinWidth(dp(75, context));
        switchCompat.setLayoutParams(layoutParams);
        switchCompat.setText(text);
        switchCompat.setChecked(checked);
        switchCompat.setTextAppearance(context, R.style.SwitchMessageText);
        switchCompat.setSwitchTextAppearance(context, R.style.SwitchTextAppearance);
        switchCompat.setOnCheckedChangeListener(changeListener);

        return switchCompat;
    }

    public static ListView getLinearListView(Context context) {
        ListView listView = new ListView(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1
        );

        params.setMargins(0, dp(5, context), 0, dp(5, context));
        listView.setLayoutParams(params);
        return listView;
    }

    /**
     * This method converts device specific pixels to scaled density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent sp equivalent to px value
     */
    public static int sp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) Math.ceil(px * metrics.scaledDensity);
    }

    public static RecyclerView getRecyclerView(Context context) {
        RecyclerView recyclerView = new RecyclerView(context);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, dp(5, context), 0, dp(5, context));
        recyclerView.setLayoutParams(params);

        return recyclerView;
    }

    public static LinearLayout getLabelledSpinner(Context context, String text, Integer selection, List<String> list,
                                                  OnItemSelectedListener selectedListener) {
        return getLabelledSpinner(context, null, text, selection, list, selectedListener);
    }

    public static LinearLayout getLabelledSpinner(Context context, String spinnerId, String text, Integer selection, List<String> list,
                                                  OnItemSelectedListener selectedListener) {
        if (spinnerId == null)
            spinnerId = "spinner";


        Spinner spinner = getBasicSpinner(context, 0, 0, 0, 0);
        spinner.setId(getIdFromString(spinnerId));

        LinearLayout labelledSpinnerLayout = makeLabelled(
                text + ": ",
                spinner,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);

        if (selection != null) {
            spinner.setSelection(selection, false);
            Timber.d("Labelled Spinner Selection: " + selection);
        }

        spinner.setOnItemSelectedListener(selectedListener);

        return labelledSpinnerLayout;
    }

    @Deprecated
    public static Spinner getBasicSpinner(Context context, int marLeft, int marTop, int marRight, int marBottom) {
        Spinner spinner = new ReSpinner(context);

        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );

        params.setMargins(
                dp(marLeft, context),
                dp(marTop, context),
                dp(marRight, context),
                dp(marBottom, context)
        );

        spinner.setLayoutParams(params);
        return spinner;
    }

    public static LinearLayout makeLabelled(String text, View v, @Nullable LayoutParams headerParams) {
        return makeLabelled(text, v, LinearLayout.HORIZONTAL, headerParams);
    }

    public static LinearLayout makeLabelled(String text, View v, @OrientationMode int orientation,
                                            @Nullable LayoutParams headerParams) {
        return makeLabelled(
                text,
                v,
                orientation,
                headerParams,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );
    }

    public static LinearLayout makeLabelled(String text, View v, @OrientationMode int orientation,
                                            @Nullable LayoutParams headerParams, @Nullable LayoutParams containerParams) {
        return makeLabelled(new TextView(v.getContext()), text, v, orientation, headerParams, containerParams);
    }

    public static LinearLayout makeLabelled(TextView inputLabelView, String text, View v, @OrientationMode int orientation,
                                            @Nullable LayoutParams headerParams, @Nullable LayoutParams containerParams) {
        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(orientation);
        if (containerParams != null)
            layout.setLayoutParams(containerParams);

        if (headerParams != null)
            inputLabelView.setLayoutParams(headerParams);

        inputLabelView.setTextAppearance(v.getContext(), getStyle(v.getContext(), "DefaultText"));
        inputLabelView.setGravity(Gravity.CENTER_VERTICAL);
        inputLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        inputLabelView.setPadding(dp(10, v.getContext()), 0, 0, 0);

        if (SDK_INT >= Build.VERSION_CODES.N)
            inputLabelView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        else
            inputLabelView.setText(Html.fromHtml(text));

        layout.addView(inputLabelView);
        layout.addView(v);
        return layout;
    }

    public static View assignMargins(View v, Rect margins) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        LayoutParams linearParams = new LinearLayout.LayoutParams(layoutParams);

        int left = dp(margins.left, v.getContext());
        int top = dp(margins.top, v.getContext());
        int right = dp(margins.right, v.getContext());
        int bottom = dp(margins.bottom, v.getContext());
        linearParams.setMargins(left, top, right, bottom);
        return v;
    }

    public static LinearLayout getHeaderSpinner(Context context, @Nullable Integer selection, List<String> spinnerItems,
                                                @Nullable OnItemSelectedListener selectedListener) {
        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        return getHeaderSpinner(context, "HeaderSpinner", selection, spinnerItems, params, selectedListener);
    }

    public static LinearLayout getHeaderSpinner(Context context, String id, @Nullable Integer selection, List<String> spinnerItems,
                                                LayoutParams layoutParams,
                                                @Nullable OnItemSelectedListener selectedListener) {
        LayoutParams containerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerParams.gravity = Gravity.CENTER;
        LinearLayout spinnerContainer = new LinearLayout(context);
        spinnerContainer.setOrientation(LinearLayout.HORIZONTAL);
        spinnerContainer.setLayoutParams(containerParams);
        spinnerContainer.setGravity(Gravity.CENTER);

        //noinspection ConstantConditions
        if (BuildConfig.VERSION_CODE >= 51)
            spinnerContainer.setBackgroundResource(getDrawable(context, "neutral_button"));

        Spinner spinner = new ReSpinner(context);
        spinner.setId(getIdFromString(id));
        spinner.setLayoutParams(layoutParams);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                getLayout(context, "item_header_spinner"), spinnerItems);
        spinner.setAdapter(adapter);

        if (selection != null)
            spinner.setSelection(selection, false);

        if (selectedListener != null)
            spinner.setOnItemSelectedListener(selectedListener);

        spinnerContainer.addView(spinner);
        return spinnerContainer;
    }

    public static LinearLayout getLabelledSeekbar(Context context, String text, int max, int progress, boolean bindOutput,
                                                  OnSeekBarResult seekListener) {
        return getLabelledSeekbar(context, text, max, 0, progress, bindOutput, seekListener, null);
    }

    public static LinearLayout getLabelledSeekbar(Context context, String text, int max, int min, int progress,
                                                  boolean bindOutput, OnSeekBarResult seekListener, OnSeekBarProgress seekBarProgress) {
        String initialText = text + ": ";

        Pair<String, TextView> bindableTextPair = null;

        TextView customLabel = new TextView(context);

        if (bindOutput) {
            initialText = String.format(text, progress);
            customLabel.setText(initialText);
            bindableTextPair = new Pair<>(text, customLabel);
        }

        return makeLabelled(
                customLabel,
                initialText + ": ",
                getBasicSeekbar(context, max, min, progress, seekListener, seekBarProgress, bindableTextPair),
                LinearLayout.VERTICAL,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1)
        );
    }

    public static SeekBar getBasicSeekbar(Context context, int max, int min, int progress,
                                          @Nullable OnSeekBarResult seekListener, @Nullable OnSeekBarProgress progressListener,
                                          @Nullable Pair<String, TextView> bindableTextPair) {
        SeekBar seekBar = new SeekBar(context);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
        );
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        seekBar.setLayoutParams(layoutParams);

        if (progress < min)
            progress = min;

        seekBar.setMax(max - min);
        seekBar.setProgress(progress - min);
        seekBar.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress += min;

                        if (bindableTextPair != null) {
                            String textFormat = bindableTextPair.first;
                            bindableTextPair.second.setText(String.format(textFormat, progress));
                        }

                        if (progressListener != null)
                            progressListener.onSeekProgress(seekBar, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //seekListener.onStartTrackingTouch(seekBar);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (seekListener != null)
                            seekListener.onSeekResult(seekBar, seekBar.getProgress() + min);
                    }
                }
        );

        return seekBar;
    }

    public static LinearLayout getLabelledSeekbar(Context context, String text, int max, int min, int progress,
                                                  boolean bindOutput, OnSeekBarResult seekListener) {
        return getLabelledSeekbar(context, text, max, min, progress, bindOutput, seekListener, null);
    }

    public static SeekBar getBasicSeekbar(Context context, int max, int min, int progress,
                                          OnSeekBarResult seekListener,
                                          @Nullable Pair<String, TextView> bindableTextPair) {
        return getBasicSeekbar(context, max, min, progress, seekListener, null, bindableTextPair);
    }

    public static TextView getDefaultTextView(Context context, String text, @Nullable Float size, @StyleRes int appearanceRes,
                                              int gravity) {
        return getDefaultTextView(context, text, size, appearanceRes, gravity, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public static TextView getDefaultTextView(Context context, String text, @Nullable Float size, @StyleRes int appearanceRes,
                                              int gravity, @Nullable LayoutParams layoutParams) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(
                layoutParams
        );

        textView.setTextAppearance(context, appearanceRes);
        textView.setGravity(gravity);

        if (size != null)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        if (SDK_INT >= Build.VERSION_CODES.N)
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        else
            textView.setText(Html.fromHtml(text));

        return textView;
    }

    public static Spinner getBasicSpinner(Context context, List<String> list) {
        Spinner spinner = getBasicSpinner(context, 0, 5, 0, 10);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);

        return spinner;
    }

    public static TextView getHeaderLabelNoSplitter(Context context, String text, int gravity) {

        return getHeaderLabelNoSplitter(context, text, gravity,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
    }

    public static TextView getHeaderLabelNoSplitter(Context context, String text, int gravity,
                                                    @Nullable ViewGroup.LayoutParams layoutParams) {
        TextView label = new TextView(context);
        label.setTextAppearance(context, R.style.HeaderText);
        label.setText(text);
        label.setPadding(dp(10, context), 0, dp(10, context), 0);
        label.setGravity(gravity);

        if (layoutParams != null)
            label.setLayoutParams(layoutParams);

        return label;
    }

    public static LinearLayout getHeaderLabel(Context context, String text) {
        return getHeaderLabel(context, text, View.TEXT_ALIGNMENT_CENTER);
    }

    public static LinearLayout getHeaderLabel(Context context, String text, int gravity) {
        return getHeaderLabel(context, text, gravity, getColor(context, "primaryLight"));
    }

    public static LinearLayout getHeaderLabel(Context context, String text, int gravity,
                                              @ColorRes int colorRes) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        TextView label = new TextView(context);
        label.setTextAppearance(context, getStyle(context, "HeaderText"));
        label.setText(text);
        label.setPadding(dp(10, context), 0, dp(10, context), 0);
        label.setGravity(gravity);
        label.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );
        container.addView(label);

        container.addView(getSplitter(context, colorRes));

        return container;
    }

    public static View getSplitter(Context context, @ColorRes int color) {
        return getSplitter(context, color, 0);
    }

    public static View getSplitter(Context context, @ColorRes int color, int bottomMargin) {
        return getSplitter(context, color, bottomMargin, 0);
    }

    public static View getSplitter(Context context, @ColorRes int color, int bottomMargin, int topMargin) {
        LinearLayout.LayoutParams splitterLayoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dp(1, context));
        splitterLayoutParams.bottomMargin = dp(bottomMargin, context);
        splitterLayoutParams.topMargin = dp(topMargin, context);

        View splitter = new View(context);
        splitter.setBackgroundResource(color);
        splitter.setLayoutParams(splitterLayoutParams);

        return splitter;
    }

    public static View getSplitter(Context context) {
        return getSplitter(context, getColor(context, "primaryLight"));
    }

    public static EditText getEditText(Context context, String text) {
        return getEditText(context, text, 30, 1, null);
    }

    public static EditText getEditText(Context context, String text, int marBottom, int weight,
                                       @Nullable EditTextListener textListener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        params.setMargins(0, 0, 0, dp(marBottom, context));

        return getEditText(context, text, marBottom, weight, textListener, params);
    }

    public static EditText getEditText(Context context, String text, int marBottom, int weight,
                                       @Nullable EditTextListener textListener,
                                       @NonNull ViewGroup.LayoutParams layoutParams) {
        EditText editText = new EditText(context);
        editText.setText(text);
        editText.setSingleLine();

        if (textListener != null) {
            textListener.setEditText(editText);
            editText.addTextChangedListener(textListener);
        }

        editText.setLayoutParams(layoutParams);
        return editText;
    }

    public static LinearLayout getLabelledEditText(Context context, String label, String defaultText,
                                                   @Nullable EditTextListener textListener) {
        TableRow layout = new TableRow(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        label += ": ";

        TextView labelView = new TextView(context);
        labelView.setTextAppearance(context, getStyle(context, "DefaultText"));
        labelView.setGravity(Gravity.CENTER_VERTICAL);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        labelView.setPadding(dp(10, context), 0, 0, 0);
        labelView.setLayoutParams(
                new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        1
                )
        );

        if (SDK_INT >= Build.VERSION_CODES.N)
            labelView.setText(Html.fromHtml(label, Html.FROM_HTML_MODE_LEGACY));
        else
            labelView.setText(Html.fromHtml(label));


        layout.addView(labelView);
        layout.addView(getEditText(
                context,
                defaultText,
                5,
                1,
                textListener,
                new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        3
                )
        ));
        return layout;
    }

    public static Button getButton(Context context, String text) {
        return getButton(context, text, null);
    }

    public static Button getButton(Context context, String text, @Nullable OnClickListener clickListener) {
        int margin = ViewFactory.dp(5, context);
        LinearLayout.LayoutParams buttonLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(margin, margin, margin, margin);

        return getButton(context, text, clickListener, buttonLayoutParams);
    }

    public static Button getButton(Context context, String text, @Nullable OnClickListener clickListener,
                                   @Nullable ViewGroup.LayoutParams layoutParams) {
        Button button = new Button(context);
        button.setText(text);

        if (clickListener != null)
            button.setOnClickListener(clickListener);

        if (layoutParams != null)
            button.setLayoutParams(layoutParams);

        return button;
    }

    public static Spanned getSpannedHtml(String text) {
        text = text.replace("\n", "<br>");

        if (SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);

        return Html.fromHtml(text);
    }

    public static <T extends View> T detach(T v) {
        ViewParent viewParent = v.getParent();

        if (viewParent == null || !(viewParent instanceof ViewGroup)) {
            return v;
        }

        ((ViewGroup) v.getParent()).removeView(v);

        return v;
    }

    public static View focus(View v) {
        if (v.getParent() == null) {
            Timber.w("Tried to scroll to view without parent");
            v.requestFocus();
            return v;
        }

        v.getParent().requestChildFocus(v, v);
        return v;
    }

    public static <T> void assignItemChangedProvider(Spinner spinner, OnItemChangedProvider<T> itemChangedProvider) {
        spinner.setOnTouchListener((v, event) -> {
            if (!itemChangedProvider.isActive) {
                Timber.d("Marking Spinner as ACTIVE");
                itemChangedProvider.isActive = true;
            }

            return false;
        });

        spinner.setOnItemSelectedListener(itemChangedProvider);
    }

    public interface OnSeekBarResult {
        void onSeekResult(SeekBar seekBar, int progress);
    }

    public interface OnSeekBarProgress {
        void onSeekProgress(SeekBar seekBar, int progress);
    }

    public interface OnItemChangedListener<T> {
        void onItemChanged(T newItem, AdapterView<?> adapterView, int position);
    }

    public static class OnItemChangedProvider<T> implements OnItemSelectedListener {
        private OnItemChangedListener<T> itemChangedListener;
        @Nullable
        private Callable<T> currentItemProvider;
        private boolean isActive;

        public OnItemChangedProvider(OnItemChangedListener<T> itemChangedListener) {
            this.itemChangedListener = itemChangedListener;
        }

        public OnItemChangedProvider(OnItemChangedListener<T> itemChangedListener, @Nullable Callable<T> currentItemProvider) {
            this.itemChangedListener = itemChangedListener;
            try {
                this.currentItemProvider = currentItemProvider;
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!isActive) {
                Timber.d("Spinner not active before setting selected item");
                return;
            }

            //noinspection unchecked
            T selectedItem = (T) parent.getItemAtPosition(position);

            if (currentItemProvider != null) {
                try {
                    T currentItem = currentItemProvider.call();
                    if (selectedItem.equals(currentItem))
                        return;
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            itemChangedListener.onItemChanged(selectedItem, parent, position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public abstract static class EditTextListener implements TextWatcher {
        @Nullable
        private EditText boundEditText;

        private void setEditText(@Nullable EditText boundEditText) {
            this.boundEditText = boundEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            textChanged(boundEditText, s);
        }

        protected abstract void textChanged(@Nullable EditText source, Editable editable);
    }
}
