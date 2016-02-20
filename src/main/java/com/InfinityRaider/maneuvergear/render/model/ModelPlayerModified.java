package com.InfinityRaider.maneuvergear.render.model;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelPlayerModified extends ModelPlayer {
    private static final ModelPlayerModified mainModel =  new ModelPlayerModified(0.0F, false);
    private static final ModelPlayerModified slim =  new ModelPlayerModified(0.0F, true);

    private float leftArmSwingProgress = 0;

    public ModelPlayerModified(float f, boolean smallArms) {
        super(f, smallArms);
    }

    public static void setLeftArmSwingProgress(float f) {
        mainModel.leftArmSwingProgress = f;
        slim.leftArmSwingProgress = f;
    }

    @Override
    public void setRotationAngles(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Entity entity) {
        super.setRotationAngles(arg0, arg1, arg2, arg3, arg4, arg5, entity);
        if (leftArmSwingProgress > 0) {
            this.bipedHead.rotateAngleY = arg3 / (180F / (float)Math.PI);
            this.bipedHead.rotateAngleX = arg4 / (180F / (float)Math.PI);
            this.bipedRightArm.rotateAngleX = MathHelper.cos(arg0 * 0.6662F + (float) Math.PI) * 2.0F * arg1 * 0.5F;
            this.bipedLeftArm.rotateAngleX = MathHelper.cos(arg0 * 0.6662F) * 2.0F * arg1 * 0.5F;
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightLeg.rotateAngleX = MathHelper.cos(arg0 * 0.6662F) * 1.4F * arg1;
            this.bipedLeftLeg.rotateAngleX = MathHelper.cos(arg0 * 0.6662F + (float)Math.PI) * 1.4F * arg1;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;

            if (this.isRiding) {
                this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
                this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
                this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
                this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
                this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
                this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
            }

            if (this.heldItemLeft != 0) {
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemLeft;
            }

            this.bipedRightArm.rotateAngleY = 0.0F;
            this.bipedRightArm.rotateAngleZ = 0.0F;

            switch (this.heldItemRight) {
                case 0:
                case 2:
                default:
                    break;
                case 1:
                    this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemRight;
                    break;
                case 3:
                    this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemRight;
                    this.bipedRightArm.rotateAngleY = -0.5235988F;
            }

            this.bipedLeftArm.rotateAngleY = 0.0F;

            if (this.swingProgress > -9990.0F) {
                float f1_R = this.swingProgress;
                float f1_L = this.leftArmSwingProgress;
                this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f1_R) * (float)Math.PI * 2.0F) * 0.2F;
                this.bipedBody.rotateAngleY -= MathHelper.sin(MathHelper.sqrt_float(f1_L) * (float)Math.PI * 2.0F) * 0.2F;
                this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
                this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
                f1_R = 1.0F - this.swingProgress;
                f1_R *= f1_R;
                f1_R *= f1_R;
                f1_R = 1.0F - f1_R;
                float f2_R = MathHelper.sin(f1_R * (float)Math.PI);
                float f3_R = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX - ((double)f2_R * 1.2D + (double)f3_R));
                this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
                f1_L = 1.0F - this.leftArmSwingProgress;
                f1_L *= f1_L;
                f1_L *= f1_L;
                f1_L = 1.0F - f1_L;
                float f2_L = MathHelper.sin(f1_L * (float)Math.PI);
                float f3_L = MathHelper.sin(this.leftArmSwingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                this.bipedLeftArm.rotateAngleX = (float)((double)this.bipedLeftArm.rotateAngleX - ((double)f2_L * 1.2D + (double)f3_L));
                this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
                this.bipedLeftArm.rotateAngleZ = MathHelper.sin(this.leftArmSwingProgress * (float)Math.PI) * -0.4F;
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

            this.bipedRightArm.rotateAngleZ += MathHelper.cos(arg2 * 0.09F) * 0.05F + 0.05F;
            this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(arg2 * 0.09F) * 0.05F + 0.05F;
            this.bipedRightArm.rotateAngleX += MathHelper.sin(arg2 * 0.067F) * 0.05F;
            this.bipedLeftArm.rotateAngleX -= MathHelper.sin(arg2 * 0.067F) * 0.05F;

            if (this.aimedBow) {
                float f3 = 0.0F;
                float f4 = 0.0F;
                this.bipedRightArm.rotateAngleZ = 0.0F;
                this.bipedLeftArm.rotateAngleZ = 0.0F;
                this.bipedRightArm.rotateAngleY = -(0.1F - f3 * 0.6F) + this.bipedHead.rotateAngleY;
                this.bipedLeftArm.rotateAngleY = 0.1F - f3 * 0.6F + this.bipedHead.rotateAngleY + 0.4F;
                this.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
                this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
                this.bipedRightArm.rotateAngleX -= f3 * 1.2F - f4 * 0.4F;
                this.bipedLeftArm.rotateAngleX -= f3 * 1.2F - f4 * 0.4F;
                this.bipedRightArm.rotateAngleZ += MathHelper.cos(arg2 * 0.09F) * 0.05F + 0.05F;
                this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(arg2 * 0.09F) * 0.05F + 0.05F;
                this.bipedRightArm.rotateAngleX += MathHelper.sin(arg2 * 0.067F) * 0.05F;
                this.bipedLeftArm.rotateAngleX -= MathHelper.sin(arg2 * 0.067F) * 0.05F;
            }
            copyModelAngles(this.bipedHead, this.bipedHeadwear);copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
            copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
            copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
            copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
            copyModelAngles(this.bipedBody, this.bipedBodyWear);
        }
    }

    public static void replaceOldModel() {
        if(!ConfigurationHandler.getInstance().overridePlayerRenderer) {
            return;
        }
        RenderPlayer renderer = getOldRenderer("default");
        if(renderer == null) {
            LogHelper.debug("Failed overriding left arm swing behaviour");
            return;
        }
        ModelPlayer oldModel = renderer.getMainModel();
        ModelPlayer newModel = null;
        for(Field field : RendererLivingEntity.class.getDeclaredFields()) {
            if(field.getType() == ModelBase.class) {
                try {
                    field.setAccessible(true);
                    Object obj = field.get(renderer);
                    if (obj == oldModel) {
                        newModel = mainModel;
                        field.set(renderer, newModel);
                        break;
                    }
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
        if(newModel != null) {
            //replace relevant fields in RenderPlayer
            replaceEntriesInRenderPlayer(renderer, newModel);
            replaceEntriesInRenderPlayer(ModelPlayerModified.getOldRenderer("slim"), slim);
        }
    }

    @SuppressWarnings("unchecked")
    private static RenderPlayer getOldRenderer(String keyword) {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Map < Class <? extends Entity > , Render<? extends Entity >> entityRenderMap = manager.entityRenderMap;
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
                    LogHelper.printStackTrace(e);
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
        for(Field field : RendererLivingEntity.class.getDeclaredFields()) {
            if(field.getType() == ModelBase.class) {
                field.setAccessible(true);
                try {
                    field.set(renderer, newModel);
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
                break;
            }
        }
    }
}
