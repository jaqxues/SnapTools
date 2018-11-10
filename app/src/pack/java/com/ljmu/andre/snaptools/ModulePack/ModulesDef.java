package com.ljmu.andre.snaptools.ModulePack;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.ModulePack.ModulesDef.Modules;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unused")
class ModulesDef extends ConstantDefiner<Modules> {
    public static final ModulesDef INST = new ModulesDef();

    public static final Modules HOOK_RESOLVER = new Modules(
            1,
            "Hook Resolver",
            HookResolver.class,
            false
    );

    // ===========================================================================

    public static final Modules SAVING = new Modules(
            2,
            "Saving",
            Saving.class,
            "Responsible for handling the saving of media\nSupported Media:\n\tStory/Received Videos and Images\n\tChat Images and Videos"
    );

    // ===========================================================================

    public static final Modules LENS_COLLECTOR = new Modules(
            3,
            "Lens Collector",
            LensCollector.class,
            "Collects your daily Lenses so that you can choose to enable/disable whichever lenses you desire"
    );

    // ===========================================================================

    public static final Modules CHAT_MANAGER = new Modules(
            4,
            "Chat Manager",
            ChatSaving.class,
            "Collects all chat messages and allows you to re-read them within the SnapTools app. Also provides the option to automatically save messages within Snapchat"
    );

    // ===========================================================================

    public static final Modules MISC_CHANGES = new Modules(
            5,
            "Misc Changes",
            MiscChanges.class,
            "A collection of small changes that provide small benefits:\n\t- Enable torch/flash on front facing camera\n\t- Multiline Captions"
    );

    // ===========================================================================

    public static final Modules REMOVE_SNAP_TIMER = new Modules(
            6,
            "Remove Snap Timer",
            RemoveSnapTimer.class,
            "Removes the timer on all snaps. Used mainly when unlimited viewing is enabled"
    );

    // ===========================================================================

    public static final Modules SCREENSHOT_BYPASS = new Modules(
            7,
            "Screenshot Bypass",
            ScreenshotBypass.class,
            "Block screenshot notifications being sent to the recipient"
    );

    // ===========================================================================

    public static final Modules STORY_BLOCKER = new Modules(
            8,
            "Story Blocker",
            StoryBlocker.class,
            "Blocks advertising within Snapchat, and allows the ability to block specific users' stories"
    );

    // ===========================================================================

    public static final Modules UNLIMITED_VIEWING = new Modules(
            9,
            "Unlimited Viewing",
            UnlimitedViewing.class,
            "Removes the time limit when viewing snaps"
    );

    // ===========================================================================

    public static final Modules SHARING = new Modules(
            10,
            "Sharing",
            Sharing.class,
            "Convert images shared to Snapchat into snaps that can be added to your story as opposed to chat media"
    );

    // ===========================================================================

//    public static final Modules CUSTOM_FILTERS = new Modules(
//            11,
//            "Custom Filters",
//            CustomFilters.class,
//            "Allows for custom images and a Now Playing filter to be loaded alongside Snapchat filters"
//    );

    // ===========================================================================

//    public static final Modules STEALTH_VIEWING = new Modules(
//            12,
//            "Stealth Viewing",
//            StealthViewing.class,
//            "Allows you to view chats and snaps without marking them as viewed"
//    );

    // ===========================================================================

    public static final Modules ACCOUNT_MANAGER = new Modules(
            13,
            "Account Manager",
            AccountManager.class,
            "Allows for safely and securely swapping between Snapchat accounts, along with other account management features"
    );

    // ===========================================================================

    public static final Modules FORCED_HOOKS = new Modules(
            14,
            "Forced Hooks",
            ForcedHooks.class,
            false
    );

    // ===========================================================================

    public static class Modules extends Constant {
        private final Class<? extends Module> moduleClass;
        private final boolean canBeDisabled;
        private String description;

        Modules(int index, String moduleName, Class<? extends Module> moduleClass, String description) {
            this(index, moduleName, moduleClass, true);
            this.description = description;
        }

        Modules(int index, String moduleName, Class<? extends Module> moduleClass, boolean canBeDisabled) {
            super(index, moduleName);
            this.moduleClass = moduleClass;
            this.canBeDisabled = canBeDisabled;
        }

        public Class<? extends Module> getModuleClass() {
            return moduleClass;
        }

        public String getClassName() {
            return moduleClass.getSimpleName();
        }

        public String getModuleName() {
            return getName();
        }

        public String getDescription() {
            return description;
        }

        public boolean canBeDisabled() {
            return canBeDisabled;
        }
    }
}
