package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@MethodsReturnNonnullByDefault
public class RenderEntityDart extends Render<EntityDart> {
    public RenderEntityDart(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(EntityDart dart, double x, double y, double z, float float1, float partialTicks) {
        renderEntity(dart, x, y, z, partialTicks);
        EntityPlayer player = dart.getPlayer();
        if (player == null) {
            return;
        }
        if (this.renderManager.options.thirdPersonView > 0 || player != Minecraft.getMinecraft().thePlayer) {
            this.renderWireThirdPerson(dart, player, x, y, z, partialTicks);
        } else {
            renderWireFirstPerson(dart, player, x, y, z, partialTicks);
        }
    }

    private void renderEntity(EntityDart entity, double x, double y, double z, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
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
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
        buffer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
        buffer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
        buffer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
        tessellator.draw();
        GL11.glNormal3f(-f8, 0.0F, 0.0F);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
        buffer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
        buffer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
        buffer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f8);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-8.0D, -2.0D, 0.0D).tex((double) f, (double) f2).endVertex();
            buffer.pos(8.0D, -2.0D, 0.0D).tex((double) f1, (double) f2).endVertex();
            buffer.pos(8.0D, 2.0D, 0.0D).tex((double) f1, (double) f3).endVertex();
            buffer.pos(-8.0D, 2.0D, 0.0D).tex((double) f, (double) f3).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderWireFirstPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        boolean left = dart.isLeft();
        float yaw = -(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * 0.017453292F;
        float pitch = -(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * 0.017453292F;
        //this vector defines the location of the points on the screen in first person
        double c1 = (left ? 0.8D : -0.8D);
        double c2 = -0.8D;
        double c3 = 1D;
        double deltaY = (double) player.getEyeHeight();
        Vec3d vec3d = new Vec3d(c1, c2, c3);
        vec3d = vec3d.rotatePitch(pitch);
        vec3d = vec3d.rotateYaw(yaw);
        //find the player's position, interpolating based on his movement
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks + vec3d.xCoord;
        double y_P = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks + vec3d.yCoord;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks + vec3d.zCoord;
        //interpolate the entity's position based on its movement
        double x_D = dart.prevPosX + (dart.posX - dart.prevPosX) * (double) partialTicks;
        double y_D = dart.prevPosY + (dart.posY - dart.prevPosY) * (double) partialTicks + 0.25D;
        double z_D = dart.prevPosZ + (dart.posZ - dart.prevPosZ) * (double) partialTicks;
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        double x_DP = (double) ((float) (x_P - x_D));
        double y_DP = (double) ((float) (y_P - y_D)) + deltaY;
        double z_DP = (double) ((float) (z_P - z_D));
        //draw the wire
        renderWire(tessellator, x, y, z, x_DP, y_DP, z_DP, getAmplitude(dart));
    }

    private void renderWireThirdPerson(EntityDart dart, EntityPlayer player, double x, double y, double z, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        boolean left = dart.isLeft();
        //find the player's position, interpolating based on his movement
        float yaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * 0.017453292F;
        double sinYaw = MathHelper.sin(yaw);
        double cosYaw = MathHelper.cos(yaw);
        double offsetX = (left ? -1 : 1) * 0.31D;
        double offsetY = 0.525D;
        double offsetZ = 0.125D;
        double x_P = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks - cosYaw*offsetX - sinYaw*offsetZ;
        double y_P = player.prevPosY + (double)player.getEyeHeight() + (player.posY - player.prevPosY)*partialTicks - offsetY;
        double z_P = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks - sinYaw*offsetX + cosYaw*offsetZ;
        double deltaY = player.isSneaking() ? -0.1875D : 0.0D;
        //interpolate the dart's position based on its movement
        double x_D = dart.prevPosX + (dart.posX - dart.prevPosX) * (double)partialTicks;
        double y_D = dart.prevPosY + (dart.posY - dart.prevPosY) * (double)partialTicks + 0.25D;
        double z_D = dart.prevPosZ + (dart.posZ - dart.prevPosZ) * (double)partialTicks;
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        double x_DP = (double)((float)(x_P - x_D ));
        double y_DP = (double)((float)(y_P - y_D)) + deltaY;
        double z_DP = (double)((float)(z_P - z_D));
        //draw the wire
        renderWire(tessellator, x, y, z, x_DP, y_DP, z_DP, getAmplitude(dart));
    }

    private void renderWire(Tessellator tessellator, double x, double y, double z, double X, double Y, double Z, double A) {
        VertexBuffer buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        byte n = 16;
        for (int i = 0; i <= n; ++i) {
            float t = (float) i / (float) n;
            buffer.pos(x + X * ((double) t), y + Y * ((double) t) - A * MathHelper.sin((float) Math.PI * i / n), z + Z * ((double) t)).color(0, 0, 0, 255).endVertex();
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
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(EntityDart entity) {
        return new ResourceLocation("3dmaneuvergear:textures/entities/entityDart.png");
    }
}
