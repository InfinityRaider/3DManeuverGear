package com.InfinityRaider.maneuvergear.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ModelManeuverGearHandle extends ModelBase implements ISmartItemModel {
    private static final ResourceLocation texture = new ResourceLocation("3dmaneuvergear:textures/models/3DGearHandle.png");

    ModelRenderer Hilt;
    ModelRenderer Triggers;
    ModelRenderer Connector;
    ModelRenderer Handle;

    public ModelManeuverGearHandle() {
        textureWidth = 64;
        textureHeight = 32;

        Hilt = new ModelRenderer(this, 0, 0);
        Hilt.addBox(0F, 0F, 0F, 4, 4, 12);
        Hilt.setRotationPoint(-7F, 10F, -3F);
        Hilt.setTextureSize(64, 32);
        Hilt.mirror = true;
        setRotation(Hilt, 0F, 0F, 0F);
        Triggers = new ModelRenderer(this, 33, 0);
        Triggers.addBox(0F, 0F, 0F, 2, 3, 5);
        Triggers.setRotationPoint(-6F, 13.5F, -3F);
        Triggers.setTextureSize(64, 32);
        Triggers.mirror = true;
        setRotation(Triggers, 0F, 0F, 0F);
        Connector = new ModelRenderer(this, 48, 0);
        Connector.addBox(0F, 0F, 0F, 2, 4, 2);
        Connector.setRotationPoint(-6F, 10F, -5F);
        Connector.setTextureSize(64, 32);
        Connector.mirror = true;
        setRotation(Connector, 0F, 0F, 0F);
        Handle = new ModelRenderer(this, 34, 0);
        Handle.addBox(0F, 0F, 0F, 0, 6, 10);
        Handle.setRotationPoint(-5F, 14F, -5.5F);
        Handle.setTextureSize(64, 32);
        Handle.mirror = true;
        setRotation(Handle, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        super.render(entity, f, f1, f2, f3, f4, f5);

        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        Hilt.render(f5);
        Triggers.render(f5);
        Connector.render(f5);
        Handle.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4, float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return null;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
        return null;
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        return null;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }
}
