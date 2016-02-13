package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntityDart extends Render<EntityDart> {
    protected RenderEntityDart(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityDart dart, double x, double y, double z, float float1, float float2) {
        renderEntity(dart, x, y, z, float2);
        EntityPlayer player = dart.getPlayer();
        if (player == null) {
            return;
        }
        if (this.renderManager.options.thirdPersonView > 0 || player != Minecraft.getMinecraft().thePlayer) {
            this.renderWireThirdPerson(dart, player, x, y, z, float2);
        } else {
            renderWireFirstPerson(dart, player, x, y, z, float2);
        }
    }

    /**
     * mostly copied from RenderArrow
     */
    private void renderEntity(EntityDart entity, double x, double y, double z, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;
        float f = 0.0F;
        float f1 = 0.5F;
        float f2 = (float) (i * 10) / 32.0F;
        float f3 = (float) (5 + i * 10) / 32.0F;
        float f4 = 0.0F;
        float f5 = 0.15625F;
        float f6 = (float) (5 + i * 10) / 32.0F;
        float f7 = (float) (10 + i * 10) / 32.0F;
        float f8 = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float f9 = -partialTicks;

        if (f9 > 0.0F) {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f8, f8, f8);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f8, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
        tessellator.draw();
        GL11.glNormal3f(-f8, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f8);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-8.0D, -2.0D, 0.0D).tex((double) f, (double) f2).endVertex();
            worldrenderer.pos(8.0D, -2.0D, 0.0D).tex((double) f1, (double) f2).endVertex();
            worldrenderer.pos(8.0D, 2.0D, 0.0D).tex((double) f1, (double) f3).endVertex();
            worldrenderer.pos(-8.0D, 2.0D, 0.0D).tex((double) f, (double) f3).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderWireFirstPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float f) {
        boolean left = dart.isLeft();
        Tessellator tessellator = Tessellator.getInstance();
        //this vector defines the location of the points on the screen in first person
        double c1 = left ? 0.8D : -0.8D;   //default: -0.5D
        double c2 = -0.8D;  //default: 0.03D
        double c3 = 0.6D;   //default: 0.08D
        Vec3 vec3 = new Vec3(c1, c2, c3);
        vec3.rotatePitch(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f) * (float) Math.PI / 180.0F);
        vec3.rotateYaw(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f) * (float) Math.PI / 180.0F);
        //find the player's position, interpolating based on his movement
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double) f + vec3.xCoord;
        double y_P = player.prevPosY + (player.posY - player.prevPosY) * (double) f + vec3.yCoord;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f + vec3.zCoord;
        //interpolate the entity's position based on its movement
        f = dart.isHooked() ? 1 : f;
        double x_D = dart.prevPosX + (dart.posX - dart.prevPosX) * (double) f;
        double y_D = dart.prevPosY + (dart.posY - dart.prevPosY) * (double) f + 0.25D;
        double z_D = dart.prevPosZ + (dart.posZ - dart.prevPosZ) * (double) f;
        double x_DP = (double) ((float) (x_P - x_D));
        double y_DP = (double) ((float) (y_P - y_D));
        double z_DP = (double) ((float) (z_P - z_D));
        //actually draw the lines
        renderWire(tessellator, x, y, z, x_DP, y_DP, z_DP, getAmplitude(dart));
    }

    private void renderWireThirdPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float f) {
        boolean left = dart.isLeft();
        f = 1;
        Tessellator tessellator = Tessellator.getInstance();
        //find the player's position, interpolating based on his movement
        double eyeHeight = (double) player.getEyeHeight();
        float playerYaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * f) * (float) Math.PI / 180.0F;
        double sinYaw = (double) MathHelper.sin(playerYaw);
        double cosYaw = (double) MathHelper.cos(playerYaw);
        double offsetX = (left ? -1 : 1) * 0.3D;     //default: 0.35D
        double offsetZ = 0.1D;     //default: 0.85D
        double offsetY = -0.3D;      //default: 0.0D
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double) f - cosYaw * offsetX - sinYaw * offsetZ;
        double y_P = player.prevPosY + eyeHeight + offsetY + (player.posY - player.prevPosY) * (double) f - 0.45D;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f - sinYaw * offsetX + cosYaw * offsetZ;
        //interpolate the entity's position based on its movement
        f = dart.isHooked() ? 1 : f;
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
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        byte n = 16;
        for (int i = 0; i <= n; ++i) {
            float t = (float) i / (float) n;
            worldrenderer.pos(x + X * ((double) t), y + Y * ((double) t) - A * MathHelper.sin((float) Math.PI * i / n), z + Z * ((double) t));
            //y + d12 * (double)(f12 * f12 + f12) * 0.5D + 0.25D
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private double getAmplitude(EntityDart dart) {
        double l = dart.getCableLength();
        double d = dart.calculateDistanceToPlayer();
        if (d == 0) {
            return l / 2.0;
        }
        if (d >= l) {
            return 0;
        }
        return l / 2.0 - d / 2.0;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDart entity) {
        return new ResourceLocation("3dmaneuvergear:textures/entities/entityDart.png");
    }

    public static class RenderFactory implements IRenderFactory<EntityDart> {
        private static final RenderFactory INSTANCE = new RenderFactory();

        public static RenderFactory getInstance() {
            return INSTANCE;
        }

        private RenderFactory() {}

        @Override
        public Render<? super EntityDart> createRenderFor(RenderManager manager) {
            return new RenderEntityDart(manager);
        }
    }
}
