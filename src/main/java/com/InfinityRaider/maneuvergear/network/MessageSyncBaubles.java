package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.render.RenderBauble;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class MessageSyncBaubles extends MessageBase<IMessage> {
    private EntityPlayer player;
    private ItemStack[] baubles;

    public MessageSyncBaubles() {}

    public MessageSyncBaubles(EntityPlayer player, ArrayList<ItemStack> baubles) {
        this();
        this.player = player;
        this.baubles = baubles.toArray(new ItemStack[baubles.size()]);
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
