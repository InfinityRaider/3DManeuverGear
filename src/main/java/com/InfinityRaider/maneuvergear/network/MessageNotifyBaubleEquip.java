package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.render.RenderBauble;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageNotifyBaubleEquip extends MessageBase<IMessage> {
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

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(equip) {
            RenderBauble.getInstance().equipBauble(player, bauble);
        } else {
            RenderBauble.getInstance().unequipBauble(player, bauble);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
