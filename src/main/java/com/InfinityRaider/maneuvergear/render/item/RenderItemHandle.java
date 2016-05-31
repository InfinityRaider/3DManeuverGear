package com.InfinityRaider.maneuvergear.render.item;

import com.InfinityRaider.maneuvergear.item.ItemManeuverGearHandle;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.reference.Constants;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGearHandle;
import com.InfinityRaider.maneuvergear.render.model.ModelTechne;
import com.InfinityRaider.maneuvergear.render.tessellation.ITessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.IdentityHashMap;
import java.util.List;


@SideOnly(Side.CLIENT)
public class RenderItemHandle extends RenderItemBase<ItemManeuverGearHandle> {
    private final ModelTechne<ModelManeuverGearHandle> model;
    private final IdentityHashMap<VertexFormat, List<BakedQuad>> quads;

    public RenderItemHandle(ItemManeuverGearHandle item) {
        super(item);
        this.model = new ModelTechne<>(new ModelManeuverGearHandle());
        this.quads = new IdentityHashMap<>();
    }

    @Override
    public void renderItem(ITessellator tessellator, World world, ItemManeuverGearHandle item, ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, VertexFormat format) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ModelManeuverGearHandle.texture);
        applyCameraTransforms(type);
        if(!quads.containsKey(format)) {
            quads.put(format, model.getBakedQuads(format, 0.025));
        }
        tessellator.addQuads(quads.get(format));
    }

    private void renderBlade(Entity entity, ItemStack stack) {

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ItemStack swordBlade = ItemResource.EnumSubItems.SWORD_BLADE.getStack();
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(swordBlade);

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
        float dx = 5*u;
        float dy = 9*u;
        float dz = 13*u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformFirstPersonRight() {
        float u = Constants.UNIT;
        float dx = 7*u;
        float dy = 9*u;
        float dz = 13*u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformThirdPerson() {
        float u = Constants.UNIT;
        float dx = 6*u;
        float dy = 7*u;
        float dz = 13*u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformGround() {
        float u = Constants.UNIT;
        float dx = 6*u;
        float dy = 8*u;
        float dz = 13*u;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(-90, 1,  0, 0);
    }

    private void transformGui() {
        float u = Constants.UNIT;
        float dx = -5*u;
        float dy = 5*u;
        float dz = 0;
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(-135, 1,  0, 0);
        GlStateManager.rotate(45, 0,  0, 1);
        GlStateManager.scale(2.5F, 2.5F, 2.5F);
    }

    private boolean renderBlade(ItemStack stack) {
        return !(stack == null || !(stack.getItem() instanceof ItemManeuverGearHandle)) && ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack);
    }
}
