package com.pretzel.dev.villagertradelimiter.nms;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.entity.Entity;

public class CraftEntity {
    private final NMS nms;
    private final Class<?> c;

    public CraftEntity(final NMS nms) {
        this.nms = nms;
        this.c = nms.getCraftBukkitClass("entity.CraftEntity");
    }

    public Object getHandle(final Entity entity) {
        try {
            return nms.getMethod(this.c, "getHandle").invoke(this.c.cast(entity));
        } catch (Exception e) {
            Util.errorMsg(e);
            return null;
        }
    }
}
