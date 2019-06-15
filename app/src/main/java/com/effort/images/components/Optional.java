package com.effort.images.components;


import android.support.v4.util.Consumer;

public final class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    private T object;

    public Optional() {
        this.object = null;
    }

    private Optional(T object) {
        this.object = object;
    }

    public static <T> Optional<T> of(T object) {
        return new Optional<>(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> empty() {
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (object != null) {
            consumer.accept(object);
        }
    }
}
