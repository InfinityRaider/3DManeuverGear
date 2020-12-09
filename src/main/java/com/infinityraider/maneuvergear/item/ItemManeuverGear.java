package com.infinityraider.maneuvergear.item;

import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.network.MessageEquipManeuverGear;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.item.ItemBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemManeuverGear extends ItemBase {
    public static int MAX_HOLSTERED_BLADES = 4;

    public ItemManeuverGear() {
        super(Names.Items.MANEUVER_GEAR, new Properties()
                .group(ItemGroup.COMBAT)
                .maxStackSize(1));
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if(world.isRemote) {
            new MessageEquipManeuverGear(hand).sendToServer();
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    /**
     * Checks the number of blades left in the holster
     * @param stack the stack containing this
     * @param left check left or right holster
     * @return the amount of blades left in the holster
     */
    public int getBladeCount(ItemStack stack, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            return 0;
        }
        return left ? tag.getInt(Names.NBT.LEFT) : tag.getInt(Names.NBT.RIGHT);
    }

    /**
     * Tries to adds blades to the holster
     * @param stack the stack containing this
     * @param amount the amount to add
     * @param left add blades to left or right holster
     * @return the amount of blades left after adding to the holster
     */
    public int addBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return amount;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        int current = getBladeCount(stack, left);
        if(left) {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.putInt(Names.NBT.LEFT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.putInt(Names.NBT.LEFT, current + amount);
                return 0;
            }
        } else {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.putInt(Names.NBT.RIGHT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.putInt(Names.NBT.RIGHT, current + amount);
                return 0;
            }
        }
    }

    /**
     * Tries to retrieve blades from the holster
     * @param stack the stack containing this
     * @param amount the amount to retrieve
     * @param left retrieve blades from left or right holster
     * @return the amount of blades retrieved from the holster
     */
    public int removeBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            return 0;
        }
        int current = getBladeCount(stack, left);
        if(left) {
            if(amount >= current) {
                tag.putInt(Names.NBT.LEFT, 0);
                return current;
            } else {
                tag.putInt(Names.NBT.LEFT, current - amount);
                return amount;
            }
        } else {
            if(amount >= current) {
                tag.putInt(Names.NBT.RIGHT, 0);
                return current;
            } else {
                tag.putInt(Names.NBT.RIGHT, current - amount);
                return amount;
            }
        }

    }

    /**
     * @return maximum number of holstered blades per holster
     */
    public int getMaxBlades() {
        return MAX_HOLSTERED_BLADES;
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGear;
    }

    //TODO: port baubles behaviour to curios

    public void onWornTick(ItemStack stack, LivingEntity entity) {
        if(entity==null || !(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        boolean remote = player.getEntityWorld().isRemote;
        if(remote && stack!=null && stack.getItem()==this) {
            if(DartHandler.instance.isWearingGear(player)
                    && (DartHandler.instance.getLeftDart(player)!=null || DartHandler.instance.getRightDart(player)!=null)) {

                PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(player);
                engine.updateTick();
            }
        }
    }

    public void onEquipped(ItemStack stack, LivingEntity entity) {
        if(entity==null || !(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.equipGear(player);
        }
    }

    public void onUnequipped(ItemStack stack, LivingEntity entity) {
        if(entity==null || !(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.unEquipGear(player);
        }
    }

    public boolean canEquip(ItemStack stack, LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    public boolean canUnequip(ItemStack stack, LivingEntity entityLivingBase) {
        return true;
    }

    public boolean willAutoSync(ItemStack stack, LivingEntity player) {
        return true;
    }

    public void onPlayerBaubleRender(ItemStack stack, PlayerEntity entityPlayer, RenderType renderType, float partialTick) {
        //RenderManeuverGear.instance.renderManeuverGear(entityPlayer, stack, renderType, partialTick);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.left_blades")
                .append(new StringTextComponent(": " + this.getBladeCount(stack, true) + "/" + MAX_HOLSTERED_BLADES)));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.right_blades")
                .append(new StringTextComponent(": " + this.getBladeCount(stack, false) + "/" + MAX_HOLSTERED_BLADES)));
    }
}