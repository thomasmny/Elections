package com.eintosti.elections.api;

import org.jetbrains.annotations.NotNull;

public final class ElectionsProvider {

    private static Elections instance;

    /**
     * Gets the singleton Elections API instance.
     *
     * @return the Elections API instance
     */
    public static @NotNull Elections get() {
        Elections instance = ElectionsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Elections has not loaded yet!");
        }
        return instance;
    }

    static void set(Elections impl) {
        ElectionsProvider.instance = impl;
    }

    private ElectionsProvider() {
        throw new AssertionError();
    }
}