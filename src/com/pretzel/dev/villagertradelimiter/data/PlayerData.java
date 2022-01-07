package com.pretzel.dev.villagertradelimiter.data;

import com.pretzel.dev.villagertradelimiter.wrappers.VillagerWrapper;
import org.bukkit.entity.Player;

public class PlayerData {
    private final Player player;
    private VillagerWrapper tradingVillager;

    public PlayerData(final Player player) {
        this.player = player;
        this.tradingVillager = null;
    }

    /** @param tradingVillager The villager that this player is currently trading with */
    public void setTradingVillager(VillagerWrapper tradingVillager) { this.tradingVillager = tradingVillager; }

    /** @return The player that this data is for */
    public Player getPlayer() { return this.player; }

    /** @return The villager that this player is currently trading with */
    public VillagerWrapper getTradingVillager() { return this.tradingVillager; }
}
