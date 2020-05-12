package com.ljmu.andre.snaptools.EventBus.Events;

import androidx.annotation.IdRes;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TutorialFinishedEvent {
    @IdRes
    private int menuId;
    private boolean fullTutorial;

    public TutorialFinishedEvent(int menuId) {
        this.menuId = menuId;
    }

    public TutorialFinishedEvent(int menuId, boolean triggerNext) {
        this.menuId = menuId;
        this.fullTutorial = triggerNext;
    }

    public int getMenuId() {
        return menuId;
    }

    public boolean isFullTutorial() {
        return fullTutorial;
    }
}
