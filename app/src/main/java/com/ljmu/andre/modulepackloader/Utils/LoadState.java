package com.ljmu.andre.modulepackloader.Utils;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 27.12.2018 - Time 17:06.
 */

public abstract class LoadState {
    private final String name;
    private State state;

    LoadState(String name) {
        this.name = name;
        setState(State.UNLOADED);
    }

    /**
     * ===========================================================================
     * Provide a breakdown of the LoadState that can be displayed to the user
     * to explain the reasoning.
     * ===========================================================================
     */
    public abstract String getBasicBreakdown();

    public abstract boolean hasFailed();

    // ===========================================================================

    public String getName() {
        return name;
    }

    public LoadState.State getState() {
        return state;
    }

    public LoadState setState(State state) {
        this.state = state;
        return this;
    }

    public enum State {
        /**
         * ===========================================================================
         * Default state, should indicate no action has been performed yet.
         * ===========================================================================
         */
        UNLOADED("Unloaded"),

        /**
         * ===========================================================================
         * Passed loading successfully with no error. It can be assumed that the
         * linked object is ready to be used.
         * ===========================================================================
         */
        SUCCESS("Pass"),

        /**
         * ===========================================================================
         * An error occurred during the loading phase.
         * ===========================================================================
         */
        FAILED("Fail"),

        /**
         * ===========================================================================
         * Not Implemented.
         * The initial concept was to allow for further flexibility by defining when
         * an object had failed to initialise.
         * ===========================================================================
         */
        FAIL_INIT("Fail Init"),

        /**
         * ===========================================================================
         * Some issues occurred during the loading phase however the object should
         * still be usable.
         * <p>
         * An example of this in use is during the hook resolution phase
         *
         * @link HookResolver#loadHooks(ClassLoader, Activity)
         * when the hook resolver can't find a hooks reference
         * ===========================================================================
         */
        ISSUES("Issues"),

        /**
         * ===========================================================================
         * The object was flagged to skip its loading phase.
         * ===========================================================================
         */
        SKIPPED("Skipped"),

        /**
         * ===========================================================================
         * An 'outlier' state to allow for an activity that needs to be differentiated
         * from the other states.
         * E.g. displaying a custom message to the user upon a specific event.
         * ===========================================================================
         */
        CUSTOM("Custom");

        private final String display;

        State(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
