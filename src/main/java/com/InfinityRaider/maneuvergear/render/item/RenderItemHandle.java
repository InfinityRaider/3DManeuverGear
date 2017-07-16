package com.InfinityRaider.maneuvergear.render.item;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.reference.Constants;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.render.DefaultTransforms;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import java.util.List;


@SideOnly(Side.CLIENT)
public class RenderItemHandle extends RenderUtilBase implements IItemRenderingHandler, DefaultTransforms.Transformer {
    public static final ResourceLocation TEXTURE = new ResourceLocation("3dmaneuvergear:models/3DGearHandle");

    private final ModelTechne<ModelManeuverGearHandle> model;

    private final Matrix4f matrixFirstPersonLeft;
    private final Matrix4f matrixFirstPersonRight;
    private final Matrix4f matrixThirdPersonLeft;
    private final Matrix4f matrixThirdPersonRight;
    private final Matrix4f matrixGround;
    private final Matrix4f matrixGui;
    private final Matrix4f none;

    public RenderItemHandle() {
        super();
        this.model = new ModelTechne<>(new ModelManeuverGearHandle());
        this.matrixFirstPersonLeft = this.transformFirstPersonLeft();
        this.matrixFirstPersonRight = this.transformFirstPersonRight();
        this.matrixThirdPersonLeft = this.transformThirdPersonLeft();
        this.matrixThirdPersonRight = this.transformThirdPersonRight();
        this.matrixGround = this.transformGround();
        this.matrixGui = this.transformGui();
        this.none = new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    @Override
    public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = tessellator.getIcon(TEXTURE);
        VertexFormat format = tessellator.getVertexFormat();

        //blade quads
        if(shouldRenderBlade(stack)) {
            renderBlade(tessellator);
        }

        //handle quads
        tessellator.addQuads(model.getBakedQuads(format, sprite, 8.0F * Constants.UNIT));
    }

    protected void renderBlade(ITessellator tessellator) {
        tessellator.pushMatrix();
        float dx = -0.4075F;
        float dy = -0.325F;
        float dz = 0.78F;
        tessellator.translate(dx, dy, dz);
        tessellator.rotate(90, 0, 1, 0);
        tessellator.rotate(45, 0, 0, 1);
        ItemStack swordBlade = ItemResource.EnumSubItems.SWORD_BLADE.getStack();
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(swordBlade);
        tessellator.addQuads(model.getQuads(null, null, 0));
        tessellator.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public DefaultTransforms.Transformer getPerspectiveTransformer() {
        return this;
    }

    @Override
    public Matrix4f apply(ItemCameraTransforms.TransformType transformType) {
        switch (transformType) {
            /*
            case FIRST_PERSON_LEFT_HAND: return this.matrixFirstPersonLeft;
            case FIRST_PERSON_RIGHT_HAND: return this.matrixFirstPersonRight;
            case THIRD_PERSON_LEFT_HAND: return this.matrixThirdPersonLeft;
            case THIRD_PERSON_RIGHT_HAND: return this.matrixThirdPersonRight;
            case GROUND: return this.matrixGround;
            case GUI: return this.matrixGui;
            */
            case FIRST_PERSON_LEFT_HAND: return this.transformFirstPersonLeft();
            case FIRST_PERSON_RIGHT_HAND: return this.transformFirstPersonRight();
            case THIRD_PERSON_LEFT_HAND: return this.transformThirdPersonLeft();
            case THIRD_PERSON_RIGHT_HAND: return this.transformThirdPersonRight();
            case GROUND: return this.transformGround();
            case GUI: return this.transformGui();
        }
        return this.none;
    }

    //TODO: convert DefaultTransforms to JOML as well
    private Matrix4f convertFromJOML(org.joml.Matrix4f m) {
        m.transpose();
        return new Matrix4f(
                m.m00(), m.m01(), m.m02(), m.m03(),
                m.m10(), m.m11(), m.m12(), m.m13(),
                m.m20(), m.m21(), m.m22(), m.m23(),
                m.m30(), m.m31(), m.m32(), m.m33()
                );
    }

    private Matrix4f transformFirstPersonLeft() {
        float u = Constants.UNIT;
        float dx = 18 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz)
                .rotate((float) Math.toRadians(180), 0, 1, 0)
                .rotate((float) Math.toRadians(-90), 1,  0, 0);
        return this.convertFromJOML(m);
    }

    private Matrix4f transformFirstPersonRight() {
        float u = Constants.UNIT;
        float dx = 5 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz)
                .rotate((float) Math.toRadians(180), 0, 1, 0)
                .rotate((float) Math.toRadians(-90), 1,  0, 0);
        return this.convertFromJOML(m);
    }

    private Matrix4f transformThirdPersonLeft() {
        float u = Constants.UNIT;
        float dx = 6.5F * u;
        float dy = 3 * u;
        float dz = 2.5F * u;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz)
                .rotate((float) Math.toRadians(180), 0, 1, 0)
                .rotate((float) Math.toRadians(-90), 1,  0, 0);
        return this.convertFromJOML(m);
    }

    private Matrix4f transformThirdPersonRight() {
        float u = Constants.UNIT;
        float dx = -6.5F * u;
        float dy = 3 * u;
        float dz = 2.5F * u;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz)
                .rotate((float) Math.toRadians(180), 0, 1, 0)
                .rotate((float) Math.toRadians(-90), 1,  0, 0);
        return this.convertFromJOML(m);
    }

    private Matrix4f transformGround() {
        float u = Constants.UNIT;
        float dx = 6.25F * u;
        float dy = 8 * u;
        float dz = 0 * u;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz);
        return this.convertFromJOML(m);
    }

    private Matrix4f transformGui() {
        float u = Constants.UNIT;
        float dx = -6 * u;
        float dy = 2 * u;
        float dz = 0;
        org.joml.Matrix4f m = new org.joml.Matrix4f().assumeNothing()
                .translate(dx, dy, dz)
                .rotate((float) Math.toRadians(-90), 1,  0, 0)
                .rotate((float) Math.toRadians(45), 0, 1, 0)
                .rotate((float) Math.toRadians(135), 0, 0, 1)
                .scaling(0.9F, 0.9F, 0.9F);
        return this.convertFromJOML(m);
    }

    private boolean shouldRenderBlade(ItemStack stack) {
        return !(stack == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) && ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getAllTextures() {
        return ImmutableList.of(TEXTURE);
    }
}
