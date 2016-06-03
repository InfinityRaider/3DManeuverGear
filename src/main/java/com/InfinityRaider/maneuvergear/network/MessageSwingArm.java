package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.handler.ArmSwingHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSwingArm extends MessageBase<IMessage> {
    private EntityPlayer player;
    private EnumHand hand;

    @SuppressWarnings("unused")
    public MessageSwingArm() {}

    public MessageSwingArm(EntityPlayer player, EnumHand hand) {
        this.player = player;
        this.hand = hand;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.CLIENT && this.player != null) {
            ArmSwingHandler.getInstance().swingArm(this.player, this.hand);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
        this.hand = EnumHand.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, this.player);
        buf.writeInt(this.hand.ordinal());
    }
}
