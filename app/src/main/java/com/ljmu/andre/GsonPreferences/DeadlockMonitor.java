package com.ljmu.andre.GsonPreferences;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.ljmu.andre.GsonPreferences.DeadlockItemModel.DeadlockItem;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class DeadlockMonitor extends Thread {
    private static final int MONITOR_WAIT_TIME = 5;
    private static final TimeUnit MONITOR_TIME_UNIT = TimeUnit.SECONDS;
    private static DeadlockMonitor instance;
    private static int tick = 0;
    private static Runnable ticker = () -> {
        synchronized (Preferences.LOCK) {
            tick = (tick + 1) % Integer.MAX_VALUE;
        }
    };

    private DeadlockMonitor(String name) {
        super(name);
        Timber.d("Created deadlock monitor");
    }

    @Override
    public void run() {
        HandlerThread lockHandlerThread = new HandlerThread("Pref Lock Handler");
        lockHandlerThread.start();
        Handler lockHandler = new Handler(lockHandlerThread.getLooper());

        while (!isInterrupted()) {
//			Timber.d("Scanning deadlock monitor");
            int lastTick = tick;
//			Timber.d("LastTick: " + lastTick);
            lockHandler.post(ticker);

            try {
                MONITOR_TIME_UNIT.sleep(MONITOR_WAIT_TIME);
            } catch (InterruptedException e) {
                Timber.e(e);
            }

            Timber.d("Tick after monitor: " + tick);

            if (lastTick == tick) {
                logAllThreadsStacks();
            }
        }

        lockHandler.removeCallbacks(ticker);
        lockHandlerThread.quit();

        Timber.e("DEADLOCK MONITOR EXITED");
    }

    private static void logAllThreadsStacks() {
        Thread mainThread = Looper.getMainLooper().getThread();

        Map<Thread, StackTraceElement[]> stackTraces = new TreeMap<>((left, right) -> {
            if (left == right)
                return 0;
            if (left == mainThread)
                return 1;
            if (right == mainThread)
                return -1;
            return right.getName().compareTo(left.getName());
        });

        stackTraces.putAll(Thread.getAllStackTraces());

        if (!stackTraces.containsKey(mainThread)) {
            stackTraces.put(mainThread, mainThread.getStackTrace());
        }

        DeadlockItem threadReplica = null;
        for (Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            String threadTitle = getThreadTitle(entry.getKey());

            threadReplica = DeadlockItemModel.generate(threadTitle, entry.getValue(), threadReplica);
        }

        Timber.e(new DeadlockException("Preference Deadlock", threadReplica));
    }

    private static String getThreadTitle(Thread thread) {
        return thread.getName() + " (state = " + thread.getState() + ")";
    }

    public synchronized static void init() {
        boolean wasDead = false;

        if (instance != null && !instance.isAlive()) {
            instance.interrupt();
            wasDead = true;
        }

        if (instance == null || wasDead) {
            instance = new DeadlockMonitor("Preference Deadlock Monitor");
            instance.start();
        }
    }

    private static class DeadlockException extends Exception {
        private static final long serialVersionUID = 6021707890871456237L;

        DeadlockException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
