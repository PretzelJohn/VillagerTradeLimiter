package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;

public class NBTTagList
{
    private final NMS nms;
    private final Object self;
    private final Class<?> c;

    public NBTTagList(final NMS nms, final Object self) {
        this.nms = nms;
        this.self = self;
        if(nms.getVersion().compareTo("v1_17_R1") < 0)
            this.c = nms.getNMSClass("server."+nms.getVersion()+".NBTTagList");
        else
            this.c = nms.getNMSClass("nbt.NBTTagList");
    }

    public NBTTagCompound getCompound(final int index) {
        try {
            return new NBTTagCompound(this.nms, this.nms.getMethod(this.c, "getCompound", Integer.TYPE).invoke(this.self, index));
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public int size() {
        try {
            return (int)this.nms.getMethod(this.c, "size").invoke(this.self, new Object[0]);
        } catch (Exception e) {
            Util.errorMsg(e);
            return -1;
        }
    }

    public Object getSelf() {
        return this.self;
    }
}
