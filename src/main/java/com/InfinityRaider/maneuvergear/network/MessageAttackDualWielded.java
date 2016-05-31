package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageAttackDualWielded extends MessageBase<IMessage> {
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

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if(player != null) {
            ItemStack stack = player.getHeldItem(left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if(stack != null && stack.getItem() instanceof IDualWieldedWeapon) {
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                weapon.onItemAttack(stack, player, entity, shift, ctrl, left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
