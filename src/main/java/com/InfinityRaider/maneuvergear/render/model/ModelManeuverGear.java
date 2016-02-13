package com.InfinityRaider.maneuvergear.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelManeuverGear extends ModelBase {
    private static final ModelManeuverGearSide holster = new ModelManeuverGearSide();
    private static final ModelManeuverGearGirdle girdle = new ModelManeuverGearGirdle();

    @Override
    //period amplitude and phase are for the swinging of arms and legs
    public void render(Entity e, float period, float amplitude, float phase, float headAngleY, float headAngleX, float scale) {
        float dx = 0.35F;
        //render the left holster
        renderHolster(dx, e, period, amplitude, phase, headAngleY, headAngleX, scale);
        //render the right holster
        renderHolster(-dx, e, period, amplitude, phase, headAngleY, headAngleX, scale);
        //render the girdle
        renderGirdle(e, period, amplitude, phase, headAngleY, headAngleX, scale);
    }

    private void renderHolster(float dx, Entity e, float period, float amplitude, float phase, float headAngleY, float headAngleX, float f) {
        float scale = 0.01F;
        float dy =  0.8F;
        float dz = 0;
        float angle = 30;
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(angle, -1, 0, 0);
        GL11.glScalef(scale, scale, scale);
        holster.render(e, period, amplitude, phase, headAngleY, headAngleX, f);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glRotatef(-angle, -1, 0, 0);
        GL11.glTranslatef(-dx, -dy, -dz);
    }

    private void renderGirdle(Entity e, float period, float amplitude, float phase, float headAngleY, float headAngleX, float f) {
        float scale = 0.02F;
        float dx = 0;
        float dy =  0.4F;
        float dz = 0.1F;
        float angle = 180;
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(angle, 0, 1, 0);
        GL11.glScalef(scale, scale, scale);
        girdle.render(e, period, amplitude, phase, headAngleY, headAngleX, f);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GL11.glRotatef(-angle, 0, 1, 0);
        GL11.glTranslatef(-dx, -dy, -dz);
    }
}
