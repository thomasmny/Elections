/*
 * Copyright (c) 2018-2024, Thomas Meaney
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.eintosti.elections.api;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link ElectionsProvider} is a singleton class that provides access to the Elections API.
 */
@NullMarked
public final class ElectionsProvider {

    @Nullable
    private static Elections instance;

    /**
     * Gets the singleton Elections API instance.
     *
     * @return the Elections API instance
     */
    public static Elections get() {
        Elections instance = ElectionsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("Elections has not loaded yet!");
        }
        return instance;
    }

    static void set(final Elections impl) {
        ElectionsProvider.instance = impl;
    }

    private ElectionsProvider() {
        throw new AssertionError();
    }
}