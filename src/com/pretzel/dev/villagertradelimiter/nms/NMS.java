package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.HashMap;

public class NMS
{
    private final HashMap<String, Class<?>> nmsClasses;
    private final HashMap<String, Class<?>> bukkitClasses;
    private final HashMap<String, Method> methods;
    private final String version;

    public NMS(final String version) {
        this.nmsClasses = new HashMap<>();
        this.bukkitClasses = new HashMap<>();
        this.methods = new HashMap<>();
        this.version = version;
    }

    public Class<?> getNMSClass(final String name) {
        if(this.nmsClasses.containsKey(name)) {
            return this.nmsClasses.get(name);
        }

        try {
            Class<?> c = Class.forName("net.minecraft."+name);
            this.nmsClasses.put(name, c);
            return c;
        } catch (Exception e) {
            Util.errorMsg(e);
            return this.nmsClasses.put(name, null);
        }
    }

    public Class<?> getCraftBukkitClass(final String name) {
        if(this.bukkitClasses.containsKey(name)) {
            return this.bukkitClasses.get(name);
        }

        try {
            Class<?> c = Class.forName("org.bukkit.craftbukkit." + this.version + "." + name);
            this.bukkitClasses.put(name, c);
            return c;
        } catch (Exception e) {
            Util.errorMsg(e);
            return this.bukkitClasses.put(name, null);
        }
    }

    public Method getMethod(final Class<?> invoker, final String name) throws NoSuchMethodException {
        return this.getMethod(invoker, name, null, null);
    }

    public Method getMethod(final Class<?> invoker, final String name, final Class<?> type) throws NoSuchMethodException {
        return this.getMethod(invoker, name, type, null);
    }

    public Method getMethod(final Class<?> invoker, final String name, final Class<?> type, final Class<?> type2) throws NoSuchMethodException {
        if(this.methods.containsKey(name) && this.methods.get(name).getDeclaringClass().equals(invoker)) {
            return this.methods.get(name);
        }
        Method method;
        try {
            if(type2 != null) {
                method = invoker.getMethod(name, type, type2);
            } else if(type != null) {
                method = invoker.getMethod(name, type);
            } else {
                method = invoker.getMethod(name);
            }
        } catch (Exception e) {
            Util.errorMsg(e);
            return this.methods.put(name, null);
        }
        this.methods.put(name, method);
        return method;
    }

    public String getVersion() { return this.version; }
}
