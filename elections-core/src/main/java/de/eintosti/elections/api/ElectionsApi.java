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

import de.eintosti.elections.ElectionsPlugin;
import de.eintosti.elections.api.election.Election;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

/**
 * Copied from <a><a href="https://github.com/lucko/spark/blob/aafc582149874584591def376c5a47de87c2a489/spark-common/src/main/java/me/lucko/spark/common/api/SparkApi.java">Spark</a></a>
 *
 * @author lucko
 */
public class ElectionsApi implements Elections {

    private static final Method SINGLETON_SET_METHOD;

    static {
        try {
            SINGLETON_SET_METHOD = ElectionsProvider.class.getDeclaredMethod("set", Elections.class);
            SINGLETON_SET_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Election getElection() {
        return JavaPlugin.getPlugin(ElectionsPlugin.class).getElection();
    }

    public static void register(Elections elections) {
        try {
            SINGLETON_SET_METHOD.invoke(null, elections);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void unregister() {
        try {
            SINGLETON_SET_METHOD.invoke(null, new Object[]{null});
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}