package com.infinityraider.maneuvergear.utility;

import com.infinityraider.maneuvergear.compat.CuriosCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManeuverGearHelper {
    public static ItemStack findManeuverGear(Player player) {
        return CuriosCompat.findManeuverGear(player);
    }
}
