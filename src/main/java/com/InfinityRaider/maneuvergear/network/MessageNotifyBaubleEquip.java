package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.render.RenderBauble;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageNotifyBaubleEquip extends MessageBase<IMessage> {
    private EntityPlayer player;
    private ItemStack bauble;
    private boolean equip;

    public MessageNotifyBaubleEquip() {}

    public MessageNotifyBaubleEquip(EntityPlayer player, ItemStack stack, boolean equip) {
        this();
        this.player = player;
        this.bauble = stack;
        this.equip = equip;
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
