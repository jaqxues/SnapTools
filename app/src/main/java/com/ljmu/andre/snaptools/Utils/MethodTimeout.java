package com.ljmu.andre.snaptools.Utils;

import timber.log.Timber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class MethodTimeout<T> implements java.util.concurrent.Callable<T> {
    public T runWithTimeout(long timeout, TimeUnit timeUnit) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Object[] arrObjHolder = new Object[1];

        executor.execute(() -> {
            try {
                arrObjHolder[0] = call();
            } catch (Throwable e) {
                Timber.e(e);
            }
        });
        executor.shutdown(); // This does not cancel the already-scheduled task.

        T returnObject;
        boolean hitTimeout = !executor.awaitTermination(timeout, timeUnit);

        if (hitTimeout) {
            throw new InterruptedException(
                    String.format("Timeout Hit [Time: %s][Unit: %s]", timeout, timeUnit.name())
            );
        }

        returnObject = (T) arrObjHolder[0];

        return returnObject;
    }
}
