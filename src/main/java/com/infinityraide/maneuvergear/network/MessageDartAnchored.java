package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageDartAnchored extends MessageBase<IMessage> {
    private EntityDart dart;
    private double cableLength;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public MessageDartAnchored() {}

    public MessageDartAnchored(EntityDart dart, double x, double y, double z, float pitch, float yaw) {
        this();
        this.dart = dart;
        this.cableLength = dart.getCableLength();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(this.dart != null) {
            this.dart.setCableLength(this.cableLength);
            DartHandler.instance.onDartAnchored(this.dart, this.x, this.y, this.z, this.pitch, this.yaw);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
