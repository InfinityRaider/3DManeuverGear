package com.InfinityRaider.maneuvergear.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IItemWithModel {
    /**
     * @return an array with ModelResourceLocations with the index corresponding the the item's meta data for that model.
     */
    ModelResourceLocation[] getModelDefinitions();
}
