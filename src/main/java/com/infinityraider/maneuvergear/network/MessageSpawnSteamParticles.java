package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSpawnSteamParticles extends MessageBase {
    private PlayerEntity player;

    public MessageSpawnSteamParticles() {}

    public MessageSpawnSteamParticles(PlayerEntity player) {
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
            double x = player.getPosX();
            double y = player.getPosY();
            double z = player.getPosZ();
            Vector3d lookVec = player.getLookVec();
            int nr = 10;
            for(int i=0;i<nr;i++) {
                player.getEntityWorld().addParticle(
                        ParticleTypes.CLOUD, x, y, z,
                        -(lookVec.getX() * i) / (0.0F + nr),
                        -(lookVec.getY() * i) / (0.0F + nr),
                        -(lookVec.getZ() * i) / (0.0F + nr));
            }
        }
    }
}
