package com.InfinityRaider.maneuvergear.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IItemWithModel {
    /**
     * @return a list with metadata values and ModelResourceLocations corresponding with it.
     */
    @SideOnly(Side.CLIENT)
    List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions();
}
