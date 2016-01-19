package com.InfinityRaider.maneuvergear.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * Interface used for dual weapon rendering
 */
@SideOnly(Side.CLIENT)
public interface IItemModelRenderer extends IItemRenderer {
    /**
     * Called when this item is rendered
     * @param entity entity
     * @param stack stack holding the item
     * @param left rendered as a left handed weapon or not
     */
    void renderModel(Entity entity, ItemStack stack, boolean left);
}
