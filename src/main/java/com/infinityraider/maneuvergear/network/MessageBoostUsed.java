package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageBoostUsed extends MessageBase {
    public MessageBoostUsed() {}

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        PlayerEntity player = ctx.getSender();
        if (player != null) {
            new MessageSpawnSteamParticles(player).sendToAllAround(
                    player.getEntityWorld().getDimensionKey(), player.getPosX(), player.getPosY(), player.getPosZ(), 64);
        }
    }
}
