package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.render.RenderBauble;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MessageNotifyBaubleEquip extends MessageBase {
    private EntityPlayer player;
    private ItemStack bauble;
    private boolean equip;

    @SuppressWarnings("unused")
    public MessageNotifyBaubleEquip() {}

    public MessageNotifyBaubleEquip(EntityPlayer player, ItemStack stack, boolean equip) {
        this.player = player;
        this.bauble = stack;
        this.equip = equip;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
        this.bauble = this.readItemStackFromByteBuf(buf);
        this.equip = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, player);
        this.writeItemStackToByteBuf(buf, bauble);
        buf.writeBoolean(equip);
    }

    public static class MessageHandler implements IMessageHandler<MessageNotifyBaubleEquip, IMessage> {
        @Override
        public IMessage onMessage(MessageNotifyBaubleEquip message, MessageContext ctx) {
            if(message.equip) {
                RenderBauble.getInstance().equipBauble(message.player, message.bauble);
            } else {
                RenderBauble.getInstance().unequipBauble(message.player, message.bauble);
            }
            return null;
        }
    }
}
