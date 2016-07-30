package com.InfinityRaider.maneuvergear.render.item;

import com.infinityraider.infinitylib.item.ICustomRenderedItem;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class RenderItemBase<I extends Item & ICustomRenderedItem> extends RenderUtilBase implements IItemRenderingHandler<I> {
    private final I item;

    protected RenderItemBase(I item) {
        this.item = item;
    }

    @Override
    public I getItem() {
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ResourceLocation> getAllTextures() {
        return getItem().getTextures();
    }

    @Override
    public abstract void renderItem(ITessellator tessellator, World world, I item, ItemStack stack,
                                    EntityLivingBase entity, ItemCameraTransforms.TransformType type, VertexFormat format);
}
