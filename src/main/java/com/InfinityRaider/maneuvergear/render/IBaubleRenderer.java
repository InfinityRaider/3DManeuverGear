package com.InfinityRaider.maneuvergear.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBaubleRenderer {
    @SideOnly(Side.CLIENT)
    void renderBauble(EntityLivingBase entity, ItemStack stack, float partialRenderTick);
}
