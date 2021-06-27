package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;

public class NMSEntity {
    private final NMS nms;
    private final Class<?> c;

    public NMSEntity(final NMS nms) {
        this.nms = nms;
        if(nms.getVersion().compareTo("v1_17_R1") < 0)
            this.c = nms.getNMSClass("server."+nms.getVersion()+".Entity");
        else
            this.c = nms.getNMSClass("world.entity.Entity");
    }

    public void save(final Object nmsEntity, final NBTTagCompound tag) {
        try {
            nms.getMethod(this.c, "save", tag.getC()).invoke(nmsEntity, tag.getSelf());
        } catch (Exception e) {
            Util.errorMsg(e);
        }
    }

    public void load(final Object nmsEntity, final NBTTagCompound tag) {
        try {
            nms.getMethod(this.c, "load", tag.getC()).invoke(nmsEntity, tag.getSelf());
        } catch (NoSuchMethodException e) {
            try {
                nms.getMethod(this.c, "f", tag.getC()).invoke(nmsEntity, tag.getSelf());
            } catch (Exception e2) {
                Util.errorMsg(e2);
            }
        } catch (Exception e3) {
            Util.errorMsg(e3);
        }
    }
}
