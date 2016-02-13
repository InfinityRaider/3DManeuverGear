package com.InfinityRaider.maneuvergear.render.model;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class ModelPlayerModified extends ModelPlayer {
    private static final ModelPlayerModified mainModel =  new ModelPlayerModified(0.0F, false);

    private float leftArmSwingProgress = 0;

    public ModelPlayerModified(float f, boolean smallArms) {
        super(f, smallArms);
    }

    public static void setLeftArmSwingProgress(float f) {
        mainModel.leftArmSwingProgress = f;
    }

    @Override
    public void setRotationAngles(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5, Entity entity) {
        if (leftArmSwingProgress > 0) {
            //TODO
        } else {
            super.setRotationAngles(arg0, arg1, arg2, arg3, arg4, arg5, entity);
        }
    }

    public static void replaceOldModel() {
        if(!ConfigurationHandler.overridePlayerRenderer) {
            return;
        }
        RenderPlayer renderer = getOldRenderer();
        if(renderer == null) {
            LogHelper.debug("Failed overriding left arm swing behaviour");
            return;
        }
        ModelBiped oldModel = renderer.getMainModel();
        ModelBiped newModel = null;
        for(Field field : RendererLivingEntity.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object obj = field.get(renderer);
                if(obj == oldModel) {
                    newModel = mainModel;
                    field.set(renderer, newModel);
                    break;
                }
            } catch(Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
        if(newModel != null) {
            for(Field field : renderer.getClass().getFields()) {
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

    private static RenderPlayer getOldRenderer() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        for(Field field : renderManager.getClass().getDeclaredFields()) {
            if(field.getType() == RenderPlayer.class) {
                field.setAccessible(true);
                try {
                    return (RenderPlayer) field.get(renderManager);
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
        return null;
    }
}
