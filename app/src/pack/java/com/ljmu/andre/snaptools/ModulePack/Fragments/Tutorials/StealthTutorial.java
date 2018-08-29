package com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StealthTutorial {
    public static ImmutableList<TutorialDetail> getTutorials() {
        return new ImmutableList.Builder<TutorialDetail>()

                /**
                 * ===========================================================================
                 * Chat stealth tutorial
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setDslViewId("chat_stealth_container")
                                .setTitle("Chat Stealth")
                                .setMessage(
                                        "The Chat Stealth system uses 'global stealth' whereby when stealth mode is enabled it will be enabled for all chats"
                                                + "\nThis is because upon entering a chat it will be marked as read so stealth must be enabled beforehand"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("switch_show_chat_stealth")
                                .setTitle("Show Chat Stealth Button")
                                .setMessage("Toggle whether or not to display the chat stealth button within your active chat windows")
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("switch_show_chat_stealth_message")
                                .setTitle("Show Chat Stealth Message")
                                .setMessage("Toggle whether or not to display toast messages when enabling/disabling stealth mode (Useful for invisible stealth buttons)")
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("chat_stealth_position_container")
                                .setTitle("Chat Stealth Button Position")
                                .setMessage("Set whether to align the stealth button to the left or right of the chat window")
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("seek_chat_stealth_opacity")
                                .setTitle("Chat Stealth Button Opacity")
                                .setMessage("Set the opacity of the chat stealth button")
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("seek_chat_stealth_padding")
                                .setTitle("Chat Stealth Button Padding")
                                .setMessage("Set the padding of the chat stealth button to reduce its size but maintain clickable area")
                )
                .add(
                        new TutorialDetail()
                                .setDslViewId("chat_stealth_button_preview")
                                .setTitle("Chat Stealth Button Preview")
                                .setMessage(
                                        "A preview of what the chat stealth button might look like within Snapchat"
                                                + "\nUses the old Snapchat UI"
                                )
                )

                // ===========================================================================

                /**
                 * ===========================================================================
                 * Snap stealth tutorial
                 * ===========================================================================
                 */

                .add(
                        new TutorialDetail()
                                .setDslViewId("snap_stealth_container")
                                .setTitle("Snap Stealth")
                                .setMessage(
                                        "The Snap Stealth system uses a different method to the Chat Stealth's global settings"
                                                + "\nYou can assign a 'default state' for the stealth when viewing snaps (Within the Snapchat Profile page)"
                                                + "\nAs Snaps are only marked as viewed when you've finished viewing it you can decide while viewing it whether or not to mark it as viewed once closed"
                                )
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("switch_show_snap_stealth")
                                .setTitle("Show Snap Stealth Button")
                                .setMessage("Toggle whether or not to display the snap stealth button within your active snap")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("switch_show_snap_stealth_message")
                                .setTitle("Show Snap Stealth Message")
                                .setMessage("Toggle whether or not to display toast messages when enabling/disabling stealth mode (Useful for invisible stealth buttons)")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("switch_mark_story_viewed")
                                .setTitle("Mark stories as viewed client side")
                                .setMessage("When Stealth is enabled for a story, should the story be marked as viewed for you but not the other person.")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("seek_stealth_snap_size")
                                .setTitle("Snap Stealth Button Size")
                                .setMessage("Set the size (In Pixels) of the snap stealth button")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("seek_stealth_snap_opacity")
                                .setTitle("Snap Stealth Button Opacity")
                                .setMessage("Set the opacity of the snap stealth button")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("seek_stealth_snap_margin")
                                .setTitle("Snap Stealth Button Margin")
                                .setMessage("Set the margin between the stealth button and the edge of the screen (In Pixels)")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("button_stealth_snap_location")
                                .setTitle("Snap Stealth Button Location")
                                .setMessage("This feature is still yet to be implemented, the plan is to display a dialog with the ability to tap the screen where you want the buttons")
                )

                .add(
                        new TutorialDetail()
                                .setDslViewId("active_snap_stealth_image")
                                .setTitle("Snap Stealth Button Preview")
                                .setMessage("A live preview of the stealth button that will be overlaid on snaps")
                )

                // ===========================================================================
                .build();
    }
}
