package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.handler.SwingLeftHandHandler;
import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageMouseButtonPressed extends MessageBase {
    private boolean left;
    private boolean shift;
    private boolean ctrl;

    @SuppressWarnings("unused")
    public MessageMouseButtonPressed() {}

    public MessageMouseButtonPressed(boolean left, boolean shift, boolean ctrl) {
        this.left = left;
        this.shift = shift;
        this.ctrl = ctrl;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.left = buf.readBoolean();
        this.shift = buf.readBoolean();
        this.ctrl = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(left);
        buf.writeBoolean(shift);
        buf.writeBoolean(ctrl);
    }

    public static class MessageHandler implements IMessageHandler<MessageMouseButtonPressed, IMessage> {
        @Override
        public IMessage onMessage(MessageMouseButtonPressed message, MessageContext ctx) {
            if(ctx.side == Side.SERVER) {
                EntityPlayer player = ctx.getServerHandler().playerEntity;
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null && stack.getItem() != null && stack.getItem() instanceof IDualWieldedWeapon) {
                    IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                    if (message.left) {
                        SwingLeftHandHandler.getInstance().onLeftWeaponSwing(player);
                        NetworkWrapperManeuverGear.wrapper.sendToAll(new MessageSwingLeftWeapon(player));
                        weapon.onLeftItemUsed(stack, player, message.shift, message.ctrl);
                    } else {
                        weapon.onRightItemUsed(stack, player, message.shift, message.ctrl);
                    }
                }
            }
            return null;
        }
    }
}
