package com.InfinityRaider.maneuvergear.render.item;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.reference.Constants;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import com.infinityraider.infinitylib.render.item.IItemRenderingHandler;
import com.infinityraider.infinitylib.render.model.ModelTechne;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


@SideOnly(Side.CLIENT)
public class RenderItemHandle extends RenderUtilBase implements IItemRenderingHandler {
    public static final ResourceLocation TEXTURE = new ResourceLocation("3dmaneuvergear:models/3DGearHandle");

    private final ModelTechne<ModelManeuverGearHandle> model;
    private final IdentityHashMap<VertexFormat, List<BakedQuad>> handleQuads;
    private final IdentityHashMap<VertexFormat, Map<EnumFacing, List<BakedQuad>>> bladeQuads;

    public RenderItemHandle() {
        super();
        this.model = new ModelTechne<>(new ModelManeuverGearHandle());
        this.handleQuads = new IdentityHashMap<>();
        this.bladeQuads = new IdentityHashMap<>();
    }
    @Override
    public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
        //TODO: fix this
        ItemCameraTransforms.TransformType type = ItemCameraTransforms.TransformType.NONE;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = tessellator.getIcon(TEXTURE);
        applyCameraTransforms(type);
        VertexFormat format = tessellator.getVertexFormat();

        //blade quads
        if(shouldRenderBlade(type, stack)) {
            renderBlade(tessellator);
        }

        //handle quads
        if(!handleQuads.containsKey(format)) {
            //handleQuads.put(format, model.getBakedQuads(format, sprite, 8 * Constants.UNIT));
        }
        tessellator.addQuads(model.getBakedQuads(format, sprite, 8 * Constants.UNIT));
    }

    private void renderBlade(ITessellator tessellator) {
        tessellator.pushMatrix();
        double dx = -0.375;
        double dy = 0.975;
        double dz = -0.45;
        tessellator.translate(dx, dy, dz);
        tessellator.rotate(-45, 0, 0, 1);
        tessellator.rotate(-90, 1, 0, 0);
        tessellator.rotate(45, 0, 1, 0);
        tessellator.rotate(-45, 0, 0, 1);
        //tessellator.scale(1, 1, 0.5);
        ItemStack swordBlade = ItemResource.EnumSubItems.SWORD_BLADE.getStack();
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(swordBlade);
        tessellator.addQuads(model.getQuads(null, null, 0));
        tessellator.popMatrix();
    }

    public void applyCameraTransforms(ItemCameraTransforms.TransformType type) {
        switch(type) {
            case FIRST_PERSON_LEFT_HAND: transformFirstPersonLeft(); break;
            case FIRST_PERSON_RIGHT_HAND: transformFirstPersonRight(); break;
            case THIRD_PERSON_LEFT_HAND: transformThirdPerson(); break;
            case THIRD_PERSON_RIGHT_HAND: transformThirdPerson(); break;
            case GROUND: transformGround(); break;
            case GUI: transformGui(); break;
        }
    }

    private void transformFirstPersonLeft() {
        float u = Constants.UNIT;
        float dx = 5 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformFirstPersonRight() {
        float u = Constants.UNIT;
        float dx = 15 * u;
        float dy = 1 * u;
        float dz = -5 * u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformThirdPerson() {
        float u = Constants.UNIT;
        float dx = 9.5F * u;
        float dy = 3 * u;
        float dz = 2.5F * u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformGround() {
        float u = Constants.UNIT;
        float dx = 6 * u;
        float dy = 8 * u;
        float dz = 13 * u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformGui() {
        float u = Constants.UNIT;
        float dx = 2 * u;
        float dy = 2 * u;
        float dz = 0;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(-90, 1,  0, 0);
        GlStateManager.rotate(-135, 0, 0, 1);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
    }

    private boolean shouldRenderBlade(ItemCameraTransforms.TransformType type, ItemStack stack) {
        return !(type == ItemCameraTransforms.TransformType.GUI || stack == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) && ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getAllTextures() {
        return ImmutableList.of(TEXTURE);
    }
}
