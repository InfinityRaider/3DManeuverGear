package com.InfinityRaider.maneuvergear.render.model;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelPlayerCustomized extends ModelPlayer {
    private static final ModelPlayerCustomized MODEL_MAIN =  new ModelPlayerCustomized(0.0F, false);
    private static final ModelPlayerCustomized MODEL_SLIM =  new ModelPlayerCustomized(0.0F, true);

    private float swingProgressLeft;
    private float swingProgressRight;

    private ModelPlayerCustomized(float modelSize, boolean smallArmsIn) {
        super(modelSize, smallArmsIn);
    }

    public void setSwingProgress(float left, float right) {
        this.swingProgressLeft = left;
        this.swingProgressRight = right;
    }

    @Override
    @SuppressWarnings("incomplete-switch")
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        boolean flag = entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getTicksElytraFlying() > 4;
        this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
        if (flag) {
            this.bipedHead.rotateAngleX = -((float)Math.PI / 4F);
        } else {
            this.bipedHead.rotateAngleX = headPitch * 0.017453292F;
        }
        this.bipedBody.rotateAngleY = 0.0F;
        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedRightArm.rotationPointX = -5.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointX = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float)(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ);
            f = f / 0.2F;
            f = f * f * f;
        }
        if (f < 1.0F) {
            f = 1.0F;
        }
        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;
        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;
        if (this.isRiding) {
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }
        this.bipedRightArm.rotateAngleY = 0.0F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        switch (this.leftArmPose) {
            case EMPTY:
                this.bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedLeftArm.rotateAngleY = 0.0F;
        }
        switch (this.rightArmPose) {
            case EMPTY:
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.bipedRightArm.rotateAngleY = 0.0F;
        }
        if(this.swingProgressLeft > 0.0F || this.swingProgressRight > 0.0F) {
            if (this.swingProgressLeft > 0.0F) {
                EnumHandSide enumhandside = EnumHandSide.LEFT;
                ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
                this.getArmForSide(enumhandside.opposite());
                float f1 = this.swingProgressLeft;
                this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f1) * ((float) Math.PI * 2F)) * 0.2F;
                this.bipedBody.rotateAngleY *= -1.0F;
                this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
                f1 = 1.0F - this.swingProgressLeft;
                f1 = f1 * f1;
                f1 = f1 * f1;
                f1 = 1.0F - f1;
                float f2 = MathHelper.sin(f1 * (float) Math.PI);
                float f3 = MathHelper.sin(this.swingProgressLeft * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
                modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgressLeft * (float) Math.PI) * -0.4F;
            } else {
                EnumHandSide enumhandside = EnumHandSide.RIGHT;
                ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
                this.getArmForSide(enumhandside.opposite());
                float f1 = this.swingProgressRight;
                this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f1) * ((float) Math.PI * 2F)) * 0.2F;
                this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
                f1 = 1.0F - this.swingProgressRight;
                f1 = f1 * f1;
                f1 = f1 * f1;
                f1 = 1.0F - f1;
                float f2 = MathHelper.sin(f1 * (float) Math.PI);
                float f3 = MathHelper.sin(this.swingProgressRight * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
                modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgressRight * (float) Math.PI) * -0.4F;
                modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgressRight * (float) Math.PI) * -0.4F;
            }
        } else if (this.swingProgress > 0.0F) {
            EnumHandSide enumhandside = this.getMainHand(entity);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
            this.getArmForSide(enumhandside.opposite());
            float f1 = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f1) * ((float)Math.PI * 2F)) * 0.2F;
            if (enumhandside == EnumHandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0F;
            }
            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float)Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
        }
        if (this.isSneak) {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;
            this.bipedRightLeg.rotationPointY = 9.0F;
            this.bipedLeftLeg.rotationPointY = 9.0F;
            this.bipedHead.rotationPointY = 1.0F;
        } else {
            this.bipedBody.rotateAngleX = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;
            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;
            this.bipedHead.rotationPointY = 0.0F;
        }
        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
        }
        copyModelAngles(this.bipedHead, this.bipedHeadwear);
    }

    public static void replaceOldModel() {
        if(!ConfigurationHandler.getInstance().overridePlayerRenderer) {
            return;
        }
        RenderPlayer renderer = getOldRenderer("default");
        if(renderer == null) {
            ManeuverGear.instance.getLogger().debug("Failed overriding left arm swing behaviour");
            return;
        }
        ModelPlayer oldModel = renderer.getMainModel();
        ModelPlayer newModel = null;
        for(Field field : RenderLivingBase.class.getDeclaredFields()) {
            if(field.getType() == ModelBase.class) {
                try {
                    field.setAccessible(true);
                    Object obj = field.get(renderer);
                    if (obj == oldModel) {
                        newModel = MODEL_MAIN;
                        field.set(renderer, newModel);
                        break;
                    }
                } catch (Exception e) {
                    ManeuverGear.instance.getLogger().printStackTrace(e);
                }
            }
        }
        if(newModel != null) {
            //replace relevant fields in RenderPlayer
            replaceEntriesInRenderPlayer(renderer, newModel);
            replaceEntriesInRenderPlayer(getOldRenderer("slim"), MODEL_SLIM);
        }
    }

    @SuppressWarnings("unchecked")
    private static RenderPlayer getOldRenderer(String keyword) {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Map< Class <? extends Entity > , Render<? extends Entity >> entityRenderMap = manager.entityRenderMap;
        for(Field field : manager.getClass().getDeclaredFields()) {
            if(field.getType() == Map.class) {
                field.setAccessible(true);
                try {
                    Object obj = field.get(manager);
                    if(obj == entityRenderMap) {
                        continue;
                    }
                    Map<String, RenderPlayer> skinMap = (Map<String, RenderPlayer>) obj;
                    return skinMap.get(keyword);
                } catch (IllegalAccessException e) {
                    ManeuverGear.instance.getLogger().printStackTrace(e);
                }
            }
        }
        return null;
    }

    private static void replaceEntriesInRenderPlayer(RenderPlayer renderer, ModelPlayer newModel) {
        if(renderer == null) {
            return;
        }
        //replace relevant fields in RenderPlayer
        for(Field field : RenderLivingBase.class.getDeclaredFields()) {
            if(field.getType() == ModelBase.class) {
                field.setAccessible(true);
                try {
                    field.set(renderer, newModel);
                } catch (IllegalAccessException e) {
                    ManeuverGear.instance.getLogger().printStackTrace(e);
                }
                break;
            }
        }
    }
}
