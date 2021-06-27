package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.entity.Entity;

public class NBTContainer {
    private final NMS nms;
    private final Entity entity;
    private final NBTTagCompound tag;

    public NBTContainer(final NMS nms, final Entity entity) {
        this.nms = nms;
        this.entity = entity;
        this.tag = this.loadTag();
    }

    public NBTTagCompound loadTag() {
        final CraftEntity craftEntity = new CraftEntity(nms);
        final NMSEntity nmsEntity = new NMSEntity(nms);
        final Class<?> tgc;
        if(nms.getVersion().compareTo("v1.17_R1") < 0)
            tgc = nms.getNMSClass("server."+nms.getVersion()+".NBTTagCompound");
        else
            tgc = nms.getNMSClass("nbt.NBTTagCompound");
        try {
            final NBTTagCompound tag = new NBTTagCompound(nms, tgc.getDeclaredConstructor().newInstance());
            nmsEntity.save(craftEntity.getHandle(this.entity), tag);
            return tag;
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }

    public void saveTag(final Entity entity, final NBTTagCompound tag) {
        final CraftEntity craftEntity = new CraftEntity(nms);
        final NMSEntity nmsEntity = new NMSEntity(nms);
        nmsEntity.load(craftEntity.getHandle(entity), tag);
    }

    public NBTTagCompound getTag() {
        return this.tag;
    }
}