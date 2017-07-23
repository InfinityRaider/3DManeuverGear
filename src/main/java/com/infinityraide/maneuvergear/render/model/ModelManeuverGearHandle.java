package com.InfinityRaider.maneuvergear.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelManeuverGearHandle extends ModelBase {
    public static final ResourceLocation TEXTURE = new ResourceLocation("3dmaneuvergear:textures/models/3DGearHandle.png");

    private final ModelRenderer hilt;
    private final ModelRenderer triggers;
    private final ModelRenderer connector;
    private final ModelRenderer handle;

    public ModelManeuverGearHandle() {
        textureWidth = 64;
        textureHeight = 64;

        hilt = new ModelRenderer(this, 0, 0);
        hilt.addBox(0F, 0F, 0F, 4, 4, 12);
        hilt.setRotationPoint(-7F, 10F, -3F);
        hilt.setTextureSize(64, 64);
        hilt.mirror = true;
        setRotation(hilt, 0F, 0F, 0F);
        triggers = new ModelRenderer(this, 33, 0);
        triggers.addBox(0F, 0F, 0F, 2, 3, 5);
        triggers.setRotationPoint(-6F, 13.5F, -3F);
        triggers.setTextureSize(64, 64);
        triggers.mirror = true;
        setRotation(triggers, 0F, 0F, 0F);
        connector = new ModelRenderer(this, 48, 0);
        connector.addBox(0F, 0F, 0F, 2, 4, 2);
        connector.setRotationPoint(-6F, 10F, -5F);
        connector.setTextureSize(64, 64);
        connector.mirror = true;
        setRotation(connector, 0F, 0F, 0F);
        handle = new ModelRenderer(this, 34, 0);
        handle.addBox(0F, 0F, 0F, 0, 6, 10);
        handle.setRotationPoint(-5F, 14F, -5.5F);
        handle.setTextureSize(64, 64);
        handle.mirror = true;
        setRotation(handle, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        super.render(entity, f, f1, f2, f3, f4, f5);

        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        hilt.render(f5);
        triggers.render(f5);
        connector.render(f5);
        handle.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity e, float f, float f1, float f2, float f3, float f4, float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }
}
