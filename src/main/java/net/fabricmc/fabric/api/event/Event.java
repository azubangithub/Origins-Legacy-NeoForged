package net.fabricmc.fabric.api.event;

import java.lang.reflect.Array;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public final class Event<T> {
    private final Class<T> type;
    private final Function<T[], T> invokerFactory;
    private final CopyOnWriteArrayList<T> handlers = new CopyOnWriteArrayList<>();
    private volatile T invoker;

    public Event(Class<T> type, Function<T[], T> invokerFactory) {
        this.type = type;
        this.invokerFactory = invokerFactory;
        updateInvoker();
    }

    public void register(T handler) {
        if (handler == null) {
            throw new NullPointerException("Registered listener cannot be null");
        }
        handlers.add(handler);
        updateInvoker();
    }

    public T invoker() {
        return invoker;
    }

    @SuppressWarnings("unchecked")
    private void updateInvoker() {
        T[] array = (T[]) Array.newInstance(type, handlers.size());
        handlers.toArray(array);
        this.invoker = invokerFactory.apply(array);
    }
}
