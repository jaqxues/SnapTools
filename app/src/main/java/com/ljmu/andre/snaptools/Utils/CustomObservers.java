package com.ljmu.andre.snaptools.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CustomObservers {
    public static class ErrorObserver<T> extends DefaultObserver<T> {
        private String messageTag = "";

        public ErrorObserver() {
        }

        public ErrorObserver(String messageTag) {
            this.messageTag = messageTag;
        }

        @Override
        public void onNext(@NonNull T t) {
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Timber.e(e, messageTag);
        }

        @Override
        public void onComplete() {
        }
    }

    public static abstract class SimpleObserver<T> extends DefaultObserver<T> {
        private String messageTag = "";

        public SimpleObserver() {
        }

        public SimpleObserver(String messageTag) {
            this.messageTag = messageTag;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Timber.e(e, messageTag);
        }

        @Override
        public void onComplete() {
        }
    }
}
