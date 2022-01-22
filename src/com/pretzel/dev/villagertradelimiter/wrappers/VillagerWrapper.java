package com.pretzel.dev.villagertradelimiter.wrappers;

import com.pretzel.dev.villagertradelimiter.nms.NBTCompound;
import com.pretzel.dev.villagertradelimiter.nms.NBTCompoundList;
import com.pretzel.dev.villagertradelimiter.nms.NBTEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class VillagerWrapper {
    private final Villager villager;
    private final NBTEntity entity;
    private final ItemStack[] contents;

    /** @param villager The Villager to store in this wrapper */
    public VillagerWrapper(final Villager villager) {
        this.villager = villager;
        this.entity = new NBTEntity(villager);
        this.contents = new ItemStack[villager.getInventory().getContents().length];
        for(int i = 0; i < this.contents.length; i++) {
            ItemStack item = villager.getInventory().getItem(i);
            this.contents[i] = (item == null ? null : item.clone());
        }
    }

    /** @return a list of wrapped recipes for the villager */
    public List<RecipeWrapper> getRecipes() {
        final List<RecipeWrapper> recipes = new ArrayList<>();

        //Add the recipes from the villager's NBT data into a list of wrapped recipes
        final NBTCompound offers = entity.getCompound("Offers");
        if(offers == null) return recipes;
        final NBTCompoundList nbtRecipes = offers.getCompoundList("Recipes");
        for(NBTCompound nbtRecipe : nbtRecipes) {
            recipes.add(new RecipeWrapper(nbtRecipe));
        }
        return recipes;
    }

    /** @return A list of wrapped gossips for the villager */
    private List<GossipWrapper> getGossips() {
        final List<GossipWrapper> gossips = new ArrayList<>();
        if(!entity.hasKey("Gossips")) return gossips;

        //Add the gossips from the villager's NBT data into a list of wrapped gossips
        final NBTCompoundList nbtGossips = entity.getCompoundList("Gossips");
        for(NBTCompound nbtGossip : nbtGossips) {
            gossips.add(new GossipWrapper(nbtGossip));
        }
        return gossips;
    }

    /**
     * @param villager The wrapped villager that contains the gossips
     * @param player The wrapped player that the gossips are about
     * @param isOld Whether the server is older than 1.16 or not. Minecraft changed how UUID's are represented in 1.16
     * @return the total reputation (from gossips) for a player
     */
    public int getTotalReputation(@NonNull final VillagerWrapper villager, @NonNull final PlayerWrapper player, final boolean isOld) {
        int totalReputation = 0;

        final String playerUUID = player.getUUID(isOld);
        final List<GossipWrapper> gossips = villager.getGossips();
        for(GossipWrapper gossip : gossips) {
            final GossipWrapper.GossipType type = gossip.getType();
            if(type == null || type == GossipWrapper.GossipType.OTHER) continue;

            final String targetUUID = gossip.getTargetUUID(isOld);
            if(targetUUID.equals(playerUUID)) {
                totalReputation += gossip.getValue() * type.getWeight();
            }
        }
        return totalReputation;
    }

    /** Resets the villager's NBT data to default */
    public void reset() {
        //Reset the recipes back to their default ingredients, MaxUses, and discounts
        for(RecipeWrapper recipe : this.getRecipes()) {
            recipe.reset();
        }

        this.villager.getInventory().clear();
        this.villager.getInventory().setContents(this.contents);
    }
}
