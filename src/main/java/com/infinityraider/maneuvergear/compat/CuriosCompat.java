package com.infinityraider.maneuvergear.compat;

import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class CuriosCompat {
    public static void sendInterModMessages() {
        InterModComms.sendTo(Names.Mods.CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
    }

    public static ItemStack findManeuverGear(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().getEquippedCurios(entity).map(curios -> {
            for(int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if(stack.getItem() == ItemRegistry.itemManeuverGear) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    public static CurioCapabilityImplementation getCurioCapabilityImplementation() {
        return CurioCapabilityImplementation.INSTANCE;
    }

    public static class CurioCapabilityImplementation implements ICapabilityImplementation<ItemStack, ICurio> {
        private static final CurioCapabilityImplementation INSTANCE = new CurioCapabilityImplementation();

        private CurioCapabilityImplementation() {}

        @Override
        public Capability<ICurio> getCapability() {
            return CuriosCapability.ITEM;
        }

        @Override
        public boolean shouldApplyCapability(ItemStack carrier) {
            return carrier.getItem() instanceof ItemManeuverGear;
        }

        @Override
        public ICurio createNewValue(ItemStack carrier) {
            return new Impl(carrier);
        }

        @Override
        public ResourceLocation getCapabilityKey() {
            return CuriosCapability.ID_ITEM;
        }

        @Override
        public Class<ItemStack> getCarrierClass() {
            return ItemStack.class;
        }

        public static class Impl implements ICurio {
            private ItemStack stack;

            private Impl(ItemStack stack) {
                this.stack = stack;
            }

            private ItemManeuverGear getManeuverGear() {
                return (ItemManeuverGear) this.stack.getItem();
            }

            @Override
            public ItemStack getStack() {
                return this.stack;
            }

            @Override
            public void curioTick(SlotContext context) {
                this.getManeuverGear().onWornTick(context.entity());
            }

            @Override
            public void onEquip(SlotContext context, ItemStack pref) {
                this.getManeuverGear().onEquip(context.entity());
            }

            @Override
            public void onUnequip(SlotContext context, ItemStack newStack) {
                this.getManeuverGear().onUnequip(context.entity());
            }

            @Override
            public boolean canEquip(SlotContext context) {
                return true;
            }

            @Override
            public boolean canUnequip(SlotContext context) {
                return true;
            }

            @Override
            public boolean canSync(SlotContext context) {
                return true;
            }

            @Nonnull
            @Override
            public CompoundTag writeSyncData(SlotContext context) {
                CompoundTag tag = new CompoundTag();
                this.stack.save(tag);
                return tag;
            }

            public void readSyncData(SlotContext context, CompoundTag tag) {
                this.stack = ItemStack.of(tag);
            }

            @Override
            public boolean canEquipFromUse(SlotContext context) {
                return true;
            }
        }
    }
}
