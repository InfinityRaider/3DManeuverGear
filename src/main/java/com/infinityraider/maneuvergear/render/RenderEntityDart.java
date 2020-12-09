package com.infinityraider.maneuvergear.render;

import com.infinityraider.maneuvergear.entity.EntityDart;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
public class RenderEntityDart extends EntityRenderer<EntityDart> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("3d_maneuver_gear:textures/entities/dart.png");
    private static final RenderType RENDER_TYPE = RenderType.getEntityCutout(TEXTURE);

    public RenderEntityDart(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(EntityDart dart, float yaw, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        this.renderEntity(dart, partialTicks, transforms, buffer, light);
        PlayerEntity player = dart.getPlayer();
        if (player == null) {
            return;
        }
        if (this.renderManager.options.getPointOfView().ordinal() > 0 || player != Minecraft.getInstance().player) {
            this.renderWireThirdPerson(dart, player, partialTicks, transforms, buffer);
        } else {
            renderWireFirstPerson(dart, player, partialTicks, transforms, buffer);
        }
    }

    private void renderEntity(EntityDart dart, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer, int light) {
        transforms.push();

        transforms.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, dart.prevRotationYaw, dart.rotationYaw) - 90.0F));
        transforms.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, dart.prevRotationPitch, dart.rotationPitch)));

        transforms.rotate(Vector3f.XP.rotationDegrees(45.0F));
        transforms.scale(0.05625F, 0.05625F, 0.05625F);
        transforms.translate(-4.0D, 0.0D, 0.0D);

        IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);
        MatrixStack.Entry entry = transforms.getLast();
        Matrix4f matrix4f = entry.getMatrix();
        Matrix3f matrix3f = entry.getNormal();

        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);

        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

        for(int j = 0; j < 4; ++j) {
            transforms.rotate(Vector3f.XP.rotationDegrees(90.0F));
            this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
        }
        transforms.pop();
    }

    private void renderWireFirstPerson(EntityDart dart, PlayerEntity player, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer) {
        boolean left = dart.isLeft();
        float yaw = -(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * ((float) Math.PI / 180F);
        float pitch = -(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * ((float) Math.PI / 180F);
        //this vector defines the location of the points on the screen in first person
        double c1 = (left ? 0.8D : -0.8D);
        double c2 = -0.8D;
        double c3 = 1D;
        float deltaY = player.getEyeHeight();
        Vector3d vec3d = new Vector3d(c1, c2, c3);
        vec3d = vec3d.rotatePitch(pitch);
        vec3d = vec3d.rotateYaw(yaw);
        //find the player's position, interpolating based on his movement
        double x_P = MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX()) + vec3d.getX();
        double y_P = MathHelper.lerp(partialTicks, player.prevPosY, player.getPosY()) + vec3d.getY();
        double z_P = MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ()) + vec3d.getZ();
        //interpolate the dart's position based on its movement
        double x_D = MathHelper.lerp(partialTicks, dart.prevPosX, dart.getPosX());
        double y_D = MathHelper.lerp(partialTicks, dart.prevPosY, dart.getPosY()) + 0.25D;
        double z_D = MathHelper.lerp(partialTicks, dart.prevPosZ, dart.getPosZ());
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        float delta_x = (float) (x_P - x_D);
        float delta_y = (float) (y_P - y_D) + deltaY;
        float delta_z = (float) (z_P - z_D);
        //draw the wire
        renderWire(transforms, buffer, delta_x, delta_y, delta_z, getAmplitude(dart));
    }

    private void renderWireThirdPerson(EntityDart dart, PlayerEntity player, float partialTicks, MatrixStack transforms, IRenderTypeBuffer buffer) {
        boolean left = dart.isLeft();
        //find the player's position, interpolating based on his movement
        float yaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * ((float) Math.PI / 180F);
        double sinYaw = MathHelper.sin(yaw);
        double cosYaw = MathHelper.cos(yaw);
        double offsetX = (left ? -1 : 1) * 0.35D;
        double offsetY = 0.525D;
        double offsetZ = 0.125D;
        double x_P = player.prevPosX + (player.getPosX() - player.prevPosX) * (double)partialTicks - cosYaw*offsetX - sinYaw*offsetZ;
        double y_P = player.prevPosY + (double)player.getEyeHeight() + (player.getPosY() - player.prevPosY)*partialTicks - offsetY;
        double z_P = player.prevPosZ + (player.getPosZ() - player.prevPosZ) * (double)partialTicks - sinYaw*offsetX + cosYaw*offsetZ;
        float deltaY = player.isSneaking() ? -0.1875F : 0.0F;
        //interpolate the dart's position based on its movement
        double x_D = MathHelper.lerp(partialTicks, dart.prevPosX, dart.getPosX());
        double y_D = MathHelper.lerp(partialTicks, dart.prevPosY, dart.getPosY()) + 0.25D;
        double z_D = MathHelper.lerp(partialTicks, dart.prevPosZ, dart.getPosZ());
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        float delta_x = (float) (x_P - x_D);
        float delta_y = (float) (y_P - y_D) + deltaY;
        float delta_z = (float) (z_P - z_D);
        //draw the wire
        renderWire(transforms, buffer, delta_x, delta_y, delta_z, getAmplitude(dart));
    }

    private void renderWire(MatrixStack transforms, IRenderTypeBuffer buffer, float delta_x, float delta_y, float delta_z, float amplitude) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());
        Matrix4f matrix = transforms.getLast().getMatrix();
        int n = 16;
        for(int i = 0; i < n; ++i) {
            this.addLineVertex(delta_x, delta_y, delta_z, builder, matrix, fraction(i, n), amplitude);
            this.addLineVertex(delta_x, delta_y, delta_z, builder, matrix, fraction(i + 1, n), amplitude);
        }
    }

    private float fraction(int step, int max) {
        return (float)step / (float)max;
    }

    private void addQuadVertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder vertexBuilder, int x, int y, int z, float u, float v, int n_x, int n_y, int n_z, int light) {
        vertexBuilder.pos(matrix, (float)x, (float)y, (float)z).color(255, 255, 255, 255).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(normals, (float)n_x, (float)n_z, (float)n_y).endVertex();
    }

    private void addLineVertex(float x, float y, float z, IVertexBuilder builder, Matrix4f matrix, float fraction, float amplitude) {
        builder.pos(matrix, x * fraction, y * fraction - amplitude * MathHelper.sin((float) Math.PI * fraction), z * fraction)
                .color(0, 0, 0, 255
                ).endVertex();
    }

    private float getAmplitude(EntityDart dart) {
        double l = dart.getCableLength();
        double d = dart.calculateDistanceToPlayer();
        if (d == 0) {
            return (float) (l / 2.0);
        }
        if (d >= l) {
            return 0;
        }
        return (float) (l / 2.0 - d / 2.0);
    }

    @Override
    @ParametersAreNonnullByDefault
    public ResourceLocation getEntityTexture(EntityDart entity) {
        return TEXTURE;
    }
}
