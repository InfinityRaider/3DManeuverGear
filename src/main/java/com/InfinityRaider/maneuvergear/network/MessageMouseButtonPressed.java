package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageMouseButtonPressed extends MessageBase<IMessage> {
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

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            NetworkWrapper.getInstance().sendToAll(new MessageSwingArm(player, left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            ItemStack stack = player.getHeldItem(left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (stack != null && stack.getItem() instanceof IDualWieldedWeapon) {
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                weapon.onItemUsed(stack, player, shift, ctrl, left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
