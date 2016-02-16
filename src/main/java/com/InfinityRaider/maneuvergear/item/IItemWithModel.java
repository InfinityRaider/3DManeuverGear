package com.InfinityRaider.maneuvergear.item;

import net.minecraft.client.resources.model.ModelResourceLocation;

public interface IItemWithModel {
    /**
     * @return an array with ModelResourceLocations with the index corresponding the the item's meta data for that model.
     */
    ModelResourceLocation[] getModelDefinitions();
}
