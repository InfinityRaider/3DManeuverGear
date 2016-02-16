package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.render.IItemModelRenderer;
import com.InfinityRaider.maneuvergear.render.ItemSpecialRenderer;
import com.InfinityRaider.maneuvergear.render.RenderItemHandle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class ItemManeuverGearHandle extends ItemSword implements IDualWieldedWeapon, IItemWithRecipe, ISpecialRenderedItem {
    public final int MAX_ITEM_DAMAGE;
    public static final ToolMaterial material_SuperHardenedSteel = EnumHelper.addToolMaterial("superHardenedSteel", 3, ConfigurationHandler.durability, 10F, ConfigurationHandler.damage, 0);

    public ItemManeuverGearHandle() {
        super(material_SuperHardenedSteel);
        this.MAX_ITEM_DAMAGE = ConfigurationHandler.durability;
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
    }

    /**
     * Checks if there is a sword blade present on the respective handle
     * @param stack the ItemStack holding this
     * @param left tho check the left or right handle
     * @return if there is a blade present
     */
    public boolean hasSwordBlade(ItemStack stack, boolean left) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getInteger(left ? Names.NBT.LEFT : Names.NBT.RIGHT) > 0;
    }

    public int getBladeDamage(ItemStack stack, boolean left) {
        if(!isValidManeuverGearHandleStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        return tag.getInteger(left ? Names.NBT.LEFT : Names.NBT.RIGHT);
    }

    /**
     * Attempts to damage the sword blade
     * @param stack the ItemStack holding this
     * @param left to damage the left or the right blade
     */
    public void damageSwordBlade(EntityPlayer player, ItemStack stack, boolean left) {
        if(!hasSwordBlade(stack, left)) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        int dmg = tag.getInteger(left ? Names.NBT.LEFT : Names.NBT.RIGHT);
        dmg = dmg -1;
        tag.setInteger(left ? Names.NBT.LEFT : Names.NBT.RIGHT, dmg);
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
        if(hasSwordBlade(stack, left)) {
            return false;
        }
        ItemStack maneuverGearStack = DartHandler.instance.getManeuverGear(player);
        ItemManeuverGear maneuverGear = (ItemManeuverGear) maneuverGearStack.getItem();
        if(maneuverGear.getBladeCount(maneuverGearStack, left) > 0) {
            maneuverGear.removeBlades(maneuverGearStack, 1, left);
            NBTTagCompound tag = stack.getTagCompound();
            tag = tag == null ? new NBTTagCompound() : tag;
            tag.setInteger(left ? Names.NBT.LEFT : Names.NBT.RIGHT, MAX_ITEM_DAMAGE);
            stack.setTagCompound(tag);
            return true;
        }
        return false;
    }

    private void onSwordBladeBroken(EntityPlayer player) {
        if(player != null && !player.worldObj.isRemote) {
            Block.SoundType sound = Block.soundTypeAnvil;
            player.worldObj.playSoundAtEntity(player, sound.getPlaceSound(), (sound.getVolume() + 1.0F) / 4.0F, sound.getFrequency() * 0.8F);
        }
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearHandleStack(ItemStack stack) {
        return stack != null && stack.getItem() != null && stack.getItem() instanceof ItemManeuverGearHandle;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacker, EntityLivingBase attacked) {
        return true;
    }

    @Override
    public void onLeftItemUsed(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl) {
        if (stack.getItem() != this) {
            return;
        }
        if (!DartHandler.instance.isWearingGear(player)) {
            return;
        }
        if (!player.worldObj.isRemote) {
            if (shift) {
                if (DartHandler.instance.hasLeftDart(player)) {
                    DartHandler.instance.retractDart(player, true);
                } else {
                    DartHandler.instance.fireDart(player.worldObj, player, true);
                }
            } else if (ctrl) {
                if (!hasSwordBlade(stack, true)) {
                    applySwordBlade(player, stack, true);
                }
            }
        }
    }

    @Override
    public void onRightItemUsed(ItemStack stack, EntityPlayer player, boolean shift, boolean ctrl) {
        if(stack.getItem() != this) {
            return;
        }
        if(!DartHandler.instance.isWearingGear(player)) {
            return;
        }
        if (!player.worldObj.isRemote) {
            if (shift) {
                if (DartHandler.instance.hasRightDart(player)) {
                    DartHandler.instance.retractDart(player, false);
                } else {
                    DartHandler.instance.fireDart(player.worldObj, player, false);
                }
            } else if (ctrl) {
                if (!hasSwordBlade(stack, false)) {
                    applySwordBlade(player, stack, false);
                }
            }
        }
    }

    @Override
    public boolean onLeftItemAttack(ItemStack stack, EntityPlayer player, Entity e, boolean shift, boolean ctrl) {
        if(ctrl || shift) {
            return true;
        }
        if(!this.hasSwordBlade(stack, true)) {
            return true;
        }
        if(!player.worldObj.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                this.damageSwordBlade(player, stack, true);
            }
        }
        return false;
    }

    @Override
    public boolean onRightItemAttack(ItemStack stack, EntityPlayer player, Entity e, boolean shift, boolean ctrl) {
        if(ctrl || shift) {
            return true;
        }
        if(!this.hasSwordBlade(stack, false)) {
            return true;
        }
        if(!player.worldObj.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                this.damageSwordBlade(player, stack, false);
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float[] getTransformationComponents(EntityPlayer player, ItemStack stack, float partialTick, boolean firstPerson) {
        if(firstPerson) {
            return new float[] {0.0075F*0.4F, 0.02F, -0.03F, -0.02F, 5, 90};
        } else {
            return new float[]{0.075F * 0.4F, 0.5F, 0.4F, -0.1F, 0, 0};
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean useModel(ItemStack stack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemModelRenderer getModel(ItemStack stack) {
        return RenderItemHandle.getInstance();
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack stack) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        if(stack != null && stack.getItem() != null) {
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handle"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.damageLeft") + ": " + this.getBladeDamage(stack, true) + "/" + this.MAX_ITEM_DAMAGE);
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.damageLeft") + ": " + this.getBladeDamage(stack, false) + "/" + this.MAX_ITEM_DAMAGE);
            list.add("");
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleLeftNormal"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleRightNormal"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleLeftSneak"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleRightSneak"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleLeftSprint"));
            list.add(StatCollector.translateToLocal("3DManeuverGear.ToolTip.handleRightSprint"));
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<IRecipe>();
        list.add(new ShapedOreRecipe(ItemRegistry.getInstance().itemManeuverGearHandle, "ww ", "iib", "wwl",
                'w', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
                'i', "ingotIron",
                'b', new ItemStack(Blocks.iron_bars),
                'l', new ItemStack(Blocks.lever)));
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemSpecialRenderer getSpecialRenderer() {
        return RenderItemHandle.getInstance();
    }

    @Override
    public ModelResourceLocation[] getModelDefinitions() {
        return new ModelResourceLocation[] {new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":handle", "inventory")};
    }
}
