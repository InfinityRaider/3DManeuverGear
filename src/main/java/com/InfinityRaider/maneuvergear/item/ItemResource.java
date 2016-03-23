package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import com.InfinityRaider.maneuvergear.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class
        ItemResource extends Item implements IItemWithRecipe, IItemWithModel {

    public ItemResource() {
        super();
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if(stack == null || stack.getItem() == null || stack.getItem() != this) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if(stack.getItemDamage() == EnumSubItems.SWORD_BLADE.ordinal()) {
            ItemStack maneuverGear = DartHandler.instance.getManeuverGear(player);
            if(maneuverGear == null || maneuverGear.getItem() == null || !(maneuverGear.getItem() instanceof ItemManeuverGear)) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            stack.stackSize = ((ItemManeuverGear) maneuverGear.getItem()).addBlades(maneuverGear, stack.stackSize, player.isSneaking());
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacker, EntityLivingBase attacked) {
        if(!attacker.worldObj.isRemote
                && attacker instanceof EntityPlayer
                && stack != null
                && stack.getItem() != null
                && stack.getItemDamage() == EnumSubItems.SWORD_BLADE.ordinal())  {
            DamageSource source = DamageSource.causePlayerDamage((EntityPlayer) attacker);
            attacker.attackEntityFrom(source, 2.5F);
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack)+"."+EnumSubItems.getNameForIndex(stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for(int i=0;i<EnumSubItems.values().length;i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null && stack.getItem() != null && stack.getItemDamage() == EnumSubItems.SWORD_BLADE.ordinal()) {
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.swordBladeRight"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.swordBladeLeft"));
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(EnumSubItems.SWORD_BLADE.getStack(), "i", "i", "b",
                'i', "ingotIron",
                'b', new ItemStack(Blocks.iron_bars)));
        list.add(new ShapedOreRecipe(EnumSubItems.GAS_CANISTER.getStack(), " l ", "isi", "ibi",
                'l', new ItemStack(Blocks.lever),
                'i', "ingotIron",
                's', "slimeball",
                'b', new ItemStack(net.minecraft.init.Items.bucket)));
        list.add(new ShapedOreRecipe(EnumSubItems.BLADE_HOLSTER.getStack(), "ibi", "i i", "iii",
                'i', "ingotIron",
                'b', new ItemStack(Blocks.iron_bars)));
        list.add(new ShapedOreRecipe(EnumSubItems.BLADE_HOLSTER_ASSEMBLY.getStack(), " g ", "shs", " s ",
                'g', EnumSubItems.GAS_CANISTER.getStack(),
                's', new ItemStack(net.minecraft.init.Items.string),
                'h', EnumSubItems.BLADE_HOLSTER.getStack()));
        list.add(new ShapedOreRecipe(EnumSubItems.BELT.getStack(), " l ", "l l", "lil",
                'l', new ItemStack(net.minecraft.init.Items.leather),
                'i', "ingotIron"));
        list.add(new ShapedOreRecipe(EnumSubItems.GIRDLE.getStack(), "l l", "sjs", "lsl",
                'l', new ItemStack(net.minecraft.init.Items.leather),
                's', new ItemStack(net.minecraft.init.Items.string),
                'j', new ItemStack(net.minecraft.init.Items.leather_chestplate)));
        list.add(new ShapedOreRecipe(EnumSubItems.GAS_NOZZLE.getStack(), "ibi", "bsb", "ibi",
                'i', "ingotIron",
                'b', new ItemStack(Blocks.iron_bars),
                's', new ItemStack(Blocks.iron_block)));
        list.add(new ShapedOreRecipe(EnumSubItems.CABLE_COIL.getStack(), "sss", "sis", "sss",
                's', new ItemStack(net.minecraft.init.Items.string),
                'i', "ingotIron"));
        list.add(new ShapedOreRecipe(EnumSubItems.GRAPPLE_LAUNCHER.getStack(), "iii", "sda", "iii",
                'i', "ingotIron", 's', new ItemStack(net.minecraft.init.Items.string),
                'd', new ItemStack(Blocks.dispenser),
                'a', new ItemStack(net.minecraft.init.Items.arrow)));
        return list;
    }

    @Override
    public ModelResourceLocation[] getModelDefinitions() {
        ModelResourceLocation[] variants = new ModelResourceLocation[EnumSubItems.values().length];
        for(int i = 0; i < variants.length ; i++) {
            variants[i] = EnumSubItems.values()[i].getModelResourceLocation();
        }
        return variants;
    }

    public enum EnumSubItems {
        SWORD_BLADE("swordBlade"),
        GAS_CANISTER("gasCanister"),
        BLADE_HOLSTER("bladeHolster"),
        BLADE_HOLSTER_ASSEMBLY("bladeHolsterAssembly"),
        BELT("belt"),
        GIRDLE("girdle"),
        GAS_NOZZLE("gasNozzle"),
        CABLE_COIL("cableSpool"),
        GRAPPLE_LAUNCHER("grappleLauncher");

        public final String name;

        EnumSubItems(String name) {
            this.name = name;
        }

        public ItemStack getStack() {
            return new ItemStack(ItemRegistry.getInstance().itemResource, 1, this.ordinal());
        }

        public ModelResourceLocation getModelResourceLocation() {
            return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + name, "inventory");
        }

        public static EnumSubItems getValue(int index) {
            return values()[correctIndex(index)];
        }

        public static String getNameForIndex(int index) {
            return getValue(index).name;
        }

        private static int correctIndex(int index) {
            return index <= 0 ? 0 : index >= values().length ? values().length-1 : index;
        }
    }
}
