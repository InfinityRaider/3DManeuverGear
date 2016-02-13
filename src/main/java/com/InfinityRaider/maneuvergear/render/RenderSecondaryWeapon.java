package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.handler.SwingLeftHandHandler;
import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import com.InfinityRaider.maneuvergear.render.model.ModelPlayerModified;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Classed used to render the right weapon for dual wielded weapons in first and third person
 */
@SideOnly(Side.CLIENT)
public class RenderSecondaryWeapon {
    private float equippedProgress = 1;
    private float prevEquippedProgress = 1;
    private int equippedItemSlot = -1;
    private int prevEquippedItemSlot = -2;

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void setPlayerLeftArmSwing(RenderPlayerEvent.Pre event) {
        ModelPlayerModified.setLeftArmSwingProgress(SwingLeftHandHandler.getInstance().getSwingProgress(event.entityPlayer, event.partialRenderTick));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void renderLeftWeaponThirdPerson(RenderPlayerEvent.Specials.Post event) {
        if(event.entityPlayer.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof IDualWieldedWeapon)) {
            return;
        }

        IDualWieldedWeapon item = (IDualWieldedWeapon) stack.getItem();
        float[] values = item.getTransformationComponents(player, stack, event.partialRenderTick, false);

        float scale = values.length >= 1 ? values[0] : 1;
        float dx = values.length >= 2 ? values[1] : 0;
        float dy = values.length >= 3 ? values[2] : 0;
        float dz = values.length >= 4 ? values[3] : 0;
        float yaw = values.length >= 5 ? values[4] : 0;
        float pitch = values.length >= 6 ? values[5] : 0;

        GL11.glPushMatrix();

        //Using event.partialRenderTick here bugs out for some reason
        applyTransformationsFromPlayerThirdPerson(player, 0, false);

        GL11.glRotatef(yaw, 0, 0, 1);
        GL11.glRotatef(pitch, 1, 0, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        if(item.useModel(stack)) {
           item.getModel(stack).renderModel(player, stack, true);
        } else {
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        }

        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-pitch, 0, 1, 0);
        GL11.glRotatef(-yaw, 1, 0, 0);

        applyTransformationsFromPlayerThirdPerson(player, 0, true);

        GL11.glPopMatrix();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void renderLeftWeaponFirstPerson(RenderGameOverlayEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if(player == null || prevEquippedItemSlot != equippedItemSlot) {
            return;
        }

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
            return;
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof IDualWieldedWeapon)) {
            return;
        }

        IDualWieldedWeapon item = (IDualWieldedWeapon) stack.getItem();
        float[] values = item.getTransformationComponents(player, stack, event.partialTicks, true);

        GL11.glPushMatrix();

        RenderHelper.enableStandardItemLighting();
        int i = Minecraft.getMinecraft().theWorld.getLight(player.getPosition());
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int l = stack.getItem().getColorFromItemStack(stack, 0);
        float r = (float) (l >> 16 & 255) / 255.0F;
        float g = (float) (l >> 8 & 255) / 255.0F;
        float b = (float) (l & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);

        correctForViewBobbing(event.partialTicks, false);
        applyTransformationsFromPlayerFirstPerson(player, event.partialTicks, false);
        applyViewBobbingLeft(event.partialTicks, false);

        float scale = values.length >= 1 ? values[0] : 1;
        float dx = values.length >= 2 ? values[1] : 0;
        float dy = values.length >= 3 ? values[2] : 0;
        float dz = values.length >= 4 ? values[3] : 0;
        float yaw = values.length >= 5 ? values[4] : 0;
        float pitch = values.length >= 6 ? values[5] : 0;

        GL11.glRotatef(pitch, 1, 0, 0);
        GL11.glRotatef(yaw, 0, 0, 1);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        if (item.useModel(stack)) {
            item.getModel(stack).renderModel(player, stack, true);
        } else {
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(Gui.icons);

        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-yaw, 0, 0, 1);
        GL11.glRotatef(-pitch, 1, 0, 0);

        applyViewBobbingLeft(event.partialTicks, true);
        applyTransformationsFromPlayerFirstPerson(player, event.partialTicks, true);
        correctForViewBobbing(event.partialTicks, true);

        RenderHelper.disableStandardItemLighting();

