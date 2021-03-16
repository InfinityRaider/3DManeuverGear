package com.infinityraider.maneuvergear.compat;

import com.infinityraider.infinitylib.capability.CapabilityProvider;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;

public class CuriosCompat {
    public static void sendInterModMessages() {
        InterModComms.sendTo(Names.Mods.CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
    }

    public static ItemStack findManeuverGear(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.getInstance().itemManeuverGear, entity)
                .map(t -> t.right).orElse(ItemStack.EMPTY);
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
            public void curioTick(String identifier, int index, LivingEntity entity) {
                this.getManeuverGear().onWornTick(entity);
            }

            @Override
            public void onEquip(String identifier, int index, LivingEntity entity) {
                this.getManeuverGear().onEquip(entity);
            }

            @Override
            public void onUnequip(String identifier, int index, LivingEntity entity) {
                this.getManeuverGear().onUnequip(entity);
            }

            @Override
            public boolean canEquip(String identifier, LivingEntity entity) {
                return true;
            }

            @Override
            public boolean canSync(String identifier, int index, LivingEntity livingEntity) {
                return true;
            }

            @Nonnull
            @Override
            public CompoundNBT writeSyncData() {
                CompoundNBT tag = new CompoundNBT();
                this.stack.write(tag);
                return tag;
            }

            public void readSyncData(CompoundNBT tag) {
                this.stack = ItemStack.read(tag);
            }

            @Override
            public boolean canRightClickEquip() {
                return true;
            }

            @Override
            public boolean canRender(String identifier, int index, LivingEntity entity) {
                return true;
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public void render(String identifier, int index, MatrixStack transforms, IRenderTypeBuffer buffer, int light, LivingEntity entity,
                               float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

                EntityRenderer<?> renderer = Minecraft.getInstance().getRenderManager().getRenderer(entity);
                if (!(renderer instanceof IEntityRenderer<?, ?>)) {
                    return;
                }
                EntityModel<?> model = ((IEntityRenderer<?, ?>) renderer).getEntityModel();
                if (!(model instanceof BipedModel<?>)) {
                    return;
                }
                this.getManeuverGear().render(this.stack, entity, transforms, buffer, light, partialTicks);
            }

        }
    }
}
