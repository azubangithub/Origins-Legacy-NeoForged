package io.github.apace100.apoli.integration;

import io.github.apace100.apoli.util.Event;
import io.github.apace100.apoli.util.EventFactory;

public interface PrePowerReloadCallback {

    Event<PrePowerReloadCallback> EVENT = EventFactory.createArrayBacked(PrePowerReloadCallback.class,
        (listeners) -> () -> {
            for (PrePowerReloadCallback event : listeners) {
                event.onPrePowerReload();
            }
        }
    );

    void onPrePowerReload();
}

