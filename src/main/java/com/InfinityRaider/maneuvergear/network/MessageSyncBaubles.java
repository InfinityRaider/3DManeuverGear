package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.render.RenderBauble;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class MessageSyncBaubles extends MessageBase<IMessage> {
    private EntityPlayer player;
    private ItemStack[] baubles;

    @SuppressWarnings("unused")
    public MessageSyncBaubles() {}

    public MessageSyncBaubles(EntityPlayer player, ArrayList<ItemStack> baubles) {
        this.player = player;
        this.baubles = baubles.toArray(new ItemStack[baubles.size()]);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
        this.baubles = new ItemStack[buf.readInt()];
        for(int i=0;i<baubles.length;i++) {
            this.baubles[i] = this.readItemStackFromByteBuf(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, this.player);
        buf.writeInt(this.baubles.length);
        for(ItemStack stack:baubles) {
            this.writeItemStackToByteBuf(buf, stack);
        }
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        RenderBauble.getInstance().syncBaubles(this.player, this.baubles);
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
