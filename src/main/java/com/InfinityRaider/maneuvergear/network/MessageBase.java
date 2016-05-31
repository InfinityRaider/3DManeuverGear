package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract  class MessageBase<REPLY extends IMessage> implements IMessage {
    public abstract Side getMessageHandlerSide();

    protected abstract void processMessage(MessageContext ctx);

    protected abstract REPLY getReply(MessageContext ctx);

    protected EntityPlayer readPlayerFromByteBuf(ByteBuf buf) {
        Entity entity = readEntityFromByteBuf(buf);
        return (entity instanceof EntityPlayer)?(EntityPlayer) entity:null;
    }

    protected void writePlayerToByteBuf(ByteBuf buf, EntityPlayer player) {
        writeEntityToByteBuf(buf, player);
    }

    protected Entity readEntityFromByteBuf(ByteBuf buf) {
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        int dimension = buf.readInt();
        return ManeuverGear.proxy.getEntityById(dimension, id);
    }

    protected void writeEntityToByteBuf(ByteBuf buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
            buf.writeInt(0);
        } else {
            buf.writeInt(e.getEntityId());
            buf.writeInt(e.worldObj.provider.getDimension());
        }
    }

    protected Item readItemFromByteBuf(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return Item.REGISTRY.getObject(new ResourceLocation(itemName));
    }

    protected void writeItemToByteBuf(Item item, ByteBuf buf) {
        String itemName = item==null?"null":Item.REGISTRY.getNameForObject(item).toString();
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
    }

    protected ItemStack readItemStackFromByteBuf(ByteBuf buf) {
        Item item = this.readItemFromByteBuf(buf);
        int meta = buf.readInt();
        int amount = buf.readInt();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if(item == null) {
            return null;
        }
        ItemStack stack = new ItemStack(item, amount, meta);
        stack.setTagCompound(tag);
        return stack;
    }

    protected void writeItemStackToByteBuf(ByteBuf buf, ItemStack stack) {
        this.writeItemToByteBuf(stack.getItem(), buf);
        buf.writeInt(stack.getItemDamage());
        buf.writeInt(stack.stackSize);
        NBTTagCompound tag = stack.getTagCompound();
        ByteBufUtils.writeTag(buf, tag);
    }
}
