package com.ljmu.andre.snaptools.UIComponents;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MenuItem;

import com.ljmu.andre.snaptools.Fragments.AboutUsFragment;
import com.ljmu.andre.snaptools.Fragments.FAQFragment;
import com.ljmu.andre.snaptools.Fragments.FeaturesFragment;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Fragments.HomeFragment;
import com.ljmu.andre.snaptools.Fragments.LegalFragment;
import com.ljmu.andre.snaptools.Fragments.PackManagerFragment;
import com.ljmu.andre.snaptools.Fragments.SettingsFragment;
import com.ljmu.andre.snaptools.Fragments.ShopFragment;
import com.ljmu.andre.snaptools.Fragments.SupportFragment;
import com.ljmu.andre.snaptools.Utils.Assert;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CustomNavigation extends NavigationView implements NavigationView.OnNavigationItemSelectedListener {
    private MenuItem selectedItem;
    private FragmentHelper activeFragment;
    private NavigationFragmentListener callback;

    private SparseArray<FragmentHelper> navigationFragments = new SparseArray<>();

    public CustomNavigation(Context context) {
        super(context);
    }

    public CustomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Activity activity) {
        callback = (NavigationFragmentListener) activity;
        Timber.d("Wrapper Init");
        if (getMenu().size() > 0) {
            selectItem(0);
        }

        this.setNavigationItemSelectedListener(this);

        addFragment(new HomeFragment());
        addFragment(new PackManagerFragment());
        addFragment(new SettingsFragment());
        addFragment(new SupportFragment());
        addFragment(new FAQFragment());
        addFragment(new AboutUsFragment());
        addFragment(new ShopFragment());
        addFragment(new FeaturesFragment());
        addFragment(new LegalFragment());

        FragmentHelper homeFragment = getFragment(HomeFragment.MENU_ID);

        activeFragment = homeFragment;
        callback.fragmentSelected(homeFragment);
    }

    public void selectItem(int index) {
        selectItem(getMenu().getItem(index));
    }

    public void addFragment(FragmentHelper fragment) {
        if (fragment != null && fragment.getMenuId() != null)
            navigationFragments.put(fragment.getMenuId(), fragment);
    }

    public <T extends FragmentHelper> T getFragment(int menuId) {
        return Assert.notNull("Fragment not found: " + menuId, (T) navigationFragments.get(menuId));
    }

    public void selectItem(MenuItem item) {
        item.setChecked(true);

        if (selectedItem != null)
            selectedItem.setChecked(false);

        selectedItem = item;

        Timber.d("Clicked item %s", item.getItemId());
    }

    public void removeFragment(int id) {
        navigationFragments.remove(id);
    }

    public <T extends FragmentHelper> T getActiveFragment() {
        return (T) activeFragment;
    }

    public boolean onNavigationItemSelected(@IdRes int menuId) {
        MenuItem selectedItem = getMenu().findItem(menuId);

        if (selectedItem == null) {
            Timber.w("Selected null menu with id %s", menuId);
            return false;
        }

        return onNavigationItemSelected(selectedItem);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentHelper selectedFragment = navigationFragments.get(item.getItemId());
        Timber.d("Frags: " + navigationFragments.toString());
        Timber.d("SelectedFrag: " + selectedFragment);
        if (selectedFragment == null)
            return false;

        if (selectedFragment == activeFragment)
            return true;

        selectItem(item);
        activeFragment = selectedFragment;
        callback.fragmentSelected(selectedFragment);

        return true;
    }

    public interface NavigationFragmentListener {
        void fragmentSelected(FragmentHelper fragment);
    }
}
