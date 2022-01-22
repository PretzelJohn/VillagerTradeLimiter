package com.pretzel.dev.villagertradelimiter.settings;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.wrappers.RecipeWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class Settings {
    private final VillagerTradeLimiter instance;

    /** @param instance The instance of VillagerTradeLimiter.java */
    public Settings(final VillagerTradeLimiter instance) { this.instance = instance; }

    /**
     * @param entity The entity to check the NPC status of
     * @return True if the entity is an NPC and config is set to ignore NPCs
     */
    public boolean shouldSkipNPC(final Entity entity) {
        if(entity == null) return true;
        if(instance.getCfg().getBoolean("IgnoreCitizens", true) && Util.isNPC(entity)) return true;
        return instance.getCfg().getBoolean("IgnoreShopkeepers", true) && Util.isShopkeeper(entity);
    }

    /**
     * @param recipe The wrapped recipe to fetch any overrides for
     * @param key The key where the fetched value is stored in config.yml (e.g, DisableTrading)
     * @param defaultValue The default boolean value to use if the key does not exist
     * @return A boolean value that has the most specific value possible between the global setting and the overrides settings
     */
    public boolean fetchBoolean(final RecipeWrapper recipe, String key, boolean defaultValue) {
        boolean global = instance.getCfg().getBoolean(key, defaultValue);
        final ConfigurationSection override = getOverride(recipe.getItemStack("buy"), recipe.getItemStack("sell"));
        if(override != null) return override.getBoolean(key, global);
        return global;
    }

    /**
     * @param recipe The wrapped recipe to fetch any overrides for
     * @param key The key where the fetched value is stored in config.yml (e.g, MaxDemand)
     * @param defaultValue The default integer value to use if the key does not exist
     * @return An integer value that has the most specific value possible between the global setting and the overrides settings
     */
    public int fetchInt(final RecipeWrapper recipe, String key, int defaultValue) {
        int global = instance.getCfg().getInt(key, defaultValue);
        final ConfigurationSection override = getOverride(recipe.getItemStack("buy"), recipe.getItemStack("sell"));
        if(override != null) return override.getInt(key, global);
        return global;
    }

    /**
     * @param recipe The wrapped recipe to fetch any overrides for
     * @param key The key where the fetched value is stored in config.yml (e.g, MaxDiscount)
     * @param defaultValue The default double value to use if the key does not exist
     * @return A double value that has the most specific value possible between the global setting and the overrides settings
     */
    public double fetchDouble(final RecipeWrapper recipe, String key, double defaultValue) {
        double global = instance.getCfg().getDouble(key, defaultValue);
        final ConfigurationSection override = getOverride(recipe.getItemStack("buy"), recipe.getItemStack("sell"));
        if(override != null) return override.getDouble(key, global);
        return global;
    }

    /**
     * @param result The itemstack for the recipe's result
     * @param ingredient1 The itemstack for the recipe's first ingredient
     * @param ingredient2 The itemstack for the recipe's second ingredient
     * @return The matched type of the item, if any
     */
    public String getType(final ItemStack result, final ItemStack ingredient1, final ItemStack ingredient2) {
        final String resultType = result.getType().name().toLowerCase();
        final String ingredient1Type = ingredient1.getType().name().toLowerCase();
        final String ingredient2Type = ingredient2.getType().name().toLowerCase();
        final String defaultType;
        if(result.getType() == Material.EMERALD) {
            if(ingredient1.getType() == Material.BOOK || ingredient1.getType() == Material.AIR) {
                defaultType = ingredient2Type;
            } else {
                defaultType = ingredient1Type;
            }
        } else {
            defaultType = resultType;
        }

        if(result.getType() == Material.ENCHANTED_BOOK) {
            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
            if(meta == null) return defaultType;
            for(Enchantment key : meta.getStoredEnchants().keySet()) {
                if (key != null) {
                    final String itemType = key.getKey().getKey() +"_"+meta.getStoredEnchantLevel(key);
                    if(getItem(ingredient1, result, itemType) != null) return itemType;
                }
            }
            return defaultType;
        }

        final ItemStack ingredient = (ingredient1.getType() == Material.AIR ? ingredient2 : ingredient1);
        if(getItem(ingredient, result, resultType) != null) return resultType;
        if(getItem(ingredient, result, ingredient1Type) != null) return ingredient1Type;
        if(getItem(ingredient, result, ingredient2Type) != null) return ingredient2Type;
        return defaultType;
    }

    /**
     * @param buy The first ingredient of the recipe
     * @param sell The result of the recipe
     * @return The corresponding override config section for the recipe, if it exists, or null
     */
    public ConfigurationSection getOverride(final ItemStack buy, ItemStack sell) {
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        if(overrides != null) {
            for(final String override : overrides.getKeys(false)) {
                final ConfigurationSection item = this.getItem(buy, sell, override);
                if(item != null) return item;
            }
        }
        return null;
    }

    /**
     * @param buy The first ingredient of the recipe
     * @param sell The result of the recipe
     * @param key The key where the override settings are stored in config.yml
     * @return The corresponding override config section for the recipe, if it exists, or null
     */
    public ConfigurationSection getItem(final ItemStack buy, final ItemStack sell, final String key) {
        final ConfigurationSection item = instance.getCfg().getConfigurationSection("Overrides."+key);
        if(item == null) return null;

        if(!key.contains("_")) {
            //Return the item if the item name is valid
            if(this.verify(buy, sell, Material.matchMaterial(key))) return item;
            return null;
        }

        final String[] words = key.split("_");
        try {
            //Return the enchanted book item if there's a number in the item name
            final int level = Integer.parseInt(words[words.length-1]);
            if(sell.getType() == Material.ENCHANTED_BOOK) {
                final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) sell.getItemMeta();
                final Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(key.substring(0, key.lastIndexOf("_"))));
                if (meta == null || enchantment == null) return null;
                if (meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) == level) return item;
            }
        } catch(NumberFormatException e) {
            //Return the item if the item name is valid
            if(this.verify(buy, sell, Material.matchMaterial(key)))
                return item;
            return null;
        } catch(Exception e2) {
            //Send an error message
            Util.errorMsg(e2);
        }
        return null;
    }

    /**
     * @param buy The first ingredient of the recipe
     * @param sell The result of the recipe
     * @param material The material to compare the recipe against
     * @return True if a recipe matches an override section, false otherwise
     */
    private boolean verify(final ItemStack buy, final ItemStack sell, final Material material) {
        return ((buy.getType() == material) || (sell.getType() == material));
    }
}
