package io.github.apace100.apoli.integration;

import io.github.apace100.apoli.util.Event;
import io.github.apace100.apoli.util.EventFactory;

public interface PostPowerReloadCallback {

    Event<PostPowerReloadCallback> EVENT = EventFactory.createArrayBacked(PostPowerReloadCallback.class,
        (listeners) -> () -> {
            for (PostPowerReloadCallback event : listeners) {
                event.onPostPowerReload();
            }
        }
    );

    void onPostPowerReload();
}

