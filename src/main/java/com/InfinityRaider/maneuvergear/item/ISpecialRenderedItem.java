package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.render.ItemSpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISpecialRenderedItem extends IItemWithModel {
    @SideOnly(Side.CLIENT)
    ItemSpecialRenderer getSpecialRenderer();
}
