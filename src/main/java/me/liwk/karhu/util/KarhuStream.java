package me.liwk.karhu.util;

import io.github.retrooper.packetevents.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Mostly just utility, i will look into caching methods and so on
 * for performance boosting when using the streams but i dont know yet lol
 */
public final class KarhuStream<T> {

    private final Supplier<Stream<T>> s;
    private final boolean parallel;

    private final Collection<T> c;

    public KarhuStream(List<T> l, boolean parallel) {
        this.s = l::stream;
        this.c = l;
        this.parallel = parallel;
    }

    public boolean any(Predicate<T> t) {
        return this.parallel ? this.s.get().parallel().anyMatch(t) : this.s.get().anyMatch(t);
    }

    public boolean all(Predicate<T> t) {
        return this.parallel ? this.s.get().parallel().allMatch(t) : this.s.get().allMatch(t);
    }

    public @NotNull Collection<T> getCollection() {
        return c;
    }

}
