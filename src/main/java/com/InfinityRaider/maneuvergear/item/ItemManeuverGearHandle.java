package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.render.item.IItemRenderingHandler;
import com.InfinityRaider.maneuvergear.render.item.RenderItemHandle;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class ItemManeuverGearHandle extends ItemSword implements IDualWieldedWeapon, IItemWithRecipe, ICustomRenderedItem<ItemManeuverGearHandle> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("3dmaneuvergear:models/3DGearHandle");

    public final int MAX_ITEM_DAMAGE;

    @SideOnly(Side.CLIENT)
    private IItemRenderingHandler<ItemManeuverGearHandle> renderer;

    public static final ToolMaterial material_SuperHardenedSteel = EnumHelper.addToolMaterial("superHardenedSteel", 3, ConfigurationHandler.getInstance().durability, 10F, ConfigurationHandler.getInstance().damage, 0);

    public ItemManeuverGearHandle() {
        super(material_SuperHardenedSteel);
        this.MAX_ITEM_DAMAGE = ConfigurationHandler.getInstance().durability;
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setMaxStackSize(1);
    }

    /**
     * Checks if there is a sword blade present on the respective handle
     * @param stack the ItemStack holding this
     * @return if there is a blade present
     */
    public boolean hasSwordBlade(ItemStack stack) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getInteger(Names.NBT.DAMAGE) > 0;
    }

    public int getBladeDamage(ItemStack stack) {
        if(!isValidManeuverGearHandleStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        return tag.getInteger(Names.NBT.DAMAGE);
    }

    /**
     * Attempts to damage the sword blade
     * @param stack the ItemStack holding this
     */
    public void damageSwordBlade(EntityPlayer player, ItemStack stack) {
        if(!hasSwordBlade(stack)) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        int dmg = tag.getInteger(Names.NBT.DAMAGE );
        dmg = dmg -1;
        tag.setInteger(Names.NBT.DAMAGE, dmg);
        if(dmg == 0) {
            onSwordBladeBroken(player);
        }
    }

    /**
     * Tries to apply a sword blade from the holster, this can fail if the player is not wearing a holster, the respective holster is empty,
     * or there is already a blade on the handle
     * @param player the player wielding the gear
     * @param stack the ItemStack holding this
     * @param left to add a blade to the left or the right handle
     * @return if a blade was successfully applied
     */
    public boolean applySwordBlade(EntityPlayer player, ItemStack stack, boolean left) {
        if(!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        if(hasSwordBlade(stack)) {
            return false;
        }
        ItemStack maneuverGearStack = DartHandler.instance.getManeuverGear(player);
        ItemManeuverGear maneuverGear = (ItemManeuverGear) maneuverGearStack.getItem();
        if(maneuverGear.getBladeCount(maneuverGearStack, left) > 0) {
            maneuverGear.removeBlades(maneuverGearStack, 1, left);
            NBTTagCompound tag = stack.getTagCompound();
            tag = tag == null ? new NBTTagCompound() : tag;
            tag.setInteger(Names.NBT.DAMAGE, MAX_ITEM_DAMAGE);
            stack.setTagCompound(tag);
            return true;
        }
        return false;
    }

    private void onSwordBladeBroken(EntityPlayer player) {
        if(player != null && !player.worldObj.isRemote) {
            SoundType type = Blocks.ANVIL.getSoundType();
            player.worldObj.playSound(null, player.posX, player.posY, player.posZ, type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
        }
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearHandleStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGearHandle;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacker, EntityLivingBase attacked) {
        return true;
    }

    @Override
    public void onItemUsed(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl, EnumHand hand) {
        if (stack.getItem() != this) {
            return;
        }
        if (!DartHandler.instance.isWearingGear(player)) {
            return;
        }
        if (!player.worldObj.isRemote) {
            boolean left = hand == EnumHand.OFF_HAND;
            if (shift) {
                if(!DartHandler.instance.isWearingGear(player)) {
                    return;
                }
                if (left ? DartHandler.instance.hasLeftDart(player) : DartHandler.instance.hasRightDart(player)) {
                    DartHandler.instance.retractDart(player, left);
                } else {
                    DartHandler.instance.fireDart(player.worldObj, player, left);
                }
            } else if (ctrl) {
                if (!hasSwordBlade(stack)) {
                    applySwordBlade(player, stack, left);
                }
            }
        }
    }

    @Override
    public boolean onItemAttack(ItemStack stack, EntityPlayer player, Entity e, boolean shift, boolean ctrl, EnumHand hand) {
        if(ctrl || shift) {
            return true;
        }
        if(!this.hasSwordBlade(stack)) {
            return true;
        }
        if(!player.worldObj.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                this.damageSwordBlade(player, stack);
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null) {
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handle"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.damage") + ": " + this.getBladeDamage(stack) + "/" + this.MAX_ITEM_DAMAGE);
            list.add("");
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleLeftNormal"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleRightNormal"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleLeftSneak"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleRightSneak"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleLeftSprint"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.handleRightSprint"));
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(ItemRegistry.getInstance().itemManeuverGearHandle, "ww ", "iib", "wwl",
                'w', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
                'i', "ingotIron",
                'b', new ItemStack(Blocks.IRON_BARS),
                'l', new ItemStack(Blocks.LEVER)));
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemRenderingHandler<ItemManeuverGearHandle> getRenderer() {
        if(this.renderer == null) {
            this.renderer = new RenderItemHandle(this);
        }
        return this.renderer;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getItemModelResourceLocation() {
        return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":handle", "inventory");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getTextures() {
        return ImmutableList.of(TEXTURE);
    }
}
