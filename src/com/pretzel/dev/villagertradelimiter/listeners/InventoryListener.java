package com.pretzel.dev.villagertradelimiter.listeners;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.data.PlayerData;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.settings.Settings;
import com.pretzel.dev.villagertradelimiter.wrappers.VillagerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class InventoryListener implements Listener {
    private final VillagerTradeLimiter instance;
    private final Settings settings;

    /**
     * @param instance The instance of VillagerTradeLimiter.java
     * @param settings The settings instance
     */
    public InventoryListener(final VillagerTradeLimiter instance, final Settings settings) {
        this.instance = instance;
        this.settings = settings;
    }

    /** Handles when a player stops trading with a villager */
    @EventHandler
    public void onPlayerStopTrading(final InventoryCloseEvent event) {
        //Don't do anything unless the player is actually finished trading with a villager
        if(event.getInventory().getType() != InventoryType.MERCHANT) return;
        if(!(event.getInventory().getHolder() instanceof Villager)) return;
        if(!(event.getPlayer() instanceof Player)) return;
        final Player player = (Player)event.getPlayer();
        if(Util.isNPC(player)) return;

        //Reset the villager's NBT data when a player is finished trading
        final PlayerData playerData = instance.getPlayerData().get(player.getUniqueId());
        if(playerData == null) return;

        final VillagerWrapper villager = playerData.getTradingVillager();
        if(villager == null) return;
        playerData.setTradingVillager(null);
        villager.reset();
    }

    /** Handles when a player successfully trades with a villager */
    @EventHandler
    public void onPlayerMakeTrade(final InventoryClickEvent event) {
        if(event.getInventory().getType() != InventoryType.MERCHANT) return;
        if(!(event.getInventory().getHolder() instanceof Villager)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(event.getRawSlot() != 2) return;
        final Player player = (Player)event.getWhoClicked();
        if(Util.isNPC(player)) return;

        //Get the items involved in the trade
        final ItemStack result = event.getCurrentItem();
        ItemStack ingredient1 = event.getInventory().getItem(0);
        ItemStack ingredient2 = event.getInventory().getItem(1);
        if(result == null || result.getType() == Material.AIR) return;
        if(ingredient1 == null) ingredient1 = new ItemStack(Material.AIR, 1);
        if(ingredient2 == null) ingredient2 = new ItemStack(Material.AIR, 1);

        //Check if there is a cooldown set for the trade
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        if(overrides == null) return;

        final String type = settings.getType(result, ingredient1, ingredient2);
        if(type == null || !overrides.contains(type+".Cooldown")) return;

        //Get the selected recipe by the items in the slots
        final MerchantRecipe selectedRecipe = getSelectedRecipe((Villager)event.getInventory().getHolder(), ingredient1, ingredient2, result);
        if(selectedRecipe == null) {
            event.setCancelled(true);
            return;
        }

        //Add a cooldown to the trade if the player has reached the max uses
        final PlayerData playerData = instance.getPlayerData().get(player.getUniqueId());
        if(playerData == null || playerData.getTradingVillager() == null) return;
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            int uses = selectedRecipe.getUses();
            if(!playerData.getTradingCooldowns().containsKey(type) && uses >= selectedRecipe.getMaxUses()) {
                playerData.getTradingCooldowns().put(type, System.currentTimeMillis());
            }
        }, 1);
    }

    /**
     * @param villager The villager to get the recipe from
     * @param ingredient1 The item in the first ingredient slot of the trade interface
     * @param ingredient2 The item in the second ingredient slot of the trade interface
     * @param result The item in the result slot of the trade interface
     * @return The villager's recipe that matches the items in the slots
     */
    private MerchantRecipe getSelectedRecipe(final Villager villager, final ItemStack ingredient1, final ItemStack ingredient2, final ItemStack result) {
        for(MerchantRecipe recipe : villager.getRecipes()) {
            final ItemStack item1 = recipe.getIngredients().get(0);
            final ItemStack item2 = recipe.getIngredients().get(1);
            if(!recipe.getResult().isSimilar(result)) continue;
            if((item1.isSimilar(ingredient1) && item2.isSimilar(ingredient2)) || (item1.isSimilar(ingredient2) && item2.isSimilar(ingredient1)))
                return recipe;
        }
        return null;
    }
}
