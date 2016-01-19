package com.InfinityRaider.maneuvergear.item;

import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class ItemRecord extends net.minecraft.item.ItemRecord {
    private final static String name = "GurenNoYumiya";

    public ItemRecord() {
        super(Reference.MOD_ID.toLowerCase()+":"+name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        this.itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation(Reference.MOD_ID.toLowerCase()+":records."+ItemRecord.name);
    }
}
