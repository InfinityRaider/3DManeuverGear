package com.infinityraider.maneuvergear.utility;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IManeuverGear {
    void onWornTick(LivingEntity entity);

    void onEquip(LivingEntity entity);

    void onUnequip(LivingEntity entity);

    @OnlyIn(Dist.CLIENT)
    void render(ItemStack stack, LivingEntity entity, MatrixStack transforms, IRenderTypeBuffer buffer, int light, float partialTicks);
}
