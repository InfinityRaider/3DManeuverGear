package com.InfinityRaider.maneuvergear.render;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface used for dual weapon rendering
 */
@SideOnly(Side.CLIENT)
public interface IItemModelRenderer {
    /**
     * Called when this item is rendered
     * @param entity entity
     * @param stack stack holding the item
     * @param left rendered as a left handed weapon or not
     */
    void renderModel(Entity entity, ItemStack stack, boolean left);
}
