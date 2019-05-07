package com.ljmu.andre.snaptools.Utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ThreadUtils {
    private static final ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(
                    1,
                    Runtime.getRuntime().availableProcessors() * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadFactoryBuilder()
                            .setNameFormat("st-modld-%s")
                            .build()
            );

    public synchronized static ExecutorService getThreadPool() {
        return threadPool;
    }
}
