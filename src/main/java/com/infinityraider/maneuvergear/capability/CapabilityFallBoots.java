package com.infinityraider.maneuvergear.capability;

import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import com.infinityraider.maneuvergear.item.ItemFallBoots;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityFallBoots implements IInfCapabilityImplementation<ItemStack, CapabilityFallBoots.Impl> {
    private static final CapabilityFallBoots INSTANCE = new CapabilityFallBoots();

    public static CapabilityFallBoots getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(Reference.MOD_ID.toLowerCase(), Names.Items.BOOTS);

    public static boolean areFallBoots(ItemStack stack) {
        return stack != null
        && stack.getCapability(CapabilityFallBoots.CAPABILITY).map(CapabilityFallBoots.Impl::areFallBoots).orElse(false);
    }

    @CapabilityInject(value = Impl.class)
    public static Capability<Impl> CAPABILITY = null;

    private CapabilityFallBoots() {}

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack stack) {
        return (stack.getItem() instanceof ArmorItem) && (((ArmorItem) stack.getItem()).getEquipmentSlot() == EquipmentSlotType.FEET);
    }

    @Override
    public Impl createNewValue(ItemStack stack) {
        return new Impl(stack);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<ItemStack> getCarrierClass() {
        return ItemStack.class;
    }

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    public static class Impl implements ISerializable {
        private boolean areFallBoots;

        private Impl(ItemStack stack) {
            this.areFallBoots = stack.getItem() instanceof ItemFallBoots;
        }

        public boolean areFallBoots() {
            return this.areFallBoots;
        }

        public void setFallBoots(boolean value) {
            this.areFallBoots = value;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.areFallBoots = tag.contains(Names.NBT.BOOTS) && tag.getBoolean(Names.NBT.BOOTS);
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean(Names.NBT.BOOTS, this.areFallBoots);
            return tag;
        }
    }
}
