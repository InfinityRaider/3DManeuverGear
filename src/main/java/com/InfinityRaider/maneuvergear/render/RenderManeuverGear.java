package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGear;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderManeuverGear implements IBaubleRenderer {
    public static final RenderManeuverGear instance = new RenderManeuverGear();

    @SideOnly(Side.CLIENT)
    private ModelManeuverGear model;

    private RenderManeuverGear() {
        this.model = new ModelManeuverGear();
    }

    @Override
    public void renderBauble(EntityLivingBase entity, ItemStack stack, float partialRenderTick) {
        float yaw = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset)*partialRenderTick;
        float dy = -1.475F;

        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(yaw, 0, 1, 0);
        GL11.glTranslatef(0, dy, 0);

        renderModel(entity, stack);

        GL11.glTranslatef(0, -dy, 0);
        GL11.glRotatef(-yaw, 0, 1, 0);
        GL11.glRotatef(-180, 1, 0, 0);
    }

    private void renderModel(Entity entity, ItemStack stack) {
        boolean sneak = entity != null && entity.isSneaking();
        if(sneak) {
            GL11.glTranslatef(0, 0.1F, 0.25F);
        }
        model.render(entity, 0, 0, 0, 0, 0, 1);
        if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemManeuverGear) {
            ItemManeuverGear maneuverGear = (ItemManeuverGear) stack.getItem();

            float f = 1F;
            float dx = 0.29F;
            float dy = 0.9F;
            float dz = -0.1F;
            float a_x = 15F;
            float a_y = 90F;
            float pinch = 0.6F;

            float delta = -0.7F/pinch;

            GL11.glPushMatrix();

            GL11.glScalef(f, f, f);
            GL11.glTranslatef(dx, dy, dz);
            GL11.glScalef(pinch, 1, 1);
            GL11.glRotatef(a_x, 1, 0, 0);
            GL11.glRotatef(a_y, 0, 1, 0);

            renderBlades(maneuverGear, stack, true);

            GL11.glTranslatef(0, 0, delta);

            renderBlades(maneuverGear, stack, false);

            GL11.glTranslatef(0, 0, -delta);

            GL11.glRotatef(-a_y, 0, 1, 0);
            GL11.glRotatef(-a_x, 1, 0, 0);
            GL11.glScalef(1.0F / pinch, 1, 1);
            GL11.glTranslatef(-dx, -dy, -dz);
            GL11.glScalef(1.0F / f, 1.0F / f, 1.0F / f);

            GL11.glPopMatrix();
        }
        if(sneak) {
            GL11.glTranslatef(0, -0.1F, -0.25F);
        }
    }

    private void renderBlades(ItemManeuverGear maneuverGear, ItemStack stack, boolean left) {
        int amount = maneuverGear.getBladeCount(stack, left);
        if(amount > 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float delta = 0.0675F;

            GL11.glPushMatrix();

            for(int i = 0; i < amount; i++) {
                GL11.glPushMatrix();

                ItemStack swordBlade = ItemResource.EnumSubItems.SWORD_BLADE.getStack();
                IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(swordBlade);
                Minecraft.getMinecraft().getRenderItem().renderItem(swordBlade, model);

                GL11.glPopMatrix();
                GL11.glTranslatef(0, 0, delta);
            }
            GL11.glTranslatef(0, 0, -amount * delta);

            GL11.glPopMatrix();
        }
    }
}
