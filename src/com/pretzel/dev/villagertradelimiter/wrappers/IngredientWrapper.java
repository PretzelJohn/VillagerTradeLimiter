package com.pretzel.dev.villagertradelimiter.wrappers;

import com.pretzel.dev.villagertradelimiter.nms.NBTCompound;

public class IngredientWrapper {
    private final NBTCompound ingredient;
    private final String materialId;
    private final int amount;

    /** @param ingredient The NBTCompound that contains the recipe's NBT data of the ingredient */
    public IngredientWrapper(final NBTCompound ingredient) {
        this.ingredient = ingredient;
        this.materialId = getMaterialId();
        this.amount = getAmount();
    }

    /** @return The ingredient's material id (e.g, minecraft:enchanted_book) */
    public String getMaterialId() { return ingredient.getString("id"); }

    /** @return The number of items in the ingredient stack, between 1 and 64 */
    public int getAmount() { return ingredient.getByte("Count").intValue(); }


    /** @param id The ingredient's material id (e.g, minecraft:enchanted_book) */
    public void setMaterialId(final String id) { this.ingredient.setString("id", id); }

    /** @param amount The number of items in the ingredient stack, which is clamped between 1 and 64 by this function */
    public void setAmount(int amount) { this.ingredient.setByte("Count", (byte)Math.max(Math.min(amount, 64), 1)); }

    /** Resets the material ID and the amount of this ingredient to default values */
    public void reset() {
        setMaterialId(this.materialId);
        setAmount(this.amount);
    }
}
