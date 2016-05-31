package com.InfinityRaider.maneuvergear.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageBoostUsed extends MessageBase<IMessage> {
    public MessageBoostUsed() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if(player != null) {
                NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(player.worldObj.provider.getDimension(), player.posX, player.posY, player.posZ, 64);
                NetworkWrapper.getInstance().sendToAllAround(new MessageSpawnSteamParticles(player), point);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
