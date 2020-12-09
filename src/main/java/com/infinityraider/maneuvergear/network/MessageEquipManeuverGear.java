package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.utility.ExtendedInventoryHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageEquipManeuverGear extends MessageBase {
    Hand hand;

    public MessageEquipManeuverGear() {}

    public MessageEquipManeuverGear(Hand hand) {
        this();
        this.hand = hand;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if(player != null) {
            ItemStack stack = player.getHeldItem(this.hand);
            if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemManeuverGear) {
                ItemStack currentBelt = ExtendedInventoryHelper.getStackInBeltSlot(player);
                currentBelt = currentBelt == null ? ItemStack.EMPTY : currentBelt.copy();
                ExtendedInventoryHelper.setStackInBeltSlot(player, stack.copy());
                player.inventory.setInventorySlotContents(player.inventory.currentItem, currentBelt);
            }
        }
        new MessageManeuverGearEquipped().sendTo(player);
    }
}
