package com.InfinityRaider.maneuvergear.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ModelManeuverGearGirdle extends ModelBase {
    private ResourceLocation texture = new ResourceLocation("3dmaneuvergear:textures/models/3DGearGirdle.png");

    //fields
    ModelRenderer Girdle;
    ModelRenderer DartLauncherLeft;
    ModelRenderer DartLauncherRight;
    ModelRenderer RopeCoil1;
    ModelRenderer RopeCoil2;
    ModelRenderer GasVent;
    ModelRenderer Turbine;

    public ModelManeuverGearGirdle() {
        textureWidth = 256;
        textureHeight = 32;

        Girdle = new ModelRenderer(this, 0, 0);
        Girdle.addBox(0F, 0F, 0F, 27, 6, 15);
        Girdle.setRotationPoint(-13.5F, 9F, -2.5F);
        Girdle.setTextureSize(256, 32);
        Girdle.mirror = true;
        setRotation(Girdle, 0F, 0F, 0F);
        DartLauncherLeft = new ModelRenderer(this, 86, 0);
        DartLauncherLeft.addBox(0F, 0F, 0F, 5, 7, 10);
        DartLauncherLeft.setRotationPoint(13.5F, 9F, 0F);
        DartLauncherLeft.setTextureSize(256, 32);
        DartLauncherLeft.mirror = true;
        setRotation(DartLauncherLeft, 0F, 0F, 0F);
        DartLauncherRight = new ModelRenderer(this, 86, 0);
        DartLauncherRight.addBox(0F, 0F, 0F, 5, 7, 10);
        DartLauncherRight.setRotationPoint(-18.5F, 9F, 0F);
        DartLauncherRight.setTextureSize(256, 32);
        DartLauncherRight.mirror = true;
        setRotation(DartLauncherRight, 0F, 0F, 0F);
        RopeCoil1 = new ModelRenderer(this, 119, 0);
        RopeCoil1.addBox(0F, 0F, 0F, 3, 9, 9);
        RopeCoil1.setRotationPoint(-7.466667F, 8F, -11F);
        RopeCoil1.setTextureSize(256, 32);
        RopeCoil1.mirror = true;
        setRotation(RopeCoil1, 0.7853982F, -0.7330383F, 0.2094395F);
        RopeCoil2 = new ModelRenderer(this, 119, 0);
        RopeCoil2.mirror = true;
        RopeCoil2.addBox(0F, 0F, 0F, 3, 9, 9);
        RopeCoil2.setRotationPoint(5.5F, 8F, -8F);
        RopeCoil2.setTextureSize(256, 32);
        RopeCoil2.mirror = true;
        setRotation(RopeCoil2, 0.7853982F, 0.7330383F, -0.2094395F);
        RopeCoil2.mirror = false;
        GasVent = new ModelRenderer(this, 0, 22);
        GasVent.addBox(0F, 0F, 0F, 10, 7, 3);
        GasVent.setRotationPoint(-5.5F, 10F, -5.5F);
        GasVent.setTextureSize(256, 32);
        GasVent.mirror = true;
        setRotation(GasVent, 0F, 0F, 0F);
        Turbine = new ModelRenderer(this, 86, 19);
        Turbine.addBox(0F, 0F, 0F, 6, 5, 6);
        Turbine.setRotationPoint(-3.5F, 7F, -7F);
        Turbine.setTextureSize(256, 32);
        Turbine.mirror = true;
        setRotation(Turbine, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Girdle.render(f5);
        DartLauncherLeft.render(f5);
        DartLauncherRight.render(f5);
        RopeCoil1.render(f5);
        RopeCoil2.render(f5);
        GasVent.render(f5);
        Turbine.render(f5);
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
