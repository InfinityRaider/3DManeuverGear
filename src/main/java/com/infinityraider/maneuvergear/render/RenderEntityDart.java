package com.infinityraider.maneuvergear.render;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.entity.EntityDart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
public class RenderEntityDart extends EntityRenderer<EntityDart> implements IRenderUtilities {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ManeuverGear.instance.getModId() + ":textures/entities/dart.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE);

    public RenderEntityDart(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(EntityDart dart, float yaw, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light) {
        this.renderEntity(dart, partialTicks, transforms, buffer, light);
        Player player = dart.getOwner();
        if (player == null) {
            return;
        }
        if (this.getPointOfView().ordinal() > 0 || player != Minecraft.getInstance().player) {
            this.renderWireThirdPerson(dart, player, partialTicks, transforms, buffer);
        } else {
            renderWireFirstPerson(dart, player, partialTicks, transforms, buffer);
        }
    }

    private void renderEntity(EntityDart dart, float partialTicks, PoseStack transforms, MultiBufferSource buffer, int light) {
        transforms.pushPose();

        transforms.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, dart.yRotO, dart.getYRot()) - 90.0F));
        transforms.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, dart.xRotO, dart.getXRot())));

        transforms.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        transforms.scale(0.05625F, 0.05625F, 0.05625F);
        transforms.translate(-4.0D, 0.0D, 0.0D);

        VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose entry = transforms.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();

        this.addQuadVertex(matrix4f, matrix3f, builder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);

        this.addQuadVertex(matrix4f, matrix3f, builder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        this.addQuadVertex(matrix4f, matrix3f, builder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

        for(int j = 0; j < 4; ++j) {
            transforms.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.addQuadVertex(matrix4f, matrix3f, builder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, builder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, builder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
            this.addQuadVertex(matrix4f, matrix3f, builder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
        }
        transforms.popPose();
    }

    private void renderWireFirstPerson(EntityDart dart, Player player, float partialTicks, PoseStack transforms, MultiBufferSource buffer) {
        boolean left = dart.isLeft();
        float yaw = -(player.xRotO + (player.getXRot() - player.xRotO) * partialTicks) * ((float) Math.PI / 180F);
        float pitch = -(player.yRotO + (player.getYRot() - player.yRotO) * partialTicks) * ((float) Math.PI / 180F);
        //this vector defines the location of the points on the screen in first person
        double c1 = (left ? 0.8D : -0.8D);
        double c2 = -0.8D;
        double c3 = 1D;
        float deltaY = player.getEyeHeight();
        Vec3 vec3d = new Vec3(c1, c2, c3);
        vec3d = vec3d.xRot(pitch);
        vec3d = vec3d.yRot(yaw);
        //find the player's position, interpolating based on his movement
        double x_P = Mth.lerp(partialTicks, player.xOld, player.getX()) + vec3d.x();
        double y_P = Mth.lerp(partialTicks, player.yOld, player.getY()) + vec3d.y();
        double z_P = Mth.lerp(partialTicks, player.zOld, player.getZ()) + vec3d.z();
        //interpolate the dart's position based on its movement
        double x_D = Mth.lerp(partialTicks, dart.xOld, dart.getX());
        double y_D = Mth.lerp(partialTicks, dart.yOld, dart.getY()) + 0.25D;
        double z_D = Mth.lerp(partialTicks, dart.zOld, dart.getZ());
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        float delta_x = (float) (x_P - x_D);
        float delta_y = (float) (y_P - y_D) + deltaY;
        float delta_z = (float) (z_P - z_D);
        //draw the wire
        renderWire(transforms, buffer, delta_x, delta_y, delta_z, getAmplitude(dart));
    }

    private void renderWireThirdPerson(EntityDart dart, Player player, float partialTicks, PoseStack transforms, MultiBufferSource buffer) {
        boolean left = dart.isLeft();
        //find the player's position, interpolating based on his movement
        float yaw = (player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks) * ((float) Math.PI / 180F);
        double sinYaw = Mth.sin(yaw);
        double cosYaw = Mth.cos(yaw);
        double offsetX = (left ? -1 : 1) * 0.3D;
        double offsetY = 0.52D;
        double offsetZ = 0.D;
        double x_P = player.xOld + (player.getX() - player.xOld) * (double)partialTicks - cosYaw*offsetX - sinYaw*offsetZ;
        double y_P = player.yOld + (double)player.getEyeHeight() + (player.getY() - player.yOld)*partialTicks - offsetY;
        double z_P = player.zOld + (player.getZ() - player.zOld) * (double)partialTicks - sinYaw*offsetX + cosYaw*offsetZ;
        float deltaY = player.isDiscrete() ? -0.1875F : 0.0F;
        //interpolate the dart's position based on its movement
        double x_D = Mth.lerp(partialTicks, dart.xOld, dart.getX());
        double y_D = Mth.lerp(partialTicks, dart.yOld, dart.getY()) + 0.25D;
        double z_D = Mth.lerp(partialTicks, dart.zOld, dart.getZ());
        //transform the coordinates of the cable's end attached to the player to the reference system of the dart
        float delta_x = (float) (x_P - x_D);
        float delta_y = (float) (y_P - y_D) + deltaY;
        float delta_z = (float) (z_P - z_D);
        //draw the wire
        renderWire(transforms, buffer, delta_x, delta_y, delta_z, getAmplitude(dart));
    }

    private void renderWire(PoseStack transforms, MultiBufferSource buffer, float delta_x, float delta_y, float delta_z, float amplitude) {
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());
        Matrix4f matrix = transforms.last().pose();
        int n = 16;
        for(int i = 0; i < n; ++i) {
            this.addLineVertex(delta_x, delta_y, delta_z, builder, matrix, fraction(i, n), amplitude);
            this.addLineVertex(delta_x, delta_y, delta_z, builder, matrix, fraction(i + 1, n), amplitude);
        }
    }

    private float fraction(int step, int max) {
        return (float)step / (float)max;
    }

    private void addQuadVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, int x, int y, int z, float u, float v, int n_x, int n_y, int n_z, int light) {
        vertexBuilder.vertex(matrix, (float)x, (float)y, (float)z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normals, (float)n_x, (float)n_z, (float)n_y).endVertex();
    }

    private void addLineVertex(float x, float y, float z, VertexConsumer builder, Matrix4f matrix, float fraction, float amplitude) {
        builder.vertex(matrix, x * fraction, y * fraction - amplitude * Mth.sin((float) Math.PI * fraction), z * fraction)
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
    public ResourceLocation getTextureLocation(EntityDart pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityRenderDispatcher getEntityRendererManager() {
        return this.entityRenderDispatcher;
    }
}
