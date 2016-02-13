package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.handler.SwingLeftHandHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSwingLeftWeapon extends MessageBase {
    private EntityPlayer player;

    @SuppressWarnings("unused")
    public MessageSwingLeftWeapon() {}

    public MessageSwingLeftWeapon(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = readPlayerFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, player);
    }

    public static class MessageHandler implements IMessageHandler<MessageSwingLeftWeapon, IMessage> {
        @Override
        public IMessage onMessage(MessageSwingLeftWeapon message, MessageContext ctx) {
            if(ctx.side == Side.CLIENT) {
                SwingLeftHandHandler.getInstance().onLeftWeaponSwing(message.player);
            }
            return null;
        }
    }
}
