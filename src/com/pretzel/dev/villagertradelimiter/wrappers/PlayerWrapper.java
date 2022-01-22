package com.pretzel.dev.villagertradelimiter.wrappers;

import com.pretzel.dev.villagertradelimiter.lib.Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerWrapper {
    private final OfflinePlayer player;

    /** @param player The offline player that this wrapper wraps */
    public PlayerWrapper(final OfflinePlayer player) { this.player = player; }

    /**
     * @param isOld Whether the server is older than 1.16 or not. Minecraft changed how UUID's are represented in 1.16
     * @return A string representation of the player's UUID, for use when matching the player's UUID to a gossip's target UUID
     */
    public String getUUID(final boolean isOld) {
        final UUID uuid = player.getUniqueId();

        //BEFORE 1.16 (< 1.16)
        if(isOld) return uuid.getMostSignificantBits()+";"+uuid.getLeastSignificantBits();

        //AFTER 1.16 (>= 1.16)
        final String uuidString = uuid.toString().replace("-", "");
        int[] intArray = new int[4];
        for(int i = 0; i < 4; i++) {
            intArray[i] = (int)Long.parseLong(uuidString.substring(8*i, 8*(i+1)), 16);
        }
        return Util.intArrayToString(intArray);
    }

    /** @return The regular, online player of this wrapper's offline player, or null if the player is not online */
    public Player getPlayer() { return player.getPlayer(); }
}
