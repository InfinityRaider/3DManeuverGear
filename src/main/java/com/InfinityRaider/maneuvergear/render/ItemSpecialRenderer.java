package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.utility.TransformationMatrix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> implements IPerspectiveAwareModel, ISmartItemModel {
    private static final List<BakedQuad> EMPTY_LIST = new ArrayList<BakedQuad>();

    public abstract Class<? extends T> getTileClass();

    public final void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderItem(partialTicks);
    }

    public abstract void renderItem(float partialTicks);

    @Override
    public final Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return new ImmutablePair<IFlexibleBakedModel, Matrix4f>(this, getTransformMatrixForPerspective(cameraTransformType).toMatrix4f());
    }

    public abstract TransformationMatrix getTransformMatrixForPerspective(ItemCameraTransforms.TransformType cameraTransformsType);

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return getRendererForStack(stack);
    }

    public abstract ItemSpecialRenderer<T> getRendererForStack(ItemStack stack);

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing) {
        return EMPTY_LIST;
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        return EMPTY_LIST;
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
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    @Override
    public VertexFormat getFormat() {
        return DefaultVertexFormats.POSITION_TEX_COLOR;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
