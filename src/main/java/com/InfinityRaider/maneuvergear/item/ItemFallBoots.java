package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.item.IItemWithModel;
import com.infinityraider.infinitylib.item.IItemWithRecipe;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemFallBoots extends ItemArmor implements IItemWithRecipe, IItemWithModel {
    public ItemFallBoots() {
        super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET); //(material: cloth, index: cloth, type: boots)
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setMaxStackSize(1);
    }

    @Override
    public String getInternalName() {
        return Names.Objects.BOOTS;
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if(stack != null) {
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.boots1"));
            list.add(I18n.translateToLocal("3DManeuverGear.ToolTip.boots2"));
        }
    }

    @Override
    public List<IRecipe> getRecipes() {
        List<IRecipe> list = new ArrayList<>();
        list.add(new ShapedOreRecipe(new ItemStack(this), "lll", "pbp", "www",
                'l', new ItemStack(Items.LEATHER),
                'p', new ItemStack(Blocks.STICKY_PISTON),
                'b', new ItemStack(Items.LEATHER_BOOTS),
                'w', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE)));
        return list;
    }

    @Override
    public List<Tuple<Integer, ModelResourceLocation>> getModelDefinitions() {
        List<Tuple<Integer, ModelResourceLocation>> list = new ArrayList<>();
        list.add(new Tuple<>(0, new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":fallBoots", "inventory")));
        return list;
    }
}
