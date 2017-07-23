package com.infinityraider.maneuvergear.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.network.MessageEquipManeuverGear;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.maneuvergear.render.RenderManeuverGear;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import com.infinityraider.infinitylib.utility.TranslationHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemManeuverGear extends ItemBase implements IBauble, IRecipeRegister, IItemWithModel, IRenderBauble {
    public static int MAX_HOLSTERED_BLADES = 4;

    public ItemManeuverGear() {
        super(Names.Objects.MANEUVER_GEAR);
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setMaxStackSize(1);
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote) {
            new MessageEquipManeuverGear(hand).sendToServer();
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * Checks the number of blades left in the holster
     * @param stack the stack containing this
     * @param left check left or right holster
     * @return the amount of blades left in the holster
     */
    public int getBladeCount(ItemStack stack, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        return left ? tag.getInteger(Names.NBT.LEFT) : tag.getInteger(Names.NBT.RIGHT);
    }

    /**
     * Tries to adds blades to the holster
     * @param stack the stack containing this
     * @param amount the amount to add
     * @param left add blades to left or right holster
     * @return the amount of blades left after adding to the holster
     */
    public int addBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return amount;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        int current = getBladeCount(stack, left);
        if(left) {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.setInteger(Names.NBT.LEFT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.setInteger(Names.NBT.LEFT, current + amount);
                return 0;
            }
        } else {
            int maxToAdd = getMaxBlades() - current;
            if(amount >= maxToAdd) {
                tag.setInteger(Names.NBT.RIGHT, getMaxBlades());
                return amount - maxToAdd;
            } else {
                tag.setInteger(Names.NBT.RIGHT, current + amount);
                return 0;
            }
        }
    }

    /**
     * Tries to retrieve blades from the holster
     * @param stack the stack containing this
     * @param amount the amount to retrieve
     * @param left retrieve blades from left or right holster
     * @return the amount of blades retrieved from the holster
     */
    public int removeBlades(ItemStack stack, int amount, boolean left) {
        if(!isValidManeuverGearStack(stack)) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null) {
            return 0;
        }
        int current = getBladeCount(stack, left);
        if(left) {
            if(amount >= current) {
                tag.setInteger(Names.NBT.LEFT, 0);
                return current;
            } else {
                tag.setInteger(Names.NBT.LEFT, current - amount);
                return amount;
            }
        } else {
            if(amount >= current) {
                tag.setInteger(Names.NBT.RIGHT, 0);
                return current;
            } else {
                tag.setInteger(Names.NBT.RIGHT, current - amount);
                return amount;
            }
        }

    }

    /**
     * @return maximum number of holstered blades per holster
     */
    public int getMaxBlades() {
        return MAX_HOLSTERED_BLADES;
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGear;
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.BELT;
    }

    /** This handles the movement of the player */
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        boolean remote = player.worldObj.isRemote;
        if(remote && stack!=null && stack.getItem()==this) {
            boolean b = DartHandler.instance.isWearingGear(player);
            if(DartHandler.instance.isWearingGear(player) && (DartHandler.instance.getLeftDart(player)!=null || DartHandler.instance.getRightDart(player)!=null)) {
                PhysicsEngine engine = DartHandler.instance.getPhysicsEngine(player);
                engine.updateTick();
            }
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.equipGear(player);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
        if(entity==null || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(stack!=null && stack.getItem()==this) {
            DartHandler.instance.unEquipGear(player);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase entityL) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, EntityPlayer entityPlayer, RenderType renderType, float partialTick) {
        RenderManeuverGear.instance.renderBauble(entityPlayer, stack, renderType, partialTick);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null) {
            list.add(TranslationHelper.translateToLocal("3DManeuverGear.ToolTip.belt"));
            list.add(TranslationHelper.translateToLocal("3DManeuverGear.ToolTip.leftBlades")+": "+this.getBladeCount(stack, true)+"/"+MAX_HOLSTERED_BLADES);
            list.add(TranslationHelper.translateToLocal("3DManeuverGear.ToolTip.rightBlades")+": "+this.getBladeCount(stack, false)+"/"+MAX_HOLSTERED_BLADES);
        }
    }

    @Override
    public void registerRecipes() {
        this.getRecipes().forEach(GameRegistry::addRecipe);
    }

    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(this, "cnc", "lgl", "hbh",
                'c', ItemResource.EnumSubItems.CABLE_COIL.getStack(),
                'n', ItemResource.EnumSubItems.GAS_NOZZLE.getStack(),
                'l', ItemResource.EnumSubItems.GRAPPLE_LAUNCHER.getStack(),
                'g', ItemResource.EnumSubItems.GIRDLE.getStack(),
                'h', ItemResource.EnumSubItems.BLADE_HOLSTER_ASSEMBLY.getStack(),
                'b', ItemResource.EnumSubItems.BELT.getStack()));
        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":maneuverGear", "inventory")));
        return list;
    }
}
