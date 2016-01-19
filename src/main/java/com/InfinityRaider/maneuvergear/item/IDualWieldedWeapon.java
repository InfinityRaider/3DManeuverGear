package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.render.IItemModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Interface implemented in Item classes which should be dual wielding weapons,
 * If you have this implemented in a class onItemUseFirst() and onItemRightClick() will no longer be called,
 * instead onLeftItemUsed() will be used if the player left clicks and onRightItemUsed() will be called if the player right clicks.
 * This is done automatically via event handling and the methods will be called on the client and on the server.
 *
 * There will also be a weapon rendered in the left hand, both in first person and third person,
 * the appropriate coordinate transformations are automatically taken care of, there is still the possibility to scale, translate and rotate the
 * item after this.
 */
public interface IDualWieldedWeapon {
    /**
     * Called when the player uses the left weapon
     * @param stack stack with this item
     * @param player Player using this item
     * @param shift if shift was held
     * @param ctrl if ctrl was held
     */
    void onLeftItemUsed(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl);

    /**
     * Called when the player uses the right weapon
     * @param stack stack with this item
     * @param player Player using this item
     * @param shift if shift was held
     * @param ctrl if ctrl was held
     */
    void onRightItemUsed(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl);

    /**
     * Called when an entity is attacked with the left weapon, can only be cancelled client side.
     * If cancelled client side this will not be called server side.
     * If not cancelled client side, returning true server side will not cancel the attack
     *
     * @param stack ItemStack holding this item
     * @param player attacking player
     * @param e attacked entity
     * @param shift if shift was held
     * @param ctrl if ctrl was held
     * @return if the attack should be cancelled
     */
    boolean onLeftItemAttack(ItemStack stack, EntityPlayer player, Entity e, boolean shift, boolean ctrl);

    /**
     * Called when an entity is attacked with the right weapon, can only be cancelled client side.
     * If cancelled client side this will not be called server side.
     * If not cancelled client side, returning true server side will not cancel the attack
     *
     * @param stack ItemStack holding this item
     * @param player attacking player
     * @param e attacked entity
     * @param shift if shift was held
     * @param ctrl if ctrl was held
     * @return if the attack should be cancelled
     */
    boolean onRightItemAttack(ItemStack stack, EntityPlayer player, Entity e, boolean shift, boolean ctrl);

    /**
     * Needed values to translate, rotate and scale the model to the correct position
     *
     * @param player the player wielding the Item
     * @param stack ItemStack containing the Item being rendered
     * @param partialTick partial render tick, used for interpolation
     * @param firstPerson if the item is being rendered in first person or third person
     * @return an array containing the following values, in this order: scale, dx, dy, dz, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    float[] getTransformationComponents(EntityPlayer player, ItemStack stack, float partialTick, boolean firstPerson);

    /**
     * Determines if the dual wielded weapon should be rendered as a model or as an icon
     * @param stack the ItemStack holding the Item being rendered
     * @return if the stack should be rendered using a model
     */
    @SideOnly(Side.CLIENT)
    boolean useModel(ItemStack stack);

    /**
     * Method used to get the model to render this stack, is only called if useModel(ItemStack stack) returns true
     * @param stack the ItemStack holding the Item being rendered
     * @return the model for this item
     */
    @SideOnly(Side.CLIENT)
    IItemModelRenderer getModel(ItemStack stack);

    /**
     * Method used to get the model to render this stack, is only called if useModel(ItemStack stack) returns false
     * @param stack the ItemStack holding the Item being rendered
     * @return the IIcon for this item
     */
    @SideOnly(Side.CLIENT)
    IIcon getIcon(ItemStack stack);
}
