package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemResource extends ItemBase {
    public ItemResource(String name) {
        super(name, new Properties()
                .tab(CreativeModeTab.TAB_MISC)
                .stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }
}
