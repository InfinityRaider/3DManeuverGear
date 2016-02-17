package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import com.InfinityRaider.maneuvergear.utility.TransformationMatrix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class RenderItemHandle extends ItemSpecialRenderer<RenderItemHandle.TileEntityDummy> implements IItemModelRenderer {
    private static final RenderItemHandle INSTANCE = new RenderItemHandle();

    public static RenderItemHandle getInstance() {
        return INSTANCE;
    }

    private final ModelManeuverGearHandle model;

    private RenderItemHandle() {
        super();
        this.model = new ModelManeuverGearHandle();
    }

    @Override
    public void renderItem(ItemStack stack, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        renderModel(player, stack, false);
    }

    @Override
    public TransformationMatrix getTransformMatrixForPerspective(ItemCameraTransforms.TransformType cameraTransformsType) {
        switch(cameraTransformsType) {
            case FIRST_PERSON: return transformationMatrixFirstPerson();
            case THIRD_PERSON: return transformationMatrixThirdPerson();
            case GUI: return transformationMatrixGui();
            case GROUND: return transformationMatrixGround();
            default: return transformationMatrixDefault();
        }
    }

    private TransformationMatrix transformationMatrixFirstPerson() {
        return new TransformationMatrix(0.25, -0.275, 0.35)
                .multiplyRightWith(new TransformationMatrix(90, 1, 0, 0))
                .multiplyRightWith(new TransformationMatrix(225, 0, 0, 1))
                .multiplyRightWith(new TransformationMatrix(180, 0, 1, 0))
                .scale(0.075, 0.075, 0.075);
    }

    private TransformationMatrix transformationMatrixThirdPerson() {
        return new TransformationMatrix(0.08, -0.1, -0.07)
                .multiplyRightWith(new TransformationMatrix(180, 0, 1, 0))
                .scale(0.03, 0.03, 0.03);
    }

    private TransformationMatrix transformationMatrixGui() {
        return new TransformationMatrix(0, -0.1, 0)
                .multiplyRightWith(new TransformationMatrix(90, -1, 0, 1))
                .multiplyRightWith(new TransformationMatrix(-0, 1, 0, 0))
                .multiplyRightWith(new TransformationMatrix(90, 0, 1, 0))
                .scale(0.035, 0.035, 0.035);
    }

    private TransformationMatrix transformationMatrixGround() {
        return new TransformationMatrix(-0.2, 0, 0.05)
                .multiplyRightWith(new TransformationMatrix(90, 1, 0, 0))
                .multiplyRightWith(new TransformationMatrix(225, 0, 0, 1))
                .multiplyRightWith(new TransformationMatrix(180, 0, 1, 0))
                .scale(0.03, 0.03, 0.03);
    }

    private TransformationMatrix transformationMatrixDefault() {
        return new TransformationMatrix(0.25, -0.275, 0.35)
                .multiplyRightWith(new TransformationMatrix(90, 1, 0, 0))
                .multiplyRightWith(new TransformationMatrix(225, 0, 0, 1))
                .multiplyRightWith(new TransformationMatrix(180, 0, 1, 0))
                .scale(0.075, 0.075, 0.075);
    }

    @Override
    public final void renderModel(Entity entity, ItemStack stack, boolean left) {
        model.render(entity, 0, 0, 0, 0, 0, 1);

        if (renderBlade(stack, left)) {
            GL11.glPushMatrix();

            float scale = 20;
            float dx = -0.26F;
            float dy = 0.75F;
            float dz = 0.45F;
            float ax = 0;
            float ay = 90;
            float az = 225;

            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(dx, dy, dz);
            GL11.glRotatef(ay, 0, 1, 0);
            GL11.glRotatef(ax, 1, 0, 0);
            GL11.glRotatef(az, 0, 0, 1);
            GL11.glScalef(3.5F, 3.5F, 1F);

            float X = -0.2F;
            float Y = 0.3F;
            float Z = 0.0125F;

            GlStateManager.translate(X, Y, Z);

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            ItemStack swordBlade = ItemResource.EnumSubItems.SWORD_BLADE.getStack();
            IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(swordBlade);
            Minecraft.getMinecraft().getRenderItem().renderItem(swordBlade, model);
            GlStateManager.translate(-X, -Y, -Z);

            GL11.glScalef(1F / 3.5F, 1F / 3.5F, 1F / 1F);
            GL11.glRotatef(-az, 0, 0, 1);
            GL11.glRotatef(-ax, 1, 0, 0);
            GL11.glRotatef(-ay, 0, 1, 0);
            GL11.glTranslatef(-dx, -dy, -dz);
            GL11.glScalef(1F / scale, 1F / scale, 1F / scale);

            GL11.glPopMatrix();
        }
    }

    private boolean renderBlade(ItemStack stack, boolean left) {
        if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) {
            return false;
        }
        return ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack, left);
    }

    @Override
    public Class<? extends TileEntityDummy> getTileClass() {
        return TileEntityDummy.class;
    }

    public static class TileEntityDummy extends TileEntity {}
}
