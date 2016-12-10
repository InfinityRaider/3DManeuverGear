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
import com.infinityraider.infinitylib.utility.math.TransformationMatrix;
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
        tessellator.addQuads(model.getBakedQuads(format, sprite, 8 * Constants.UNIT));
    }

    protected void renderBlade(ITessellator tessellator) {
        tessellator.pushMatrix();
        double dx = -0.4075;
        double dy = -0.325;
        double dz = 0.78;
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
            case FIRST_PERSON_LEFT_HAND: return this.matrixFirstPersonLeft;
            case FIRST_PERSON_RIGHT_HAND: return this.matrixFirstPersonRight;
            case THIRD_PERSON_LEFT_HAND: return this.matrixThirdPersonLeft;
            case THIRD_PERSON_RIGHT_HAND: return this.matrixThirdPersonRight;
            case GROUND: return this.matrixGround;
            case GUI: return this.matrixGui;
        }
        return this.none;
    }

    private Matrix4f transformFirstPersonLeft() {
        float u = Constants.UNIT;
        float dx = 18 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        matrix.multiplyRightWith(new TransformationMatrix(180, 0, 1, 0));
        matrix.multiplyRightWith(new TransformationMatrix(-90, 1,  0, 0));
        return matrix.toMatrix4f();
    }

    private Matrix4f transformFirstPersonRight() {
        float u = Constants.UNIT;
        float dx = 5 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        matrix.multiplyRightWith(new TransformationMatrix(180, 0, 1, 0));
        matrix.multiplyRightWith(new TransformationMatrix(-90, 1,  0, 0));
        return matrix.toMatrix4f();
    }

    private Matrix4f transformThirdPersonLeft() {
        float u = Constants.UNIT;
        float dx = 6.5F * u;
        float dy = 3 * u;
        float dz = 2.5F * u;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        matrix.multiplyRightWith(new TransformationMatrix(180, 0, 1, 0));
        matrix.multiplyRightWith(new TransformationMatrix(-90, 1,  0, 0));
        return matrix.toMatrix4f();
    }

    private Matrix4f transformThirdPersonRight() {
        float u = Constants.UNIT;
        float dx = -6.5F * u;
        float dy = 3 * u;
        float dz = 2.5F * u;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        matrix.multiplyRightWith(new TransformationMatrix(180, 0, 1, 0));
        matrix.multiplyRightWith(new TransformationMatrix(-90, 1,  0, 0));
        return matrix.toMatrix4f();
    }

    private Matrix4f transformGround() {
        float u = Constants.UNIT;
        float dx = 6.25F * u;
        float dy = 8 * u;
        float dz = 0 * u;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        return matrix.toMatrix4f();
    }

    private Matrix4f transformGui() {
        float u = Constants.UNIT;
        float dx = -6 * u;
        float dy = 2 * u;
        float dz = 0;
        TransformationMatrix matrix = new TransformationMatrix(dx, dy, dz);
        matrix.multiplyRightWith(new TransformationMatrix(-90, 1,  0, 0));
        matrix.multiplyRightWith(new TransformationMatrix(45, 0, 1, 0));
        matrix.multiplyRightWith(new TransformationMatrix(135, 0, 0, 1));
        matrix.scale(0.9, 0.9, 0.9);
        return matrix.toMatrix4f();
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
