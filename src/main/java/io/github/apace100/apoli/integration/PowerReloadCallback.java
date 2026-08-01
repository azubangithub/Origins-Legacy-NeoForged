package io.github.apace100.apoli.integration;

import io.github.apace100.apoli.util.Event;
import io.github.apace100.apoli.util.EventFactory;

@Deprecated
public interface PowerReloadCallback {

    Event<PowerReloadCallback> EVENT = EventFactory.createArrayBacked(PowerReloadCallback.class,
        (listeners) -> () -> {
            for (PowerReloadCallback event : listeners) {
                event.onPowerReload();
            }
        }
    );

    void onPowerReload();
}

