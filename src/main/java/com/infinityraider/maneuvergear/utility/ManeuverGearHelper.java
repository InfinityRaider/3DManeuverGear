package com.infinityraider.maneuvergear.utility;

import com.infinityraider.maneuvergear.compat.CuriosCompat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ManeuverGearHelper {
    public static ItemStack findManeuverGear(PlayerEntity player) {
        return CuriosCompat.findManeuverGear(player);
    }
}
