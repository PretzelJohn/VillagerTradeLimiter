package com.pretzel.dev.villagertradelimiter.listeners;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.nms.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {
    private static final Material[] MATERIALS = new Material[] { Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.BELL, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.SHIELD, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.FILLED_MAP, Material.FISHING_ROD, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.LEATHER_HORSE_ARMOR, Material.SADDLE, Material.ENCHANTED_BOOK, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_PICKAXE, Material.STONE_HOE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS };

    private final VillagerTradeLimiter instance;

    public PlayerListener(VillagerTradeLimiter instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Villager)) return;
        final Villager villager = (Villager)event.getRightClicked();
        if(Util.isNPC(villager)) return; //Skips NPCs
        if(villager.getProfession() == Villager.Profession.NONE || villager.getProfession() == Villager.Profession.NITWIT) return; //Skips non-trading villagers
        if(villager.getRecipeCount() == 0) return; //Skips non-trading villagers

        //DisableTrading feature
        if(instance.getCfg().isBoolean("DisableTrading")) {
            if(instance.getCfg().getBoolean("DisableTrading", false)) {
                event.setCancelled(true);
                return;
            }
        } else {
            List<String> disabledWorlds = instance.getCfg().getStringList("DisableTrading");
            for(String world : disabledWorlds) {
                if(event.getPlayer().getWorld().getName().equals(world)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        final Player player = event.getPlayer();
        if(Util.isNPC(player)) return; //Skips NPCs
        this.hotv(player);
        this.setIngredients(villager);
        this.setData(villager);
        this.maxDiscount(villager, player);
        this.maxDemand(villager);
    }

    private void setIngredients(final Villager villager) {
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        final NBTEntity villagerNBT = new NBTEntity(villager);
        NBTCompoundList recipes = villagerNBT.getCompound("Offers").getCompoundList("Recipes");
        for (NBTCompound recipe : recipes) {
            if(overrides != null) {
                for(final String override : overrides.getKeys(false)) {
                    final ConfigurationSection item = this.getItem(recipe, override);
                    if(item != null) {
                        if (item.contains("item-1-material"))
                            recipe.getCompound("buy").setString("id", "minecraft:" + item.getString("item-1-material"));
                        if (item.contains("item-2-material"))
                            recipe.getCompound("buyB").setString("id", "minecraft:" + item.getString("item-2-material"));

                        if (recipe.getCompound("buy").getString("id") != "minecraft:air" && item.contains("item-1-amount")) {
                            int cost = item.getInt("item-1-amount");
                            if (cost <= 0)
                                cost = 1;
                            else if (cost > 64)
                                cost = 64;
                            recipe.getCompound("buy").setInteger("Count", cost);
                        }

                        if (recipe.getCompound("buyB").getString("id") != "minecraft:air" && item.contains("item-2-amount")) {
                            int cost2 = item.getInt("item-2-amount");
                            if (cost2 <= 0)
                                cost2 = 1;
                            else if (cost2 > 64)
                                cost2 = 64;
                            recipe.getCompound("buyB").setInteger("Count", cost2);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void setData(final Villager villager) {
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        final NBTEntity villagerNBT = new NBTEntity(villager);
        NBTCompoundList recipes = villagerNBT.getCompound("Offers").getCompoundList("Recipes");
        for (NBTCompound recipe : recipes) {
            if(overrides != null) {
                for(final String override : overrides.getKeys(false)) {
                    final ConfigurationSection item = this.getItem(recipe, override);
                    if(item != null) {
                        if (item.contains("uses")) {
                            int uses = item.getInt("uses");
                            if (uses > 0)
                                recipe.setInteger("maxUses", uses);
                        }
                        break;
                    }
                }
            }
        }
    }

    //Hero of the Village effect limiter feature
    private void hotv(final Player player) {
        final PotionEffectType effect = PotionEffectType.HERO_OF_THE_VILLAGE;
        if(!player.hasPotionEffect(effect)) return; //Skips when player doesn't have HotV

        final int maxHeroLevel = instance.getCfg().getInt("MaxHeroLevel", 1);
        if(maxHeroLevel == 0) player.removePotionEffect(effect);
        if(maxHeroLevel <= 0) return; //Skips when disabled in config.yml

        final PotionEffect pot = player.getPotionEffect(effect);
        if(pot.getAmplifier() > maxHeroLevel-1) {
            player.removePotionEffect(effect);
            player.addPotionEffect(new PotionEffect(effect, pot.getDuration(), maxHeroLevel-1));
        }
    }

    //MaxDiscount feature - limits the lowest discounted price to a % of the base price
    private void maxDiscount(final Villager villager, final Player player) {
        int majorPositiveValue = 0, minorPositiveValue = 0, tradingValue = 0, minorNegativeValue = 0, majorNegativeValue = 0;

        NBTEntity nbtEntity = new NBTEntity(villager);
        final NBTEntity playerNBT = new NBTEntity(player);
        final String playerUUID = Util.intArrayToString(playerNBT.getIntArray("UUID"));
        if (nbtEntity.hasKey("Gossips")) {
            NBTCompoundList gossips = nbtEntity.getCompoundList("Gossips");
            for (NBTCompound gossip : gossips) {
                final String type = gossip.getString("Type");
                final String targetUUID = Util.intArrayToString(gossip.getIntArray("Target"));
                final int value = gossip.getInteger("Value");
                if (targetUUID == playerUUID) {
                    switch (type) {
                        case "trading": tradingValue = value; break;
                        case "minor_positive": minorPositiveValue = value; break;
                        case "minor_negative": minorNegativeValue = value; break;
                        case "major_positive": majorPositiveValue = value; break;
                        case "major_negative": majorNegativeValue = value; break;
                        default: break;
                    }
                }
            }
        }
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");

        final NBTEntity villagerNBT = new NBTEntity(villager);
        NBTCompoundList recipes = villagerNBT.getCompound("Offers").getCompoundList("Recipes");
        List<NBTCompound> remove = new ArrayList<>();
        for (NBTCompound recipe : recipes) {
            final int ingredientAmount = recipe.getCompound("buy").getInteger("Count");
            final float priceMultiplier = this.getPriceMultiplier(recipe);
            final int valueModifier = 5 * majorPositiveValue + minorPositiveValue + tradingValue - minorNegativeValue - 5 * majorNegativeValue;
            final float finalValue = ingredientAmount - priceMultiplier * valueModifier;
            boolean disabled = false;
            double maxDiscount = instance.getCfg().getDouble("MaxDiscount", 0.3);
            if(overrides != null) {
                for(final String override : overrides.getKeys(false)) {
                    final ConfigurationSection item = this.getItem(recipe, override);
                    if(item != null) {
                        disabled = item.getBoolean("Disabled", false);
                        maxDiscount = item.getDouble("MaxDiscount", maxDiscount);
                        break;
                    }
                }
            }
            if(maxDiscount >= 0.0 && maxDiscount <= 1.0) {
                if(finalValue < ingredientAmount * (1.0 - maxDiscount) && finalValue != ingredientAmount) {
                    recipe.setFloat("priceMultiplier", ingredientAmount * (float)maxDiscount / valueModifier);
                } else {
                    recipe.setFloat("priceMultiplier", priceMultiplier);
                }
            } else {
                recipe.setFloat("priceMultiplier", priceMultiplier);
            }
            if(disabled)
                remove.add(recipe);
        }
        remove.forEach(rem -> { recipes.remove(rem); });
    }

    //MaxDemand feature - limits demand-based price increases
    private void maxDemand(final Villager villager) {
        final NBTEntity villagerNBT = new NBTEntity(villager);
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        if (villagerNBT.hasKey("Offers")) {
            NBTCompoundList recipes = villagerNBT.getCompound("Offers").getCompoundList("Recipes");
            for (NBTCompound recipe : recipes) {
                final int demand = recipe.getInteger("demand");
                int maxDemand = instance.getCfg().getInt("MaxDemand", -1);
                if (overrides != null) {
                    for (String override : overrides.getKeys(false)) {
                        final ConfigurationSection item = this.getItem(recipe, override);
                        if(item != null) {
                            maxDemand = item.getInt("MaxDemand", maxDemand);
                            break;
                        }
                    }
                }
                if(maxDemand >= 0 && demand > maxDemand) {
                    recipe.setInteger("demand", maxDemand);
                }
            }
        }
    }

    //Returns the price multiplier for a given trade
    private float getPriceMultiplier(final NBTCompound recipe) {
        float p = 0.05f;
        final Material type = recipe.getItemStack("sell").getType();
        for(int length = MATERIALS.length, i = 0; i < length; ++i) {
            if(type == MATERIALS[i]) {
                p = 0.2f;
                break;
            }
        }
        return p;
    }

    //Returns the configured settings for a trade
    private ConfigurationSection getItem(final NBTCompound recipe, final String k) {
        final ConfigurationSection item = instance.getCfg().getConfigurationSection("Overrides."+k);
        if(item == null) return null;

        if(!k.contains("_")) {
            //Return the item if the item name is valid
            if(this.verify(recipe, Material.matchMaterial(k))) return item;
            return null;
        }

        final String[] words = k.split("_");
        try {
            //Return the enchanted book item if there's a number in the item name
            final int level = Integer.parseInt(words[words.length-1]);
            if(recipe.getItemStack("sell").getType() == Material.ENCHANTED_BOOK) {
                final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) recipe.getItemStack("sell").getItemMeta();
                final Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(k.substring(0, k.lastIndexOf("_"))));
                if (meta == null || enchantment == null) return null;
                if (meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) == level) return item;
            }
        } catch(NumberFormatException e) {
            //Return the item if the item name is valid
            if(this.verify(recipe, Material.matchMaterial(k)))
                return item;
            return null;
        } catch(Exception e2) {
            //Send an error message
            Util.errorMsg(e2);
        }
        return null;
    }

    //Verifies that an item exists in the villager's trade
    private boolean verify(final NBTCompound recipe, final Material material) {
        return ((recipe.getItemStack("sell").getType() == material) || (recipe.getItemStack("buy").getType() == material));
    }
}
