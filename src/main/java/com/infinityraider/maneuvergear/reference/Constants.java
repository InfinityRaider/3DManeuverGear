package com.infinityraider.maneuvergear.reference;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

public class Constants {
    /**
     * The number of units in a block.
     */
    public static final int WHOLE = 16;

    /**
     * The value of 1/16 as represented in float form. Pre-calculated as to cut
     * back on calculations.
     */
    public static final float UNIT = 1.0f / WHOLE;

    /**
     * Default key references and total key count
     */
    @OnlyIn(Dist.CLIENT)
    public static final int KEY_X = org.lwjgl.glfw.GLFW.GLFW_KEY_X;
    @OnlyIn(Dist.CLIENT)
    public static final int KEY_Z = org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
    @OnlyIn(Dist.CLIENT)
    public static final int KEYBOARD_SIZE = GLFW.GLFW_KEY_LAST;
}
