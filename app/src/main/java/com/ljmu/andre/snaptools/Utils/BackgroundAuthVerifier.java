package com.ljmu.andre.snaptools.Utils;


import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * ===========================================================================
 * Create a relatively hidden class to perform a check that the authentication
 * system has been triggered within 90 seconds when a premium pack is in use.
 * <p>
 * This system is designed with obfuscation in mind so there are references
 * to variables in unassuming classes (E.g. AnimationUtils).
 * ===========================================================================
 */
public class BackgroundAuthVerifier {
    public static void spoolVerifierThread() {
        try {
            //noinspection Convert2Lambda
            new Thread(
                    // Do not use lambda, the compiler seems to discard this function when using it
                    new Runnable() {
                        @Override
                        public void run() {

                            try {
                                TimeUnit.SECONDS.sleep(90);

                                /**
                                 * ===========================================================================
                                 * Check whether a premium pack has loaded and verify that the mandatory
                                 * authentication has been performed.
                                 * ===========================================================================
                                 */
                                if (AnimationUtils.shouldTriggerAuthVerifier)
                                    VerifyPackAuth.verifyAuthHasCompleted();
                            } catch (Throwable e) {
                                Timber.e(e);
                            }
                        }
                    }
            ).start();
        } catch (Throwable t) {
            Timber.e(t);
        }
    }

    static class VerifyPackAuth {
        /**
         * ===========================================================================
         * Check whether our hidden verification switch has been flipped.
         * If not, perform a silent exit of the application with no stack trace.
         * ===========================================================================
         */
        static void verifyAuthHasCompleted() {
            if (!AnimationUtils.hiddenAuthTriggeredBool) {
                System.exit(-1);
            }
        }
    }
}
