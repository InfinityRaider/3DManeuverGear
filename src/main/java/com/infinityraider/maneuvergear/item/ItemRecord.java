package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.registry.SoundRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

@MethodsReturnNonnullByDefault
public class ItemRecord extends RecordItem implements IInfinityItem {
    public ItemRecord() {
        super(16, ItemRecord::getSoundEvent, new Properties()
                .stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE));
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
        return SoundRegistry.soundEventRecord;
    }
}
