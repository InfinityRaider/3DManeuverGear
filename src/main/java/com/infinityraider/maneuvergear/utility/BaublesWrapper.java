package com.infinityraider.maneuvergear.utility;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BaublesWrapper {
    private static final BaublesWrapper INSTANCE = new BaublesWrapper();

    public static final int BELT_SLOT = 3;

    public static BaublesWrapper getInstance() {
        return INSTANCE;
    }

    private BaublesWrapper() {}

    public IInventory getBaubles(EntityPlayer player) {
        return BaublesApi.getBaubles(player);
    }

    public ItemStack getBauble(EntityPlayer player, int slot) {
        IInventory inventory = getBaubles(player);
        if(slot < 0 || slot >= inventory.getSizeInventory()) {
            return null;
        }
        return BaublesApi.getBaubles(player).getStackInSlot(slot);
    }
}
