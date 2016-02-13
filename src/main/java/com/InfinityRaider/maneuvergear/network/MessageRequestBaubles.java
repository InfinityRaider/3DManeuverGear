package com.InfinityRaider.maneuvergear.network;

import baubles.api.BaublesApi;
import com.InfinityRaider.maneuvergear.item.IBaubleRendered;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

public class MessageRequestBaubles extends MessageBase {
    private EntityPlayer subject;

    @SuppressWarnings("unused")
    public MessageRequestBaubles() {}

    public MessageRequestBaubles(EntityPlayer subject) {
        this.subject = subject;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.subject = readPlayerFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, subject);
    }

    public static class MessageHandler implements IMessageHandler<MessageRequestBaubles, MessageSyncBaubles> {
        @Override
        public MessageSyncBaubles onMessage(MessageRequestBaubles message, MessageContext ctx) {
            IInventory baubleInv = BaublesApi.getBaubles(message.subject);
            ArrayList<ItemStack> baubles = new ArrayList<ItemStack>();
            if(baubleInv != null) {
                for(int i=0;i<baubleInv.getSizeInventory();i++) {
                    ItemStack bauble = baubleInv.getStackInSlot(i);
                    if((bauble!=null) && (bauble.getItem()!=null) && (bauble.getItem() instanceof IBaubleRendered)) {
                        baubles.add(bauble);
                    }
                }
            }
            return new MessageSyncBaubles(message.subject, baubles);
        }
    }
}
