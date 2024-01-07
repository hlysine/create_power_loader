package com.hlysine.create_power_loader.content;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeakCollection<T> implements Collection<T> {
    private final WeakHashMap<T, Void> items = new WeakHashMap<>();

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.containsKey(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return items.keySet().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return items.keySet().toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return items.keySet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (items.containsKey(t)) return false;
        items.put(t, null);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return items.remove(o, null);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return items.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean changed = false;
        for (T t : c) {
            if (items.containsKey(t)) continue;
            changed = true;
            items.put(t, null);
        }
        return changed;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return items.keySet().removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return items.keySet().retainAll(c);
    }

    @Override
    public void clear() {
        items.clear();
    }
}
