package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAttackDualWielded extends MessageBase {
    private boolean left;
    private boolean shift;
    private boolean ctrl;
    private Entity entity;

    @SuppressWarnings("unused")
    public MessageAttackDualWielded() {}

    public MessageAttackDualWielded(Entity entity, boolean left, boolean shift, boolean ctrl) {
        this.left = left;
        this.shift = shift;
        this.ctrl = ctrl;
        this.entity = entity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        left = buf.readBoolean();
        shift = buf.readBoolean();
        ctrl = buf.readBoolean();
        entity = this.readEntityFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(left);
        buf.writeBoolean(shift);
        buf.writeBoolean(ctrl);
        writeEntityToByteBuf(buf, entity);
    }

    public static class MessageHandler implements IMessageHandler<MessageAttackDualWielded, IMessage> {
        @Override
        public IMessage onMessage(MessageAttackDualWielded message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if(player != null) {
                ItemStack stack = player.getHeldItem(message.left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                if(stack != null && stack.getItem() != null && stack.getItem() instanceof IDualWieldedWeapon) {
                    IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                    if(message.left) {
                        weapon.onLeftItemAttack(stack, player, message.entity, message.shift, message.ctrl);
                    } else {
                        weapon.onRightItemAttack(stack, player, message.entity, message.shift, message.ctrl);
                    }
                }
            }
            return null;
        }
    }
}
