package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ItemRecord extends net.minecraft.item.ItemRecord implements IItemWithModel {
    private final static String name = "GurenNoYumiya";

    public ItemRecord() {
        super(Reference.MOD_ID.toLowerCase()+":"+name, null);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase()+":records."+ItemRecord.name);
    }

    @Override
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":record", "inventory")));
        return list;
    }
}
