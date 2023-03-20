package com.pretzel.dev.villagertradelimiter.listeners;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.data.Cooldown;
import com.pretzel.dev.villagertradelimiter.data.PlayerData;
import com.pretzel.dev.villagertradelimiter.settings.Settings;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.time.Instant;
import java.util.Date;
import java.util.List;
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

    /** Handles villager promotions */
    @EventHandler
    public void onVillagerPromotion(final VillagerAcquireTradeEvent event) {
        //Gets the items in the trade
        final MerchantRecipe recipe = event.getRecipe();
        List<ItemStack> items = recipe.getIngredients();
        items.add(recipe.getResult());

        //Gets the disabled item list from config
        List<String> disabledItems = instance.getCfg().getStringList("DisableItems");

        //Checks each item if it should be removed from the trade list
        for(ItemStack item : items) {
            for(String disabledItem : disabledItems) {
                if(disabledItem.equalsIgnoreCase(item.getType().name())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    /** Handles villager profession change **/
    @EventHandler
    public void onVillagerChangeProfession(final VillagerCareerChangeEvent event) {
        //Gets the new profession
        final Villager.Profession profession = event.getProfession();

        //Gets the disabled profession list from config
        List<String> disabledProfessions = instance.getCfg().getStringList("DisableProfessions");

        //Changes the new profession to none if disabled in config
        for(String disabledProfession : disabledProfessions) {
            if(disabledProfession.equalsIgnoreCase(profession.name())) {
                event.setProfession(Villager.Profession.NONE);
                return;
            }
        }
    }

    /** Handles villager restocks */
    @EventHandler
    public void onVillagerRestock(final VillagerReplenishTradeEvent event) {
        if(!(event.getEntity() instanceof Villager)) return;
        final Villager villager = (Villager)event.getEntity();
        if(settings.shouldSkipNPC(villager)) return; //Skips NPCs

        //Get the items involved in the restock
        final MerchantRecipe recipe = event.getRecipe();
        final ItemStack result = recipe.getResult();
        ItemStack ingredient1 = recipe.getIngredients().get(0);
        ItemStack ingredient2 = recipe.getIngredients().get(1);
        final String type = settings.getType(result, ingredient1, ingredient2);

        //Get the villager's data container
        final UUID uuid = villager.getUniqueId();
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
