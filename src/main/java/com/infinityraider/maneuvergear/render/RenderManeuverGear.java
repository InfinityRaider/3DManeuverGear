package com.infinityraider.maneuvergear.render;

import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderManeuverGear {
    private static final RenderManeuverGear INSTANCE = new RenderManeuverGear();

    private ItemStack blade;

    public static RenderManeuverGear getInstance() {
        return INSTANCE;
    }

    private RenderManeuverGear() {}

    public void render(ItemStack stack, LivingEntity entity, MatrixStack transforms, IRenderTypeBuffer buffer, int light, float partialTicks) {
        transforms.push();

        // Correct for Sneaking
        if(entity.isSneaking()) {
            transforms.translate(0, 0.1, 0.25);
        }
        //Render the maneuver gear belt
        this.renderGear(stack, transforms, buffer, light);
        // Render the blades
        this.renderBlades(stack, transforms, buffer, light);

        transforms.pop();
    }

    private void renderGear(ItemStack stack, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        transforms.push();
        float scale = 1.25F;
        transforms.translate(-scale*0.125, 0.625, scale*0.125);
        transforms.rotate(Vector3f.ZP.rotationDegrees(180));
        transforms.scale(scale, scale, scale);
        this.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, transforms, buffer);
        transforms.pop();
    }

    private void renderBlades(ItemStack stack, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        if(stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemManeuverGear)) {
            return;
        }
        ItemManeuverGear maneuverGear = (ItemManeuverGear) stack.getItem();

        transforms.push();

        transforms.translate(0, 0.95,0);
        transforms.rotate(Vector3f.YP.rotationDegrees(90));

        this.renderBlades(maneuverGear, stack, true, transforms, buffer, light);
        this.renderBlades(maneuverGear, stack, false, transforms, buffer, light);

        transforms.pop();
    }

    private void renderBlades(ItemManeuverGear gear, ItemStack stack, boolean left, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        int count = gear.getBladeCount(stack, left);
        if(count <= 0) {
            return;
        }
        if(count > ItemManeuverGear.MAX_HOLSTERED_BLADES) {
            count = ItemManeuverGear.MAX_HOLSTERED_BLADES;
        }
        transforms.push();
        transforms.translate(0.05, 0, (left ? 1 : -1)*0.285);
        transforms.rotate(Vector3f.ZP.rotationDegrees(40));
        transforms.scale(0.70F, 1, 0.25F);
        for(int i = 0; i < count; i++) {
            transforms.push();
            transforms.translate(0, 0, (left ? 1 : -1)*0.15*i);
            this.getItemRenderer().renderItem(this.getBlade(), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, transforms, buffer);
            transforms.pop();
        }
        transforms.pop();
    }

    private ItemStack getBlade() {
        if(this.blade == null) {
            this.blade = new ItemStack(ItemRegistry.getInstance().itemSwordBlade);
        }
        return this.blade;
    }

    private ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }
}
