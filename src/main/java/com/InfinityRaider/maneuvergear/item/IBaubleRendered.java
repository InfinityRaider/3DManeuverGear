package com.InfinityRaider.maneuvergear.item;

import baubles.api.IBauble;
import com.InfinityRaider.maneuvergear.render.IBaubleRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

public interface IBaubleRendered extends IBauble {
    @SideOnly(Side.CLIENT)
    IBaubleRenderer getRenderer(ItemStack stack);
}
