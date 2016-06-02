package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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

    @SuppressWarnings("unused")
    public MessageDartAnchored() {}

    public MessageDartAnchored(EntityDart dart, double x, double y, double z, float pitch, float yaw) {
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

    @Override
    public void fromBytes(ByteBuf buf) {
        Entity entity = this.readEntityFromByteBuf(buf);
        if(entity instanceof EntityDart) {
            this.dart = (EntityDart) entity;
        }
        this.cableLength = buf.readDouble();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readFloat();
        this.yaw = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeEntityToByteBuf(buf, this.dart);
        buf.writeDouble(this.cableLength);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.yaw);
    }
}
