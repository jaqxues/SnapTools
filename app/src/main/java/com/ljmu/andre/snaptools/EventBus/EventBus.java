package com.ljmu.andre.snaptools.EventBus;

import java.util.HashSet;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class EventBus extends com.google.common.eventbus.EventBus {
    private static final Object BUS_LOCK = new Object();
    private static EventBus eventBus;
    private HashSet<Integer> subscribedHashCodeMap = new HashSet<>();

    private EventBus(String identifier) {
        super(identifier);
    }

    public static void soleRegister(Object object) {
        synchronized (BUS_LOCK) {
            EventBus eventBus = getInstance();

            int hashCode = object.hashCode();

            if (!eventBus.subscribedHashCodeMap.contains(hashCode)) {
                eventBus.register(object);
                eventBus.subscribedHashCodeMap.add(hashCode);
            }
        }
    }

    public static EventBus getInstance() {
        synchronized (BUS_LOCK) {
            if (eventBus == null)
                eventBus = new EventBus(EventBus.class.getSimpleName());

            return eventBus;
        }
    }

    public static void soleUnregister(Object object) {
        try {
            synchronized (BUS_LOCK) {
                EventBus eventBus = getInstance();

                int hashCode = object.hashCode();

                if (eventBus.subscribedHashCodeMap.contains(hashCode)) {
                    eventBus.unregister(object);
                    eventBus.subscribedHashCodeMap.remove(hashCode);
                }
            }
        } catch (Throwable t) {
            Timber.e(t, "Issue unregisterring [%s] from EventBus", object != null ? object.toString() : null);
        }
    }
}
