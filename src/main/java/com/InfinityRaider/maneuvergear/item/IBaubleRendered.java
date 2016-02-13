package com.InfinityRaider.maneuvergear.item;

import baubles.api.IBauble;
import com.InfinityRaider.maneuvergear.render.IBaubleRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBaubleRendered extends IBauble {
    @SideOnly(Side.CLIENT)
    IBaubleRenderer getRenderer(ItemStack stack);
}
