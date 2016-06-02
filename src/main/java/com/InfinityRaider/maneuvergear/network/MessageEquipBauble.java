package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.utility.BaublesWrapper;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageEquipBauble extends MessageBase<IMessage> {
    EnumHand hand;

    @SuppressWarnings("unused")
    public MessageEquipBauble() {}

    public MessageEquipBauble(EnumHand hand) {
        this.hand = hand;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack stack = player.getHeldItem(this.hand);
            if(stack != null) {
                IInventory baubles = BaublesWrapper.getInstance().getBaubles(player);
                ItemStack belt = baubles.getStackInSlot(BaublesWrapper.BELT_SLOT);
                belt = belt == null ? null : belt.copy();
                baubles.setInventorySlotContents(BaublesWrapper.BELT_SLOT, stack.copy());
                player.inventory.setInventorySlotContents(player.inventory.currentItem, belt);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.hand.ordinal());
    }
}
