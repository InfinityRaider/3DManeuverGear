package com.infinityraider.maneuvergear.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IManeuverGear {
    void onWornTick(LivingEntity entity);

    void onEquip(LivingEntity entity);

    void onUnequip(LivingEntity entity);

    @OnlyIn(Dist.CLIENT)
    void render(ItemStack stack, LivingEntity entity, PoseStack transforms, MultiBufferSource buffer, int light, float partialTicks);
}
