package com.InfinityRaider.maneuvergear.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageBoostUsed extends MessageBase {
    public MessageBoostUsed() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class MessageHandler implements IMessageHandler<MessageBoostUsed, IMessage> {
        @Override
        public IMessage onMessage(MessageBoostUsed message, MessageContext ctx) {
            if(ctx.side == Side.SERVER) {
                EntityPlayer player = ctx.getServerHandler().playerEntity;
                if(player != null) {
                    IMessage msg = new MessageSpawnSteamParticles(player);
                    NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64);
                    NetworkWrapperManeuverGear.wrapper.sendToAllAround(msg, point);
                }
            }
            return null;
        }
    }
}