        GL11.glPopMatrix();
        GL11.glFlush();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void updateRenderer(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            updateEquippedItem();
        }
    }

    private void applyTransformationsFromPlayerThirdPerson(EntityPlayer player, float partialTick, boolean inverse) {
        Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(player);
        if(!(render instanceof RenderPlayer)) {
            return;
        }
        applyTransformationsFromModel(((RenderPlayer) render).getMainModel().bipedLeftArm, partialTick, inverse);
    }

    private void applyTransformationsFromModel(ModelRenderer model, float partialTick, boolean inverse) {
        if(inverse) {
            if (model.rotateAngleX != 0.0F) {
                GL11.glRotatef(model.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }
            if (model.rotateAngleY != 0.0F) {
                GL11.glRotatef(model.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }
            if (model.rotateAngleZ != 0.0F) {
                GL11.glRotatef(model.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }
            GL11.glTranslatef(model.rotationPointX * partialTick, model.rotationPointY * partialTick, model.rotationPointZ * partialTick);
            GL11.glTranslatef(model.offsetX, model.offsetY, model.offsetZ);
        } else {
            GL11.glTranslatef(model.offsetX, model.offsetY, model.offsetZ);
            GL11.glTranslatef(model.rotationPointX * partialTick, model.rotationPointY * partialTick, model.rotationPointZ * partialTick);
            if (model.rotateAngleZ != 0.0F) {
                GL11.glRotatef(model.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }
            if (model.rotateAngleY != 0.0F) {
                GL11.glRotatef(model.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }
            if (model.rotateAngleX != 0.0F) {
                GL11.glRotatef(model.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private void applyTransformationsFromPlayerFirstPerson(EntityPlayerSP player, float partialTick, boolean inverse) {
        float yaw = 190;
        float yaw2 = 15;
        float roll = -2;

        float x = 0.1F;
        float y = -0.09F;
        float z = 0.075F;

        if(inverse) {
            applySwingProgress(player, partialTick, true);
            applyEquipProgress(partialTick, true);
            GL11.glTranslatef(-x, -y, -z);
            GL11.glRotatef(yaw, 0, 1, 0);
            GL11.glRotatef(-yaw2, 0, 1, 0);
            GL11.glRotatef(-roll, 0, 0, 1);
        } else {
            GL11.glRotatef(-yaw, 0, 1, 0);
            GL11.glTranslatef(x, y, z);
            GL11.glRotatef(yaw2, 0, 1, 0);
            GL11.glRotatef(roll, 0, 0, 1);
            applyEquipProgress(partialTick, false);
            applySwingProgress(player, partialTick, false);
        }
    }

    private void applyEquipProgress(float partialTick, boolean inverse) {
        float f1 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTick;
        float f2 = 1F;
        if(inverse) {
            GL11.glTranslatef(0,   f1 * f2, 0);
        } else {
            GL11.glTranslatef(0, -f1 * f2, 0);
        }
    }

    private void applySwingProgress(EntityPlayer player, float partialTick, boolean inverse) {
        float f5 = SwingLeftHandHandler.getInstance().getSwingProgress(player, partialTick);
        float f6 = MathHelper.sin(f5 * f5 * (float) Math.PI);
        float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
        if(inverse) {
            GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
        } else {
            GL11.glRotatef(f6 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(f7 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f7 * 80.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    private void correctForViewBobbing(float partialTick, boolean inverse) {
        Minecraft mc = Minecraft.getMinecraft();
        if(!mc.gameSettings.viewBobbing) {
            return;
        }
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * partialTick);
            float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTick;
            float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTick;
            if(inverse) {
                GL11.glTranslatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float)Math.PI) * f3), 0.0F);
                GL11.glRotatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(Math.abs(MathHelper.cos(f2 * (float)Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
            } else {
                GL11.glRotatef(-f4, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F, Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3), 0.0F);
            }
        }
    }

    private void applyViewBobbingLeft(float partialTick, boolean inverse) {
        Minecraft mc = Minecraft.getMinecraft();
        if(!mc.gameSettings.viewBobbing) {
            return;
        }
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * partialTick);
            float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTick;
            float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTick;
            float f5 = 0.1F;
            if(inverse) {
                GL11.glRotatef(-f4, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F * f5, Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3) * f5, 0.0F);
            } else {
                GL11.glTranslatef(-MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F * f5, -Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3) * f5, 0.0F);
                GL11.glRotatef(MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    public void updateEquippedItem() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if(player == null) {
            return;
        }

        ItemStack stack = player.inventory.getCurrentItem();

        this.prevEquippedItemSlot = equippedItemSlot;
        this.equippedItemSlot = player.inventory.currentItem;
        this.prevEquippedProgress = this.equippedProgress;
        if(stack == null || stack.getItem() == null || !(stack.getItem() instanceof IDualWieldedWeapon)) {
            this.equippedProgress = 1;
        } else {
            if(prevEquippedItemSlot != equippedItemSlot) {
                this.equippedProgress = 1;
            }
            if(equippedProgress > 0) {
                this.equippedProgress = this.equippedProgress*0.3F;
                if(this.equippedProgress < 1E-10) {
                    this.equippedProgress = 0;
                }
            }
        }
    }
}
