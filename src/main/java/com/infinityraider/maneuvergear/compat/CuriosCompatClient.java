package com.infinityraider.maneuvergear.compat;

import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.infinityraider.maneuvergear.render.RenderManeuverGear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class CuriosCompatClient {
    public static void init() {
        CuriosRendererRegistry.register(ItemRegistry.itemManeuverGear, Renderer::new);
    }

    public static class Renderer implements ICurioRenderer {
        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(
                ItemStack stack, SlotContext slotContext, PoseStack transforms, RenderLayerParent<T, M> renderLayerParent,
                MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float partialTicks,
                float ageInTicks, float netHeadYaw, float headPitch) {

            transforms.pushPose();
            ICurioRenderer.translateIfSneaking(transforms, slotContext.entity());
            ICurioRenderer.rotateIfSneaking(transforms, slotContext.entity());
            RenderManeuverGear.getInstance().render(stack, transforms, buffer, light);

            transforms.popPose();

        }
    }
}
