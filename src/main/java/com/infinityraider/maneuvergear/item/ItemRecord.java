package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.SoundRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.SoundEvent;

@MethodsReturnNonnullByDefault
public class ItemRecord extends MusicDiscItem implements IInfinityItem {
    public ItemRecord() {
        super(16, ItemRecord::getSoundEvent, new Properties()
                .maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.RARE));
    }

    @Override
    public String getInternalName() {
        return Names.Items.RECORD;
    }

    @Override
    public boolean isEnabled() {
        return !ManeuverGear.instance.getConfig().disableMusicDisc();
    }

    private static SoundEvent getSoundEvent() {
        return SoundRegistry.getInstance().soundEventRecord;
    }
}
