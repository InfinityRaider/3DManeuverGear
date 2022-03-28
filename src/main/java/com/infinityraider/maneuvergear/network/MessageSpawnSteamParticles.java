package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSpawnSteamParticles extends MessageBase {
    private Player player;

    public MessageSpawnSteamParticles() {}

    public MessageSpawnSteamParticles(Player player) {
        this();
        this.player = player;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.player != null) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            Vec3 lookVec = player.getLookAngle();
            int nr = 10;
            for(int i=0;i<nr;i++) {
                player.getLevel().addParticle(
                        ParticleTypes.CLOUD, x, y, z,
                        -(lookVec.x() * i) / (0.0F + nr),
                        -(lookVec.y() * i) / (0.0F + nr),
                        -(lookVec.z() * i) / (0.0F + nr));
            }
        }
    }
}
