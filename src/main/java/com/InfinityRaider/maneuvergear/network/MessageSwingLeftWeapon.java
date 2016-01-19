package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.handler.SwingLeftHandHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

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
