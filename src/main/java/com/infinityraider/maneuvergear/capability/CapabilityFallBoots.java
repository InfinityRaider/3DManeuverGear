package com.infinityraider.maneuvergear.capability;

import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.maneuvergear.capability.CapabilityFallBoots.Impl;
import com.infinityraider.maneuvergear.item.ItemFallBoots;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityFallBoots implements IInfSerializableCapabilityImplementation<ItemStack, Impl> {
    private static final CapabilityFallBoots INSTANCE = new CapabilityFallBoots();

    public static CapabilityFallBoots getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(Reference.MOD_ID.toLowerCase(), Names.Items.BOOTS);

    public static boolean areFallBoots(ItemStack stack) {
        return stack != null
        && stack.getCapability(CapabilityFallBoots.CAPABILITY).map(CapabilityFallBoots.Impl::areFallBoots).orElse(false);
    }

    public static Capability<Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityFallBoots() {}

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack stack) {
        return (stack.getItem() instanceof ArmorItem) && (((ArmorItem) stack.getItem()).getEquipmentSlot(stack) == EquipmentSlot.FEET);
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

    public static class Impl implements Serializable<Impl> {
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
        public void copyDataFrom(Impl from) {
            this.setFallBoots(from.areFallBoots());
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Names.NBT.BOOTS, this.areFallBoots);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.areFallBoots = tag.contains(Names.NBT.BOOTS) && tag.getBoolean(Names.NBT.BOOTS);
        }
    }
}
