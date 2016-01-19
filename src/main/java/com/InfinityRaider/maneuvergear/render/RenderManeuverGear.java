package com.InfinityRaider.maneuvergear.render;

import com.InfinityRaider.maneuvergear.init.Items;
import com.InfinityRaider.maneuvergear.item.ItemManeuverGear;
import com.InfinityRaider.maneuvergear.item.ItemResource;
import com.InfinityRaider.maneuvergear.render.model.ModelManeuverGear;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderManeuverGear implements IBaubleRenderer, IItemRenderer {
    public static final RenderManeuverGear instance = new RenderManeuverGear();

    @SideOnly(Side.CLIENT)
    private ModelManeuverGear model;

    private RenderManeuverGear() {
        this.model = new ModelManeuverGear();
    }

    @Override
    public void renderBauble(EntityLivingBase entity, ItemStack stack, float partialRenderTick) {
        renderModel(entity, stack);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(type == ItemRenderType.ENTITY) {
            renderEntity(item, (Entity) data[1]);
            return;
        }
        if(type == ItemRenderType.EQUIPPED) {
            renderEquipped(item, (Entity) data[1]);
            return;
        }
        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            renderEquippedFirstPerson(item, (Entity) data[1]);
            return;
        }
        if(type == ItemRenderType.INVENTORY) {
            renderInventory(item);
        }
    }

    /** Render item as entity in the
     * @param stack: the itemstack
     * @param entity: the entity in the world
     */
    protected void renderEntity(ItemStack stack, Entity entity) {
        GL11.glTranslatef(0, 2, 0);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        renderModel(entity, stack);
        GL11.glScalef(1F / 1.5F, 1F / 1.5F, 1F / 1.5F);
        GL11.glRotatef(-180, 1, 0, 0);
        GL11.glTranslatef(0, -2, 0);
    }

    /** Render item held by an entity
     * @param stack: the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquipped(ItemStack stack, Entity entity) {
        GL11.glTranslatef(0, 2, 0);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(135, 0, 1, 0);
        GL11.glTranslatef(0, 0.5F, 1);
        GL11.glScalef(2F, 2F, 2F);
        renderModel(entity, stack);
        GL11.glScalef(1F / 2F, 1F / 2F, 1F / 2F);
        GL11.glTranslatef(0, -0.5F, -1);
        GL11.glRotatef(-135, 0, 1, 0);
        GL11.glRotatef(-180, 1, 0, 0);
        GL11.glTranslatef(0, -2, 0);
    }

    /** Render item held by an entity
     * @param stack: the itemstack
     * @param entity: the entity holding the stack
     */
    protected void renderEquippedFirstPerson(ItemStack stack, Entity entity) {
        if(entity==null) {
            return;
        }
        GL11.glTranslatef(0, 2, 0);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(-135, 0, 1, 0);
        GL11.glTranslatef(-0.5F, 0, 0);
        GL11.glScalef(2F, 2F, 2F);
        renderModel(entity, stack);
        GL11.glScalef(1F / 2F, 1F / 2F, 1F / 2F);
        GL11.glTranslatef(0.5F, 0, 0);
        GL11.glRotatef(135, 0, 1, 0);
        GL11.glRotatef(-180, 1, 0, 0);
        GL11.glTranslatef(0, -2, 0);
    }

    /** Render item held by an entity
     * @param stack: the itemstack
     */
    protected void renderInventory(ItemStack stack) {
        GL11.glTranslatef(0, 1, 0);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glRotatef(-45, 0, 1, 0);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        renderModel(null, stack);
        GL11.glScalef(1F / 1.5F, 1F / 1.5F, 1F / 1.5F);
        GL11.glRotatef(45, 0, 1, 0);
        GL11.glRotatef(-180, 1, 0, 0);
        GL11.glTranslatef(0, -1, 0);
    }

    private void renderModel(Entity entity, ItemStack stack) {
        boolean sneak = entity != null && entity.isSneaking();
        if(sneak) {
            GL11.glTranslatef(0, 0, 0.25F);
        }
        model.render(entity, 0, 0, 0, 0, 0, 1);
        if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemManeuverGear) {
            ItemManeuverGear maneuverGear = (ItemManeuverGear) stack.getItem();

            float f = 0.75F;
            float dx = 0.4F;
            float dy = 0.6F;
            float dz = 0.2F;
            float a_x = 15F;
            float a_y = 90F;

            float delta = -0.9375F;

            GL11.glPushMatrix();

            GL11.glScalef(f, f, f);
            GL11.glTranslatef(dx, dy, dz);
            GL11.glRotatef(a_x, 1, 0, 0);
            GL11.glRotatef(a_y, 0, 1, 0);

            renderBlades(maneuverGear, stack, true);

            GL11.glTranslatef(0, 0, delta);

            renderBlades(maneuverGear, stack, false);

            GL11.glTranslatef(0, 0, -delta);

            GL11.glRotatef(-a_y, 0, 1, 0);
            GL11.glRotatef(-a_x, 1, 0, 0);
            GL11.glTranslatef(-dx, -dy, -dz);
            GL11.glScalef(1.0F / f, 1.0F / f, 1.0F / f);

            GL11.glPopMatrix();
        }
        if(sneak) {
            GL11.glTranslatef(0, 0, -0.25F);
        }
    }

    private void renderBlades(ItemManeuverGear maneuverGear, ItemStack stack, boolean left) {
        int amount = maneuverGear.getBladeCount(stack, left);
        if(amount > 0) {
            Tessellator tessellator = Tessellator.instance;
            IIcon icon = Items.itemResource.getIconFromDamage(ItemResource.EnumSubItems.SWORD_BLADE.ordinal());
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
            float delta = 0.055F;

            GL11.glPushMatrix();

            for(int i = 0; i < amount; i++) {
                ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.4F / ((float) 16));
                GL11.glTranslatef(0, 0, delta);
            }
            GL11.glTranslatef(0, 0, -amount * delta);

            GL11.glPopMatrix();
        }
    }
}
