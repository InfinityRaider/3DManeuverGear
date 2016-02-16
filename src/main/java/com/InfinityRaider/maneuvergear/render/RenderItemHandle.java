package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import com.InfinityRaider.maneuvergear.utility.TransformationMatrix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class RenderItemHandle extends ItemSpecialRenderer<RenderItemHandle.TileEntityDummy> implements IItemModelRenderer {
    private static final RenderItemHandle INSTANCE = new RenderItemHandle(true);
    private static final RenderItemHandle NO_BLADE = new RenderItemHandle(false);

    public static RenderItemHandle getInstance() {
        return INSTANCE;
    }

    private final ModelManeuverGearHandle model;
    private final boolean renderBlade;

    private RenderItemHandle(boolean renderBlade) {
        this.model = new ModelManeuverGearHandle();
        this.renderBlade = renderBlade;
    }

    @Override
    public void renderItem(float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack stack = player.getCurrentEquippedItem();
        if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) {
            return;
        }
        /*
        float scale = 0.075F;
        float dx = 0F;
        float dy = 0F;
        float dz = -0F;
        float angleX = 90F;
        float angleY = 0F;
        float angleZ = 45F;

        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glRotatef(angleZ, 0, 0, 1);
        GL11.glScalef(scale, scale, scale);
        */

        renderModel(player, stack, false);

        /*
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glRotatef(-angleZ, 0, 0, 1);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glTranslatef(-dx, -dy, -dz);
        */
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
        return transformationMatrixDefault();
    }

    private TransformationMatrix transformationMatrixThirdPerson() {
        return transformationMatrixDefault();
    }

    private TransformationMatrix transformationMatrixGui() {
        return transformationMatrixDefault();
    }

    private TransformationMatrix transformationMatrixGround() {
        return transformationMatrixDefault();
    }

    private TransformationMatrix transformationMatrixDefault() {
        return new TransformationMatrix(90, 1, 0, 0).multiplyRightWith(new TransformationMatrix(45, 0, 0, 1)).scale(0.075, 0.075, 0.075);
    }

    @Override
    public ItemSpecialRenderer<TileEntityDummy> getRendererForStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) {
            return this;
        }
        ItemManeuverGearHandle handle = ((ItemManeuverGearHandle) stack.getItem());
        if (handle.hasSwordBlade(stack, false)) {
            return this;
        } else {
            return NO_BLADE;
        }
    }

    /**
     * Render item as entity in the
     *
     * @param stack:  the itemstack
     */
    protected void renderEntity(ItemStack stack) {
        float scale = 0.05F;
        float dx = 0.4F;
        float dy = -1.7F;
        float dz = 0.9F;
        float angleX = 180;
        float angleY = 135;
        float angleZ = -20;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(Minecraft.getMinecraft().thePlayer, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    /**
     * Render item held by an entity
     *
     * @param stack:  the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquipped(ItemStack stack, Entity entity) {
        float scale = 0.075F;
        float dx = 0.4F;
        float dy = -1.7F;
        float dz = 0.9F;
        float angleX = 180;
        float angleY = 135;
        float angleZ = -20;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(entity, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    /**
     * Render item held by an entity
     *
     * @param stack:  the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquippedFirstPerson(ItemStack stack, Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            float scale = 0.075F;
            float dx = 1F;
            float dy = 0F;
            float dz = 1F;
            float angleX = 95F;
            float angleY = 0F;
            float angleZ = -130F;

            GL11.glTranslatef(dx, dy, dz);
            GL11.glRotatef(angleX, 1, 0, 0);
            GL11.glRotatef(angleY, 0, 1, 0);
            GL11.glRotatef(angleZ, 0, 0, 1);
            GL11.glScalef(scale, scale, scale);

            renderModel(entity, stack, false);

            GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
            GL11.glRotatef(-angleZ, 0, 0, 1);
            GL11.glRotatef(-angleY, 0, 1, 0);
            GL11.glRotatef(-angleX, 1, 0, 0);
            GL11.glTranslatef(-dx, -dy, -dz);
        }
    }

    /** Render item held by an entity
     * @param stack: the itemstack
     */
    protected void renderInventory(ItemStack stack) {
        float scale = 0.045F;
        float dx = 0.7F;
        float dy = -1.0F;
        float dz = 0.8F;
        float angleX = 135;
        float angleY = 100;
        float angleZ = -50;

        GL11.glRotatef(angleZ, -1.0F, 0, 1);
        GL11.glRotatef(angleX, 1, 0, 0);
        GL11.glRotatef(angleY, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderModel(Minecraft.getMinecraft().thePlayer, stack, false);

        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-angleY, 0, 1, 0);
        GL11.glRotatef(-angleX, 1, 0, 0);
        GL11.glRotatef(-angleZ, -1.0F, 0, 1);
    }

    @Override
    public final void renderModel(Entity entity, ItemStack stack, boolean left) {
        model.render(entity, 0, 0, 0, 0, 0, 1);

        if (renderBlade(stack, left)) {
            GL11.glPushMatrix();

            float scale = 20;
            float dx = -0.26F;
            float dy = 0.65F;
            float dz = -0.2F;
            float ax = 0;
            float ay = 90;
            float az = 225;

            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(dx, dy, dz);
            GL11.glRotatef(ay, 0, 1, 0);
            GL11.glRotatef(ax, 1, 0, 0);
            GL11.glRotatef(az, 0, 0, 1);
            GL11.glScalef(1.5F, 1.5F, 0.8F);

            float X = -0.2F;
            float Y = 0.3F;
            float Z = 0.0125F;

            GlStateManager.translate(X, Y, Z);
            Minecraft.getMinecraft().getRenderItem().renderItem(ItemResource.EnumSubItems.SWORD_BLADE.getStack(), ItemCameraTransforms.TransformType.NONE);
            GlStateManager.translate(-X, -Y, -Z);

            GL11.glScalef(1F / 1.5F, 1F / 1.5F, 1F / 0.8F);
            GL11.glRotatef(-az, 0, 0, 1);
            GL11.glRotatef(-ax, 1, 0, 0);
            GL11.glRotatef(-ay, 0, 1, 0);
            GL11.glTranslatef(-dx, -dy, -dz);
            GL11.glScalef(1F / scale, 1F / scale, 1F / scale);

            GL11.glPopMatrix();
        }
    }

    private boolean renderBlade() {
        return renderBlade;
    }

    private boolean renderBlade(ItemStack stack, boolean left) {
        if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) {
            return renderBlade();
        }
        return ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack, left);
    }

    @Override
    public Class<? extends TileEntityDummy> getTileClass() {
        return TileEntityDummy.class;
    }

    public static class TileEntityDummy extends TileEntity {}
}
