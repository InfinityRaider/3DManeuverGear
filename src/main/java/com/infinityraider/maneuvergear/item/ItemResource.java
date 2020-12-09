package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemResource extends ItemBase {
    public ItemResource(String name) {
        super(name, new Properties()
                .group(ItemGroup.MISC)
                .maxStackSize(16));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }
}
