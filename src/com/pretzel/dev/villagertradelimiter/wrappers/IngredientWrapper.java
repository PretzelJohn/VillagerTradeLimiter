package com.pretzel.dev.villagertradelimiter.wrappers;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.inventory.ItemStack;

public class IngredientWrapper {
    private final NBTCompound recipe;
    private final String key;
    private final ItemStack itemStack;

    /**
     * @param recipe The NBTCompound that contains the recipe's NBT data of the ingredient
     * @param key The key under which the recipe is located
     */
    public IngredientWrapper(final NBTCompound recipe, final String key) {
        this.recipe = recipe;
        this.key = key;
        this.itemStack = getItemStack();
    }

    /** @return The {@link ItemStack} representing the data in the recipe */
    public ItemStack getItemStack() {
        return recipe.getItemStack(key);
    }

    /** @param itemStack The {@link ItemStack} which will replace the item in the recipe */
    public void setItemStack(final ItemStack itemStack) {
        recipe.setItemStack(key, itemStack);
    }

    /** Resets the material ID and the amount of this ingredient to default values */
    public void reset() {
        setItemStack(itemStack);
    }
}
