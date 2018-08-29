package com.ljmu.andre.snaptools.Utils;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import java.util.concurrent.TimeUnit;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ShellUtils {
    private static final long SHELL_TIMEOUT = TimeUnit.SECONDS.toMillis(10);

    public static void sendCommand(String command, Callable<Boolean> resultCallable) {
        sendCommand(command)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        resultCallable.call(aBoolean);
                    }
                });
    }

    public static Observable<Boolean> sendCommand(String command) {
        return sendCommand(command, Schedulers.computation());
    }

    public static Observable<Boolean> sendCommand(String command, Scheduler scheduler) {
        return Observable.fromCallable(() -> {
            Timber.d("Shell command on thread: " + Thread.currentThread().getName());

            CommandResult cmd = Shell.SU.run(command);
            if (cmd.isSuccessful()) {
                Timber.d("Shell command '" + command + "' executed successfully.");
                return true;
            } else {
                Timber.e(cmd.getStderr());
            }

            return false;
        }).subscribeOn(scheduler);
    }

    public static boolean sendCommandSync(String command) {
        CommandResult cmd = Shell.SU.run(command);
        if (cmd.isSuccessful()) {
            Timber.d("Shell command '" + command + "' executed successfully.");
            return true;
        } else {
            Timber.e(cmd.getStderr());
        }

        return false;
    }
}
