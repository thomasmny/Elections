package com.eintosti.elections.api;

import com.eintosti.elections.ElectionsPlugin;
import com.eintosti.elections.api.election.Election;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

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