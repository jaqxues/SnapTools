package com.ljmu.andre.snaptools.Utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 15.05.20 - Time 15:20.
 *
 * Allows to handle Events in multiple observers, without handling an Event twice
 */
abstract class EventObserver<T> : Observer<T> {
    var lastChecked = 0
    final override fun onChanged(t: T) {
        // Only handle event once
        if (lastChecked == t.hashCode())
            return
        handleEvent(t)
        lastChecked = t.hashCode()
    }

    abstract fun handleEvent(t: T)
}

inline fun <T> LiveData<T>.addEventObserver(lifecycleOwner: LifecycleOwner, crossinline handler: (T) -> Unit) {
    observe(lifecycleOwner, object: EventObserver<T>() {
        override fun handleEvent(t: T) {
            handler(t)
        }
    })
}

inline fun <T> LiveData<T>.consumeResult(crossinline action: (T) -> Unit) {
    observeForever(object: Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            action(t)
        }
    })
}