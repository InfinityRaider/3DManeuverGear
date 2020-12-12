package com.infinityraider.maneuvergear.network;

import com.infinityraider.maneuvergear.entity.EntityDart;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDartAnchored extends MessageBase {
    private EntityDart dart;
    private double cableLength;
    private Vector3d pos;
    private float pitch;
    private float yaw;

    public MessageDartAnchored() {}

    public MessageDartAnchored(EntityDart dart, Vector3d pos, double cableLength, float pitch, float yaw) {
        this();
        this.dart = dart;
        this.cableLength = cableLength;
        this.pos = pos;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.dart != null) {
            this.dart.setCableLength(this.cableLength);
            DartHandler.instance.onDartAnchored(this.dart, this.pos, this.cableLength, this.pitch, this.yaw);
        }
    }
}
