package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageBoostUsed extends MessageBase {
    public MessageBoostUsed() {}

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        Player player = ctx.getSender();
        if (player != null) {
            new MessageSpawnSteamParticles(player).sendToAllAround(
                    player.getLevel().dimension(), player.getX(), player.getY(), player.getZ(), 64);
        }
    }
}
