package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;

public class NBTTagCompound {
    private final NMS nms;
    private final Class<?> c;
    private final Object self;

    public NBTTagCompound(final NMS nms, final Object self) {
        this.nms = nms;
        this.self = self;
        this.c = self.getClass();
    }

    public Object get(final String key) {
        try {
            return this.nms.getMethod(this.c, "get", String.class).invoke(this.self, key);
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public String getString(final String key) {
        try {
            return (String)this.nms.getMethod(this.c, "getString", String.class).invoke(this.self, key);
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public int getInt(final String key) {
        try {
            return (int)this.nms.getMethod(this.c, "getInt", String.class).invoke(this.self, key);
        } catch (Exception e) {
            Util.errorMsg(e);
            return Integer.MIN_VALUE;
        }
    }

    public int[] getIntArray(final String key) {
        try {
            return (int[])this.nms.getMethod(this.c, "getIntArray", String.class).invoke(this.self, key);
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public NBTTagCompound getCompound(final String key) {
        try {
            return new NBTTagCompound(this.nms, this.nms.getMethod(this.self.getClass(), "getCompound", String.class).invoke(this.self, key));
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public void setInt(final String key, final int value) {
        try {
            this.nms.getMethod(this.c, "setInt", String.class, Integer.TYPE).invoke(this.self, key, value);
        } catch (Exception e) {
            Util.errorMsg(e);
        }
    }

    public Object getSelf() { return this.self; }
    public Class<?> getC() { return this.c; }
}
