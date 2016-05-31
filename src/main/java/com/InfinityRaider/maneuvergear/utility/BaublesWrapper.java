package com.InfinityRaider.maneuvergear.utility;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class BaublesWrapper {
    private static final BaublesWrapper INSTANCE = new BaublesWrapper();

    public static BaublesWrapper getInstance() {
        return INSTANCE;
    }

    private BaublesWrapper() {}

    public IInventory getBaubles(EntityPlayer player) {
        return BaublesApi.getBaubles(player);
    }
}
