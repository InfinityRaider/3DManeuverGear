package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSpawnSteamParticles extends MessageBase<IMessage> {
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

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(player != null) {
            ManeuverGear.proxy.spawnSteamParticles(player);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
