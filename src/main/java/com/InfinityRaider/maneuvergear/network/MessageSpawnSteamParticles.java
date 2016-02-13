package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSpawnSteamParticles extends MessageBase {
    private EntityPlayer player;

    @SuppressWarnings("unused")
    public MessageSpawnSteamParticles() {}

    public MessageSpawnSteamParticles(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, player);
    }

    public static class MessageHandler implements IMessageHandler<MessageSpawnSteamParticles, IMessage> {
        @Override
        public IMessage onMessage(MessageSpawnSteamParticles message, MessageContext ctx) {
            if(message.player != null) {
                ManeuverGear.proxy.spawnSteamParticles(message.player);
            }
            return null;
        }
    }
}
