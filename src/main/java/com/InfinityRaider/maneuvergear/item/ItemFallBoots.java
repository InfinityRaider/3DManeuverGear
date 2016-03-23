package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class ItemFallBoots extends ItemArmor implements IItemWithRecipe, IItemWithModel {
    public ItemFallBoots() {
        super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET); //(material: cloth, index: cloth, type: boots)
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null && stack.getItem() != null) {
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.boots1"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.boots2"));
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(new ItemStack(this), "lll", "pbp", "www",
                'l', new ItemStack(Items.leather),
                'p', new ItemStack(Blocks.sticky_piston),
                'b', new ItemStack(Items.leather_boots),
                'w', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE)));
        return list;
    }

    @Override
    public ModelResourceLocation[] getModelDefinitions() {
        return new ModelResourceLocation[] {new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":fallBoots", "inventory")};
    }
}
