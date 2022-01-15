package com.pretzel.dev.villagertradelimiter.listeners;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.data.Cooldown;
import com.pretzel.dev.villagertradelimiter.data.PlayerData;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.settings.Settings;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class VillagerListener implements Listener {
    private final VillagerTradeLimiter instance;
    private final Settings settings;

    /**
     * @param instance The instance of VillagerTradeLimiter.java
     * @param settings The settings instance
     */
    public VillagerListener(final VillagerTradeLimiter instance, final Settings settings) {
        this.instance = instance;
        this.settings = settings;
    }

    /** Handles villager restocks */
    @EventHandler
    public void onVillagerRestock(final VillagerReplenishTradeEvent event) {
        if(!(event.getEntity() instanceof Villager)) return;
        if(Util.isNPC((Villager) event.getEntity())) return;

        //Get the items involved in the restock
        final MerchantRecipe recipe = event.getRecipe();
        final ItemStack result = recipe.getResult();
        ItemStack ingredient1 = recipe.getIngredients().get(0);
        ItemStack ingredient2 = recipe.getIngredients().get(1);
        final String type = settings.getType(result, ingredient1, ingredient2);

        //Get the villager's data container
        final UUID uuid = event.getEntity().getUniqueId();
        final PlayerData villagerData = instance.getPlayerData().get(uuid);
        if(villagerData == null) return;

        //Get the time of the last trade, restock cooldown setting, and now
        final String lastTradeStr = villagerData.getTradingCooldowns().get(type);
        if(lastTradeStr == null) return;

        String cooldownStr = instance.getCfg().getString("Restock", "0");
        cooldownStr = instance.getCfg().getString("Overrides."+type+".Restock", cooldownStr);

        final Date now = Date.from(Instant.now());
        final Date lastTrade = Cooldown.parseTime(lastTradeStr);
        if(lastTrade == null) return;
        final long cooldown = Cooldown.parseCooldown(cooldownStr);

        //Cancel the event if there is an active restock cooldown, otherwise remove the restock cooldown
        if(now.getTime()/1000L >= lastTrade.getTime()/1000L + cooldown) {
            villagerData.getTradingCooldowns().remove(type);
        } else {
            event.setCancelled(true);
        }
    }
}
