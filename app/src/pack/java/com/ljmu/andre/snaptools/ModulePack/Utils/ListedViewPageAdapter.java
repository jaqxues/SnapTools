package com.ljmu.andre.snaptools.ModulePack.Utils;

import androidx.viewpager.widget.PagerAdapter;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ListedViewPageAdapter extends PagerAdapter implements ObjectAtPositionInterface {
    private List<Pair<String, View>> viewList;
    private int mCurrentPosition = -1; // Keep track of the current position

    public ListedViewPageAdapter(List<Pair<String, View>> viewList) {
        this.viewList = viewList;
    }

    public ListedViewPageAdapter(Pair<String, View>... viewArray) {
        this.viewList = Arrays.asList(viewArray);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Timber.d("Position: " + position);
        View currentItem = bindView(container, position);
        //bindView(container, position + 1);

        Timber.d("Container count: " + container.getChildCount());
        return currentItem;
    }

    private View bindView(ViewGroup container, int position) {
        if (position < 0 || position >= viewList.size())
            return null;

        View itemView = viewList.get(position).second;

        ViewParent parent = itemView.getParent();

        if (parent != null && parent != container)
            ((ViewGroup) itemView.getParent()).removeView(itemView);

        if (parent == null) {
            container.addView(itemView);
            Timber.d("Bound new view: " + position);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!(container instanceof WrapContentViewPager)) {
            return; // Do nothing if it's not a compatible ViewPager
        }

        View view = (View) object;

        if (position != mCurrentPosition) { // If the position has changed, tell WrappingViewPager
            WrapContentViewPager pager = (WrapContentViewPager) container;
            if (view != null) {
                mCurrentPosition = position;
                pager.onPageChanged(view);
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < 0 || position >= viewList.size())
            return null;

        return viewList.get(position).first;
    }

    @Override
    public Object getObjectAtPosition(int position) {
        if (position < 0 || position > viewList.size() - 1)
            return null;

        Pair<String, View> titleViewPair = viewList.get(position);

        if (titleViewPair == null)
            return null;

        return titleViewPair.second;
    }
}
