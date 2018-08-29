package com.ljmu.andre.snaptools.ModulePack;

import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.snaptools.Framework.Module;
import com.ljmu.andre.snaptools.ModulePack.ModulesDef.Modules;

import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class ModulesDef extends ConstantDefiner<Modules> {
    public static final ModulesDef INST = new ModulesDef();

    public static final Modules HOOK_RESOLVER = new Modules(
            1,
            /*Hook Resolver*/ decryptMsg(new byte[]{44, -40, -14, 86, 98, -48, 76, 104, 112, -18, -35, -32, 66, -107, 40, 121}),
            HookResolver.class,
            false
    );

    // ===========================================================================

    public static final Modules SAVING = new Modules(
            2,
            /*Saving*/ decryptMsg(new byte[]{112, 72, 58, -54, -68, -45, -98, 74, -25, -18, 52, 114, 81, -55, -96, -64}),
            Saving.class,
            /*Responsible for handling the saving of media*/ decryptMsg(new byte[]{-51, -54, -72, 22, 25, -5, -100, 115, 111, 91, 96, -25, -113, -8, 4, 98, 59, -116, 10, 53, 61, 50, 111, -46, -39, 62, -105, -119, -11, 28, -36, -118, -42, -114, 97, 44, 99, -12, -111, -36, 40, 39, 79, -18, -55, -62, -99, 91})
            + "\n"
            + /*Supported Media:*/ decryptMsg(new byte[]{-87, 40, -92, 105, -21, -24, 121, -99, -45, -13, -23, 81, 91, 24, 13, -69, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66})
            + "\n\t"
            + /*Story/Received Videos and Images*/ decryptMsg(new byte[]{-82, -97, -35, -50, -18, -68, -35, -20, -119, 31, -84, 79, -83, -64, 60, -17, 5, -20, 113, 62, 82, 37, 27, -80, 115, 59, -66, 120, 8, -39, -93, 27, 104, -50, 116, -85, 125, 59, 23, -57, 29, -117, 35, 62, -41, 17, -14, 66})
            + "\n\t"
            + /*Chat Images and Videos*/ decryptMsg(new byte[]{-88, -109, -32, -3, -68, 81, -43, -115, 44, 19, 56, -71, 119, 99, -87, -30, -50, 127, 52, -2, -97, 102, 20, -126, -26, -83, -5, -37, -89, 89, -116, 50})
    );

    // ===========================================================================

    public static final Modules LENS_COLLECTOR = new Modules(
            3,
            /*Lens Collector*/ decryptMsg(new byte[]{22, -121, -28, -62, 14, 103, -76, 55, -31, -23, 33, 102, 38, 64, -32, 90}),
            LensCollector.class,
            /*Collects your daily Lenses so that you can choose to enable/disable whichever lenses you desire*/ decryptMsg(new byte[]{76, -86, -28, -39, -31, -88, 81, 2, -79, 48, -56, -123, 80, 101, -60, -83, -85, -101, 76, -99, 1, 81, -42, 72, -119, 16, -36, -46, -45, -46, 18, -31, 116, 69, 21, 108, 86, -126, -89, -99, 12, -97, -59, -69, 50, -117, 122, 123, -70, -16, 12, 116, -31, -74, -114, 50, 119, -105, 40, -100, 86, -96, -126, -95, -64, 106, -16, -37, 121, 43, 22, -82, -117, -88, -97, 115, -80, 16, 57, -70, 110, -70, 7, 68, -84, -9, -100, 8, -42, 0, -6, 74, 61, -68, 39, 103})
    );

    // ===========================================================================

    public static final Modules CHAT_MANAGER = new Modules(
            4,
            /*Chat Manager*/ decryptMsg(new byte[]{-18, -110, 123, -107, 66, 102, -89, -49, -50, 46, 121, 47, 53, -13, -56, 111}),
            ChatSaving.class,
            /*Collects all chat messages and allows you to re-read them within the SnapTools app. Also provides the option to automatically save messages within Snapchat*/ decryptMsg(new byte[]{-15, 122, -52, 60, 7, 76, -63, 21, -88, 79, -5, -23, 97, -85, -74, 127, -113, -26, -63, -60, 5, -79, -114, 37, 16, -63, 49, -40, 58, 85, -92, 71, -35, -31, -89, -64, -7, -78, 8, 126, 48, -74, -36, 5, -98, 30, 112, -48, -121, 6, 51, -36, -33, -127, -89, -71, -101, -101, 96, -62, 125, 125, -55, 41, -120, 74, 60, -111, 72, 66, 124, -79, 68, 26, -96, 88, 99, -14, -15, -40, -7, 109, 75, -76, -103, 62, -22, 61, -21, -66, 41, -18, -18, 23, -109, 127, -26, -120, -65, 97, 86, 60, -50, 2, -42, 72, 42, 87, 18, -84, -3, 72, -12, -33, -65, -59, 55, 119, -14, 92, -15, 28, 70, -35, -76, -47, 99, 118, -120, 92, 85, -45, -95, 61, -65, -97, 12, 55, -69, 8, 105, -1, 111, 58, -89, -21, 61, 75, -37, 101, -30, -23, -53, -7, 75, 110, 13, 92, -9, -45})
    );

    // ===========================================================================

    public static final Modules MISC_CHANGES = new Modules(
            5,
            /*Misc Changes*/ decryptMsg(new byte[]{-107, -91, -53, -59, -70, -40, -71, -32, 113, 120, 77, 33, 5, 83, -69, 0}),
            MiscChanges.class,
            /*A collection of small changes that provide small benefits:*/ decryptMsg(new byte[]{-78, -91, 30, -111, -80, 105, -37, 100, 17, 74, 16, 29, -83, 32, -124, -57, -47, 26, -25, 13, 87, -127, 109, 16, 29, 52, 8, -19, -30, 50, -114, -48, -105, -123, -69, 20, 80, -77, 96, -40, -31, -104, -48, 11, 37, -60, 45, -37, 35, -79, 37, 114, -38, -14, -79, 65, -26, 104, -31, 79, 91, 41, 121, -9})
            + "\n\t"
            + /*- Enable torch/flash on front facing camera*/ decryptMsg(new byte[]{-30, -105, 36, -77, -1, -44, -125, -103, 50, -71, -90, 56, 24, -23, -93, 108, 18, -119, -46, -83, -26, 29, 44, 31, -33, -93, 90, -119, 89, 93, 49, 36, 91, 81, 39, -80, 113, 76, -37, 35, -26, 42, -32, -119, -106, -57, 126, -120, -43, -49, 106, -64, -28, 93, -68, -2, 44, -75, 47, 53, -125, -62, 54, 13, 29, -111, 48, -59, 58, -115, -97, -97, -47, -34, -104, -70, 51, -69, 44, 37})
            + "\n\t"
            + /*- Fix multilining on Captions*/ decryptMsg(new byte[]{62, 72, 62, 79, -91, 118, 37, -82, 12, -26, 109, -107, -44, -60, -19, -116, 12, -27, -10, 64, -64, 113, 68, -51, -106, 74, 84, -18, 88, -115, 5, -123})
    );

    // ===========================================================================

    public static final Modules REMOVE_SNAP_TIMER = new Modules(
            6,
            /*Remove Snap Timer*/ decryptMsg(new byte[]{3, -37, 117, 97, 101, 78, -117, 112, -49, 30, 22, -22, -63, -7, -115, -79, 61, -115, 40, -37, -126, 96, -115, -114, -18, -95, -4, 94, 3, 61, 102, -45}),
            RemoveSnapTimer.class,
            /*Removes the timer on all snaps. Used mainly when unlimited viewing is enabled*/ decryptMsg(new byte[]{-100, 108, 30, 22, 42, 9, 10, 98, 83, 50, -49, 21, -74, -39, 74, 104, -83, 5, 121, 98, -12, 20, 89, -36, -98, 43, -125, -59, -10, -43, -20, 97, -58, 110, 89, 10, 114, 127, -50, -102, -87, -95, 52, -100, -114, -121, 90, 4, -47, 57, -18, -94, -41, -81, 27, 56, 43, -42, 22, -98, -42, 31, 71, 115, -73, 12, 103, -103, 118, 104, 116, -12, -16, 87, 8, 84, 98, 61, 122, 9})
    );

    // ===========================================================================

    public static final Modules SCREENSHOT_BYPASS = new Modules(
            7,
            /*Screenshot Bypass*/ decryptMsg(new byte[]{122, -17, -5, -111, 57, 41, -57, -78, 112, 114, -102, -25, 10, -8, -51, 20, 124, -77, 53, -111, -6, -26, -91, 61, 59, 12, -89, 23, -9, -51, 109, -117}),
            ScreenshotBypass.class,
            /*Block screenshot notifications being sent to the recipient*/ decryptMsg(new byte[]{-117, 10, 22, 14, 62, -127, 90, 37, 114, 90, -21, 68, 69, 73, 121, 29, 117, 34, -93, 16, -60, -50, 46, -58, 123, 127, -121, 93, 102, 71, -15, 79, -29, 123, -95, 61, -124, 38, 2, 20, 17, 125, 30, 35, 101, 48, -47, 76, -64, 89, 100, -31, 89, -73, -8, -50, 20, 65, 86, -26, -75, -58, 51, 49})
    );

    // ===========================================================================

    public static final Modules STORY_BLOCKER = new Modules(
            8,
            /*Story Blocker*/ decryptMsg(new byte[]{-82, -120, -82, 88, 73, -69, -62, 76, 49, -101, 7, 123, -82, -109, 89, -85}),
            StoryBlocker.class,
            /*Blocks advertising within Snapchat, and allows the ability to block specific users' stories*/ decryptMsg(new byte[]{-111, -51, -2, 24, -46, -21, 105, -46, 76, -1, -91, -93, -19, 127, 21, -114, 18, -39, -77, 105, -76, -61, 120, 9, 37, 21, -98, -45, 115, 8, -40, -105, -5, 81, 30, 24, 8, 22, 84, -55, 12, 126, 16, -113, -74, 65, 122, 7, 74, -15, 32, 34, 27, -3, 49, -50, 64, -9, -123, 53, -105, 118, 118, 106, 116, -121, 21, -36, 100, -123, -23, -41, -127, 123, -93, 99, 38, -99, -2, -65, 50, 28, -102, -80, 27, 113, -117, -52, -55, 8, 81, -34, -31, -76, 12, -2})
    );

    // ===========================================================================

    public static final Modules UNLIMITED_VIEWING = new Modules(
            9,
            /*Unlimited Viewing*/ decryptMsg(new byte[]{-50, 4, 37, 123, 28, 27, 46, 8, -4, -76, -92, -48, -28, -52, 112, 72, 11, -5, 13, 20, 79, -116, -22, -94, 64, 44, 97, 93, -37, 39, 7, -10}),
            UnlimitedViewing.class,
            /*Removes the time limit when viewing snaps*/ decryptMsg(new byte[]{-100, 108, 30, 22, 42, 9, 10, 98, 83, 50, -49, 21, -74, -39, 74, 104, -61, -83, -89, 86, 97, 88, 62, -72, -127, -112, -111, -25, 61, -92, 121, 25, -1, 38, 42, -51, 16, 109, 70, -99, 24, 64, -39, -117, -122, -78, 66, -1})
    );

    // ===========================================================================

    public static final Modules SHARING = new Modules(
            10,
            /*Sharing*/ decryptMsg(new byte[]{51, 45, -52, -97, -111, 77, 43, 110, -86, 14, 34, -57, -26, -15, -105, 96}),
            Sharing.class,
            /*Convert images shared to Snapchat into snaps that can be added to your story as opposed to chat media*/ decryptMsg(new byte[]{95, -69, -117, 8, 119, -104, 90, -9, -2, 107, -54, 7, 70, -27, 98, -42, 28, 85, -65, 22, -102, 125, -91, 0, 117, -88, -63, 44, 0, -108, 49, 117, -67, -77, -23, 105, -53, 103, -15, 29, -5, 49, -75, 122, -84, -15, -126, -72, 86, 37, -103, -115, -33, 98, 32, 77, -63, -112, -48, -3, -2, 12, 88, 63, -89, -109, -27, -11, -107, 104, 73, 92, -17, 18, 51, -47, -94, 118, -41, 38, 49, 20, 7, -67, -123, 99, -94, 21, 84, 68, 68, -115, -13, 74, 73, 0, 80, -45, -63, -62, -14, -7, 34, 18, -19, -2, 116, 106, -16, -62, -1, 61})
    );

    // ===========================================================================

    public static final Modules CUSTOM_FILTERS = new Modules(
            11,
            /*Custom Filters*/ decryptMsg(new byte[]{65, 111, -25, -55, 0, 6, 124, 44, -59, -7, -49, 80, -2, 42, 88, 43}),
            CustomFilters.class,
            /*Allows for custom images and a Now Playing filter to be loaded alongside Snapchat filters*/ decryptMsg(new byte[]{64, -13, -91, -72, 79, -120, -113, -59, -50, 81, 45, 27, 14, -10, 37, -73, -17, -60, 70, -100, -20, -39, -57, -23, 74, 12, -123, 121, -5, 61, -101, 109, -97, -60, -61, -30, 11, -81, 78, 51, 100, -124, 30, -62, 29, -113, 78, -50, -64, -63, -116, -127, -26, -5, 127, 86, 100, 53, -59, -79, 115, -112, -103, -39, 95, -11, 123, -97, 126, -50, 92, -48, -78, -10, -33, -85, 68, -119, -127, -112, -61, -103, 110, -85, -92, 79, 123, -68, 51, -23, 40, 32, -11, -104, 116, 64})
    );

    // ===========================================================================

    public static final Modules STEALTH_VIEWING = new Modules(
            12,
            /*Stealth Viewing*/ decryptMsg(new byte[]{-73, 104, 62, 98, 101, -113, 64, -58, -79, 38, 5, 15, 98, -110, -128, 65}),
            StealthViewing.class,
            /*Allows you to view chats and snaps without marking them as viewed*/ decryptMsg(new byte[]{83, 2, -102, 119, -105, -66, -15, -35, -36, -22, 82, -10, -119, -31, 50, 90, 94, 72, 111, 104, -25, 94, -12, 122, -61, 51, -55, -121, -11, -121, 24, 57, -117, 59, 52, 87, 75, -18, 48, -35, 43, 103, -62, 50, -60, 24, 78, -71, -19, -49, 61, -1, 74, 92, 63, 60, 16, 11, 75, -74, -23, 0, 42, 97, -105, 124, 6, -98, 51, -78, -54, -47, -29, -81, 28, 106, -101, 96, -35, 32})
    );

    // ===========================================================================

    public static final Modules ACCOUNT_MANAGER = new Modules(
            13,
            /*Account Manager*/ decryptMsg(new byte[]{19, -123, -112, -17, 87, 121, -35, -65, 38, 116, -55, 108, 3, 49, -22, -52}),
            AccountManager.class,
            /*Allows for safely and securely swapping between Snapchat accounts, along with other account management features*/ decryptMsg(new byte[]{-115, -52, 0, -71, -32, -113, 85, 18, -1, 43, -46, -74, 17, -44, 97, -1, -16, -78, -70, 24, -88, 62, -88, 102, 27, 29, -34, -42, -125, -35, -47, 69, 36, -29, 111, -8, -50, 9, 55, 69, -83, -24, -21, -36, -105, -10, 102, -38, 111, 3, 53, 73, -41, -23, -1, 69, -14, -61, -46, -45, 56, -74, 0, 9, 112, 21, 26, -10, 103, -92, -84, 89, 94, -69, 96, 67, 102, 72, -104, 40, -77, -57, -118, -96, -46, 33, 86, -26, -49, 111, 71, 9, 11, -111, 41, 58, -21, -19, 63, -51, -95, 43, 30, 32, -8, 21, -86, 65, 20, 54, 121, -96})
    );

    // ===========================================================================

    public static final Modules FORCED_HOOKS = new Modules(
            14,
            /*Forced Hooks*/ decryptMsg(new byte[]{93, 70, 77, -18, -74, -24, -4, 114, -25, 87, -95, 26, 45, -125, -25, -2}),
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
