package com.ljmu.andre.snaptools.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.ljmu.andre.snaptools.R;

import java.util.concurrent.Callable;

import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FrameworkViewFactory {
    public static void setContainerPadding(View v) {
        setContainerPadding(v, 16);
    }

    public static void setContainerPadding(View v, int top) {
        Context context = v.getContext();
        int topPadding = dp(top, context);
        int bottomPadding = dp(16, context);
        int sidePadding = dp(16, context);
        v.setPadding(sidePadding, topPadding, sidePadding, bottomPadding);
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

    public static void addRelativeParamRule(@NonNull View target, int verb) {
        addRelativeParamRule(target, verb, RelativeLayout.TRUE);
    }

    public static void addRelativeParamRule(@NonNull View target, int verb, int subject) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) target.getLayoutParams();
        params.addRule(verb, subject);
        target.setLayoutParams(params);
    }

    public static Spanned getSpannedHtml(String text) {
        text = text.replaceAll("(\r\n|\n)", "<br>");

        if (SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);

        return Html.fromHtml(text);
    }

    public static View getSplitter(Context context) {
        return getSplitter(context, R.color.primaryLight);
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

    @DrawableRes
    public static int getSelectableBackgroundId(Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return backgroundResource;
    }

    public static <T extends View> T detach(T v) {
        ViewParent viewParent = v.getParent();

        if (viewParent == null || !(viewParent instanceof ViewGroup)) {
            return v;
        }

        ((ViewGroup) v.getParent()).removeView(v);

        return v;
    }

    public static void removeClipping(ViewGroup v) {
        v.setClipChildren(false);
        v.setClipToPadding(false);
    }

	/*public static View scrollTo(View v) {
		if(v.getParent() == null) {
			Timber.w("Tried to scroll to view without parent");
			return v;
		}

		v.getParent().requestChildFocus(v, v);
		return v;
	}*/

    /**
     * Used to scroll to the given view.
     *
     * @param view View to which we need to scroll.
     */
    public static View scrollTo(View view) {
        if (view == null)
            return null;

        ScrollView scrollView = null;

        ViewParent parent = view.getParent();

        while (true) {
            if (parent == null)
                break;

            if (parent instanceof ScrollView) {
                scrollView = (ScrollView) parent;
                break;
            }

            parent = parent.getParent();
        }

        if (scrollView == null) {
            Timber.e("No scrollview parent found");
            return view;
        }

        return scrollTo(scrollView, view);
    }

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view             View to which we need to scroll.
     */
    public static View scrollTo(ScrollView scrollViewParent, View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        Timber.d("ScrollViewHeight: " + scrollViewParent.getHeight());
        scrollViewParent.scrollTo(0, childOffset.y - 200);
        return view;
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumalated Offset.
     */
    private static void getDeepChildOffset(ViewGroup mainParent, ViewParent parent, View child, Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.y += child.getTop();

        if (parentGroup.equals(mainParent))
            return;

        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
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
