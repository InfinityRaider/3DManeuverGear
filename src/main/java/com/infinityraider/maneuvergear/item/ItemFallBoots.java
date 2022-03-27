package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFallBoots extends ArmorItem implements IInfinityItem {

    private final String internalName;

    public ItemFallBoots() {
        super(ArmorMaterials.LEATHER, EquipmentSlot.FEET, new Properties().tab(CreativeModeTab.TAB_COMBAT));
        this.internalName = Names.Items.BOOTS;
    }

    @Override
    public String getInternalName() {
        return this.internalName;
    }

    @Override
    public boolean isEnabled() {
        return !ManeuverGear.instance.getConfig().disableFallBoots();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Reference.MOD_ID +":textures/models/armor/fall_boots.png";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TextComponent(""));
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_2"));
    }
}
