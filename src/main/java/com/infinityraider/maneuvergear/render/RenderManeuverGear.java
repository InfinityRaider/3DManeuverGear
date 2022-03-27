package com.infinityraider.maneuvergear.render;

import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
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

    public void render(ItemStack stack, PoseStack transforms, MultiBufferSource buffer, int light) {
        transforms.pushPose();
        //Render the maneuver gear belt
        this.renderGear(stack, transforms, buffer, light);
        // Render the blades
        this.renderBlades(stack, transforms, buffer, light);

        transforms.popPose();
    }

    private void renderGear(ItemStack stack, PoseStack transforms, MultiBufferSource buffer, int light) {
        transforms.pushPose();
        float scale = 1.25F;
        transforms.translate(-scale*0, 1, scale*0.2);
        transforms.mulPose(Vector3f.ZP.rotationDegrees(180));
        transforms.scale(scale, scale, scale);
        this.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, transforms, buffer, 0);
        transforms.popPose();
    }

    private void renderBlades(ItemStack stack, PoseStack transforms, MultiBufferSource buffer, int light) {
        if(stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemManeuverGear)) {
            return;
        }
        ItemManeuverGear maneuverGear = (ItemManeuverGear) stack.getItem();

        transforms.pushPose();

        transforms.translate(0, 0.95,0);
        transforms.mulPose(Vector3f.YP.rotationDegrees(90));

        this.renderBlades(maneuverGear, stack, true, transforms, buffer, light);
        this.renderBlades(maneuverGear, stack, false, transforms, buffer, light);

        transforms.popPose();
    }

    private void renderBlades(ItemManeuverGear gear, ItemStack stack, boolean left, PoseStack transforms, MultiBufferSource buffer, int light) {
        int count = gear.getBladeCount(stack, left);
        if(count <= 0) {
            return;
        }
        if(count > ItemManeuverGear.MAX_HOLSTERED_BLADES) {
            count = ItemManeuverGear.MAX_HOLSTERED_BLADES;
        }
        transforms.pushPose();
        transforms.translate(0.05, 0, (left ? 1 : -1)*0.285);
        transforms.mulPose(Vector3f.ZP.rotationDegrees(40));
        transforms.scale(0.70F, 1, 0.25F);
        for(int i = 0; i < count; i++) {
            transforms.pushPose();
            transforms.translate(0, 0, (left ? 1 : -1)*0.15*i);
            this.getItemRenderer().renderStatic(this.getBlade(), ItemTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, transforms, buffer, 0);
            transforms.popPose();
        }
        transforms.popPose();
    }

    private ItemStack getBlade() {
        if(this.blade == null) {
            this.blade = new ItemStack(ItemRegistry.itemSwordBlade);
        }
        return this.blade;
    }

    private ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }
}
