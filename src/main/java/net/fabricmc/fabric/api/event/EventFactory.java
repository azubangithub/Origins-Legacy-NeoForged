package net.fabricmc.fabric.api.event;

import java.util.function.Function;

public final class EventFactory {
    public static <T> Event<T> createArrayBacked(Class<T> type, Function<T[], T> invokerFactory) {
        return new Event<>(type, invokerFactory);
    }
}
