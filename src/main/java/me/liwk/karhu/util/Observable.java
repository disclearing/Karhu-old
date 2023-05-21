package me.liwk.karhu.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Observable<T> {



    Set<ChangeObserver<T>> observers = Collections.newSetFromMap(new ConcurrentHashMap<ChangeObserver<T>, Boolean>());
    //private final Set<ChangeObserver<T>> observers = Sets.new();
    private T value;

    public Observable(final T initValue) {
        this.value = initValue;
    }

    public T get() {
        return value;
    }

    public void set(final T value) {

        final T oldValue = this.value;

        this.value = value;

        observers.forEach((it) -> it.handle(oldValue, value));
    }


    public ChangeObserver<T> observe(final ChangeObserver<T> onChange) {
        observers.add(onChange);
        return onChange;
    }

    public void unobserve(final ChangeObserver<T> onChange) {
        observers.remove(onChange);
    }

    @FunctionalInterface
    public interface ChangeObserver<T> {

        void handle(final T from, final T to);

    }
}
