package com.infinityraider.maneuvergear.utility;

import com.infinityraider.maneuvergear.compat.CuriosCompat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ManeuverGearHelper {

    public static ICapabilityProvider getCapability(ItemStack stack) {
        return CuriosCompat.getCurioCapability(stack);
    }

    public static ItemStack findManeuverGear(PlayerEntity player) {
        return CuriosCompat.findManeuverGear(player);
    }
}
