package com.ljmu.andre.snaptools.EventBus.Events;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ReqLoadFragmentEvent {
    private int fragmentId;
    private FragmentHelper fragmentHelper;
    private String name;

    public ReqLoadFragmentEvent(int fragmentId, String name) {
        this.fragmentId = fragmentId;
        this.name = name;
    }

    public ReqLoadFragmentEvent(FragmentHelper fragmentHelper) {
        this.fragmentHelper = fragmentHelper;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public FragmentHelper getFragmentHelper() {
        return fragmentHelper;
    }

    public String getName() {
        return name;
    }
}
