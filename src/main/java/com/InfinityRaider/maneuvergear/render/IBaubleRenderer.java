package com.InfinityRaider.maneuvergear.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public interface IBaubleRenderer {
    @SideOnly(Side.CLIENT)
    void renderBauble(EntityLivingBase entity, ItemStack stack, float partialRenderTick);
}
