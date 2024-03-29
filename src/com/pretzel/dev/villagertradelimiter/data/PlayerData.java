package com.pretzel.dev.villagertradelimiter.data;

import com.pretzel.dev.villagertradelimiter.wrappers.VillagerWrapper;

import java.util.HashMap;

public class PlayerData {
    private final HashMap<String, String> tradingCooldowns;
    private VillagerWrapper tradingVillager;

    public PlayerData() {
        this.tradingCooldowns = new HashMap<>();
        this.tradingVillager = null;
    }

    /** @return The map of items to timestamps for the player's trading history */
    public HashMap<String, String> getTradingCooldowns() { return this.tradingCooldowns; }

    /** @param tradingVillager The villager that this player is currently trading with */
    public void setTradingVillager(final VillagerWrapper tradingVillager) { this.tradingVillager = tradingVillager; }

    /** @return The villager that this player is currently trading with */
    public VillagerWrapper getTradingVillager() { return this.tradingVillager; }
}
