package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.item.IBaubleRendered;
import com.InfinityRaider.maneuvergear.utility.BaublesWrapper;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class MessageRequestBaubles extends MessageBase<MessageSyncBaubles> {
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

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {}

    @Override
    protected MessageSyncBaubles getReply(MessageContext ctx) {
        IInventory baubleInv = BaublesWrapper.getInstance().getBaubles(this.subject);
        ArrayList<ItemStack> baubles = new ArrayList<>();
        if(baubleInv != null) {
            for(int i=0;i<baubleInv.getSizeInventory();i++) {
                ItemStack bauble = baubleInv.getStackInSlot(i);
                if((bauble!=null) && (bauble.getItem() instanceof IBaubleRendered)) {
                    baubles.add(bauble);
                }
            }
        }
        return new MessageSyncBaubles(this.subject, baubles);
    }
}
