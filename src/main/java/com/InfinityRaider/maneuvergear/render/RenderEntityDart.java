package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderEntityDart extends Render {
    @Override
    public void doRender(Entity entity, double x, double y, double z, float float1, float float2) {
        renderEntity(entity, x, y, z, float2);
        EntityDart dart = (EntityDart) entity;
        EntityPlayer player = dart.getPlayer();
        if(player == null) {
            return;
        }
        if(this.renderManager.options.thirdPersonView > 0 || player != Minecraft.getMinecraft().thePlayer) {
            this.renderWireThirdPerson(dart, player, x, y, z, float2);
        } else {
            renderWireFirstPerson(dart, player, x, y, z, float2);
        }
    }

    /** mostly copied from RenderArrow */
    private void renderEntity(Entity entity, double x, double y, double z, float float2) {
        this.bindEntityTexture(entity);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * float2 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * float2, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float)(b0 * 10) / 32.0F;
        float f5 = (float)(5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + b0 * 10) / 32.0F;
        float f9 = (float)(10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f9);
        tessellator.draw();
        for (int i = 0; i < 4; ++i) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.draw();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void renderWireFirstPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float f) {
        boolean left = dart.isLeft();
        Tessellator tessellator = Tessellator.instance;
        //this vector defines the location of the points on the screen in first person
        double c1 = left?0.8D:-0.8D;   //default: -0.5D
        double c2 = -0.8D;  //default: 0.03D
        double c3 = 0.6D;   //default: 0.08D
        Vec3 vec3 = Vec3.createVectorHelper(c1, c2, c3);
        vec3.rotateAroundX(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f) * (float) Math.PI / 180.0F);
        vec3.rotateAroundY(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f) * (float) Math.PI / 180.0F);
        //find the player's position, interpolating based on his movement
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double)f + vec3.xCoord;
        double y_P = player.prevPosY + (player.posY - player.prevPosY) * (double)f + vec3.yCoord;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f + vec3.zCoord;
        //interpolate the entity's position based on its movement
        f = dart.isHooked()?1:f;
        double x_D = dart.prevPosX + (dart.posX - dart.prevPosX) * (double)f;
        double y_D = dart.prevPosY + (dart.posY - dart.prevPosY) * (double)f + 0.25D;
        double z_D = dart.prevPosZ + (dart.posZ - dart.prevPosZ) * (double)f;
        double x_DP = (double)((float)(x_P - x_D));
        double y_DP = (double)((float)(y_P - y_D));
        double z_DP = (double)((float)(z_P - z_D));
        //actually draw the lines
        renderWire(tessellator, x, y, z, x_DP, y_DP, z_DP, getAmplitude(dart));
    }

    private void renderWireThirdPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float f) {
        boolean left = dart.isLeft();
        f = 1;
        Tessellator tessellator = Tessellator.instance;
        //find the player's position, interpolating based on his movement
        double eyeHeight = (double) player.getEyeHeight();
        float playerYaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * f) * (float) Math.PI / 180.0F;
        double sinYaw = (double) MathHelper.sin(playerYaw);
        double cosYaw = (double) MathHelper.cos(playerYaw);
        double offsetX = (left?-1:1)*0.3D;     //default: 0.35D
        double offsetZ = 0.1D;     //default: 0.85D
        double offsetY = -0.3D;      //default: 0.0D
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double) f - cosYaw*offsetX - sinYaw*offsetZ;
        double y_P = player.prevPosY + eyeHeight + offsetY + (player.posY - player.prevPosY) * (double) f - 0.45D;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f - sinYaw*offsetX + cosYaw*offsetZ;
        //interpolate the entity's position based on its movement
        f = dart.isHooked()?1:f;
        double x_D = dart.prevPosX + (dart.posX - dart.prevPosX) * (double) f;
        double y_D = dart.prevPosY + (dart.posY - dart.prevPosY) * (double) f + 0.25D;
        double z_D = dart.prevPosZ + (dart.posZ - dart.prevPosZ) * (double) f;
        double x_DP = (double) ((float) (x_P - x_D));
        double y_DP = (double) ((float) (y_P - y_D));
        double z_DP = (double) ((float) (z_P - z_D));
        //actually draw the lines
        renderWire(tessellator, x, y, z, x_DP, y_DP, z_DP, getAmplitude(dart));
    }

    private void renderWire(Tessellator tessellator, double x, double y, double z, double X, double Y, double Z, double A) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawing(3);
        tessellator.setColorOpaque_I(0);
        byte n = 16;
        for (int i = 0; i <= n; ++i) {
            float t = (float) i / (float) n;
            tessellator.addVertex(x + X * ((double) t), y + Y * ((double) t) - A*MathHelper.sin((float) Math.PI*i/n), z + Z * ((double) t));
            //y + d12 * (double)(f12 * f12 + f12) * 0.5D + 0.25D
        }
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);}

    private double getAmplitude(EntityDart dart) {
        double l = dart.getCableLength();
        double d = dart.calculateDistanceToPlayer();
        if(d == 0) {
            return l/2.0;
        }
        if(d >= l) {
            return 0;
        }
        return l/2.0 - d/2.0;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation("3dmaneuvergear:textures/entities/entityDart.png");
    }
}
