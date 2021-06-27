package com.pretzel.dev.villagertradelimiter.listeners;

import com.pretzel.dev.villagertradelimiter.VillagerTradeLimiter;
import com.pretzel.dev.villagertradelimiter.lib.Util;
import com.pretzel.dev.villagertradelimiter.nms.*;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerListener implements Listener {
    private static final Material[] MATERIALS = new Material[] { Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.BELL, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.SHIELD, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.FILLED_MAP, Material.FISHING_ROD, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.LEATHER_HORSE_ARMOR, Material.SADDLE, Material.ENCHANTED_BOOK, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_PICKAXE, Material.STONE_HOE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.IRON_SWORD, Material.DIAMOND_SWORD };

    private VillagerTradeLimiter instance;
    private NMS nms;

    public PlayerListener(VillagerTradeLimiter instance) {
        this.instance = instance;
        this.nms = new NMS(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Villager)) return;
        final Villager villager = (Villager)event.getRightClicked();
        if(Util.isNPC(villager)) return; //Skips NPCs
        if(villager.getProfession() == Villager.Profession.NONE || villager.getProfession() == Villager.Profession.NITWIT) return; //Skips non-trading villagers
        if(villager.getRecipeCount() == 0) return; //Skips non-trading villagers
        if(instance.getCfg().getBoolean("DisableTrading", false)) {
            event.setCancelled(true);
            return;
        }

        final Player player = event.getPlayer();
        if(Util.isNPC(player)) return; //Skips NPCs
        this.hotv(player);
        this.maxDiscount(villager, player);
        this.maxDemand(villager);
    }

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

    private void maxDiscount(final Villager villager, final Player player) {
        final List<MerchantRecipe> recipes = villager.getRecipes();
        int a = 0, b = 0, c = 0, d = 0, e = 0;

        final NBTContainer vnbt = new NBTContainer(this.nms, villager);
        final NBTTagList gossips = new NBTTagList(this.nms, vnbt.getTag().get("Gossips"));
        final NBTContainer pnbt = new NBTContainer(this.nms, player);
        final String puuid = Util.intArrayToString(pnbt.getTag().getIntArray("UUID"));

        for (int i = 0; i < gossips.size(); ++i) {
            final NBTTagCompound gossip = gossips.getCompound(i);
            final String type = gossip.getString("Type");
            final String tuuid = Util.intArrayToString(gossip.getIntArray("Target"));
            final int value = gossip.getInt("Value");
            if (tuuid.equals(puuid)) {
                switch(type) {
                    case "trading": c = value; break;
                    case "minor_positive": b = value; break;
                    case "minor_negative": d = value; break;
                    case "major_positive": a = value; break;
                    case "major_negative": e = value; break;
                    default: break;
                }
            }
        }
        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        for (final MerchantRecipe recipe : recipes) {
            final int x = recipe.getIngredients().get(0).getAmount();
            final float p0 = this.getPriceMultiplier(recipe);
            final int w = 5 * a + b + c - d - 5 * e;
            final float y = x - p0 * w;
            double maxDiscount = instance.getCfg().getDouble("MaxDiscount", 0.3);
            if(overrides != null) {
                for (final String k : overrides.getKeys(false)) {
                    final ConfigurationSection item = this.getItem(recipe, k);
                    if (item != null) {
                        maxDiscount = item.getDouble("MaxDiscount", maxDiscount);
                        break;
                    }
                }
            }
            if(maxDiscount >= 0.0 && maxDiscount <= 1.0) {
                if(y < x * (1.0 - maxDiscount) && y != x) {
                    recipe.setPriceMultiplier(x * (float)maxDiscount / w);
                } else {
                    recipe.setPriceMultiplier(p0);
                }
            } else {
                recipe.setPriceMultiplier(p0);
            }
        }
    }

    private void maxDemand(final Villager villager) {
        List<MerchantRecipe> recipes = villager.getRecipes();
        final NBTContainer vnbt = new NBTContainer(this.nms, villager);
        final NBTTagCompound vtag = vnbt.getTag();
        final NBTTagList recipes2 = new NBTTagList(this.nms, vtag.getCompound("Offers").get("Recipes"));

        final ConfigurationSection overrides = instance.getCfg().getConfigurationSection("Overrides");
        for(int i = 0; i < recipes2.size(); ++i) {
            final NBTTagCompound recipe2 = recipes2.getCompound(i);
            final int demand = recipe2.getInt("demand");
            int maxDemand = instance.getCfg().getInt("MaxDemand", -1);
            if(overrides != null) {
                for(final String k : overrides.getKeys(false)) {
                    final ConfigurationSection item = this.getItem(recipes.get(i), k);
                    if(item != null) {
                        maxDemand = item.getInt("MaxDemand", maxDemand);
                        break;
                    }
                }
            }
            if(maxDemand >= 0 && demand > maxDemand) {
                recipe2.setInt("demand", maxDemand);
            }
        }
        villager.getInventory().clear();
        vnbt.saveTag(villager, vtag);
    }

    private float getPriceMultiplier(final MerchantRecipe recipe) {
        float p = 0.05f;
        final Material type = recipe.getResult().getType();
        for(int length = MATERIALS.length, i = 0; i < length; ++i) {
            if(type == MATERIALS[i]) {
                p = 0.2f;
                break;
            }
        }
        return p;
    }

    private ConfigurationSection getItem(final MerchantRecipe recipe, final String k) {
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
            if(recipe.getResult().getType() != Material.ENCHANTED_BOOK) return null;
            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta)recipe.getResult().getItemMeta();
            final Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(k.substring(0, k.lastIndexOf("_"))));
            if(meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) == level) return item;
            return null;
        } catch(NumberFormatException e) {
            //Return the item if the item name is valid
            if(this.verify(recipe, Material.matchMaterial(k))) return item;
            return null;
        } catch(Exception e2) {
            //Send an error message
            Util.errorMsg(e2);
            return null;
        }
    }

    //Verifies that an item exists in the villager's trade
    private boolean verify(final MerchantRecipe recipe, final Material material) {
        return ((recipe.getResult().getType() == material) || (recipe.getIngredients().get(0).getType() == material));
    }
}
