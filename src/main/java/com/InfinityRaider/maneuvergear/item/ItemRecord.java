package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Reference;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class ItemRecord extends net.minecraft.item.ItemRecord implements IItemWithModel {
    private final static String name = "GurenNoYumiya";

    public ItemRecord() {
        super(Reference.MOD_ID.toLowerCase()+":"+name);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase()+":records."+ItemRecord.name);
    }

    @Override
    public ModelResourceLocation[] getModelDefinitions() {
        return new ModelResourceLocation[] {new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":record", "inventory")};
    }
}
