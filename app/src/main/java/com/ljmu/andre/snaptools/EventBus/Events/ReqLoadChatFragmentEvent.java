package com.ljmu.andre.snaptools.EventBus.Events;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ReqLoadChatFragmentEvent {
    private FragmentHelper chatFragment;

    public ReqLoadChatFragmentEvent(FragmentHelper chatFragment) {
        this.chatFragment = chatFragment;
    }

    public FragmentHelper getChatFragment() {
        return chatFragment;
    }
}
