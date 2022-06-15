package com.pretzel.dev.villagertradelimiter.wrappers;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import de.tr7zw.changeme.nbtapi.NBTCompound;

public class GossipWrapper {
    private final NBTCompound gossip;

    public enum GossipType {
        MAJOR_NEGATIVE(-5),
        MINOR_NEGATIVE(-1),
        TRADING(1),
        MINOR_POSITIVE(1),
        MAJOR_POSITIVE(5),
        OTHER(0);

        private final int weight;
        GossipType(int weight) { this.weight = weight; }
        int getWeight() { return this.weight; }
    }

    /** @param gossip The NBTCompound that contains the villager's NBT data of the gossip */
    public GossipWrapper(final NBTCompound gossip) { this.gossip = gossip; }

    /** @return The GossipType of this gossip: MAJOR_NEGATIVE, MINOR_NEGATIVE, TRADING, MINOR_POSITIVE, MAJOR_POSITIVE, or OTHER if not found */
    public GossipType getType() {
        try {
            return GossipType.valueOf(gossip.getString("Type").toUpperCase());
        } catch (IllegalArgumentException e) {
            return GossipType.OTHER;
        }
    }

    /**
     * @param isOld Whether the server is older than 1.16 or not. Minecraft changed how UUID's are represented in 1.16
     * @return A string representation of the target UUID, for use when matching the target UUID to a player's UUID
     */
    public String getTargetUUID(final boolean isOld) {
        //BEFORE 1.16 (< 1.16)
        if(isOld) return gossip.getLong("TargetMost")+";"+gossip.getLong("TargetLeast");

        //AFTER 1.16 (>= 1.16)
        return Util.intArrayToString(gossip.getIntArray("Target"));
    }

    /** @return The strength of this gossip, which is a value between 0 and: 25, 100, or 200, depending on the gossip type */
    public int getValue() { return gossip.getInteger("Value"); }
}
