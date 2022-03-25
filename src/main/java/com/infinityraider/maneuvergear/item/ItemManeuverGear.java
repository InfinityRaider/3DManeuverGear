package com.infinityraider.maneuvergear.item;

import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.maneuvergear.render.RenderManeuverGear;
import com.infinityraider.maneuvergear.utility.IManeuverGear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemManeuverGear extends ItemBase implements IManeuverGear {
    public static int MAX_HOLSTERED_BLADES = 4;

    public ItemManeuverGear() {
        super(Names.Items.MANEUVER_GEAR, new Properties()
                .group(ItemGroup.COMBAT)
                .maxStackSize(1));
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.left_blades")
                .appendSibling(new StringTextComponent(": " + this.getBladeCount(stack, true) + "/" + MAX_HOLSTERED_BLADES)));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.right_blades")
                .appendSibling(new StringTextComponent(": " + this.getBladeCount(stack, false) + "/" + MAX_HOLSTERED_BLADES)));
    }

    @Override
    public void onWornTick(LivingEntity entity) {
        if (entity instanceof Player) {
            DartHandler.instance.getPhysicsEngine((Player) entity).updateTick();
        }
    }

    @Override
    public void onEquip(LivingEntity entity) {
        if(entity instanceof Player) {
            DartHandler.instance.equipGear((Player) entity);
        }
    }

    @Override
    public void onUnequip(LivingEntity entity) {
        if(entity instanceof Player) {
            DartHandler.instance.unEquipGear((Player) entity);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(ItemStack stack, LivingEntity entity, PoseStack transforms, MultiBufferSource buffer, int light, float partialTicks) {
        RenderManeuverGear.getInstance().render(stack, entity, transforms, buffer, light, partialTicks);
    }
}
