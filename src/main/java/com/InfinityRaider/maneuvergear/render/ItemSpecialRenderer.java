package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.utility.TransformationMatrix;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ItemSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> implements IPerspectiveAwareModel, IBakedModel {
    private static final List<BakedQuad> EMPTY_LIST = new ArrayList<>();

    private Map<Long, ItemStack> stacksBeingRendered;

    protected ItemSpecialRenderer() {
        this.stacksBeingRendered = new HashMap<>();
    }

    public abstract Class<? extends T> getTileClass();

    public final void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderItem(stacksBeingRendered.get(Thread.currentThread().getId()), partialTicks);
    }

    public abstract void renderItem(ItemStack stack, float partialTicks);

    @Override
    public final Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return new ImmutablePair<ItemSpecialRenderer<T>, Matrix4f>(this, getTransformMatrixForPerspective(cameraTransformType).toMatrix4f());
    }

    public abstract TransformationMatrix getTransformMatrixForPerspective(ItemCameraTransforms.TransformType cameraTransformsType);

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }
}
