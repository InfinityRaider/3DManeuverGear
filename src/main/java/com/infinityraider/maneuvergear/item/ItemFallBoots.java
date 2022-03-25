package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFallBoots extends ArmorItem implements IInfinityItem {

    private final String internalName;

    public ItemFallBoots() {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Properties().group(ItemGroup.COMBAT));
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
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return Reference.MOD_ID +":textures/models/armor/fall_boots.png";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_2"));
    }
}
