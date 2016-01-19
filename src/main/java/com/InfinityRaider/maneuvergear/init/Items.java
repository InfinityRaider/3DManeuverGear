package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.item.*;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.utility.RegisterHelper;
import net.minecraft.item.Item;

public class Items {
    public static Item itemManeuverGear;
    public static Item itemManeuverGearHandle;
    public static Item itemResource;
    public static Item itemFallBoots;
    public static Item itemRecord;

    public static void init() {
        itemManeuverGear = new ItemManeuverGear();
        RegisterHelper.registerItem(itemManeuverGear, Names.Objects.MANEUVER_GEAR);

        itemManeuverGearHandle = new ItemManeuverGearHandle();
        RegisterHelper.registerItem(itemManeuverGearHandle, Names.Objects.MANEUVER_HANDLE);

        itemResource = new ItemResource();
        RegisterHelper.registerItem(itemResource, Names.Objects.RESOURCE);

        if(!ConfigurationHandler.disableFallBoots) {
            itemFallBoots = new ItemFallBoots();
            RegisterHelper.registerItem(itemFallBoots, Names.Objects.BOOTS);
        }

        if(!ConfigurationHandler.disableMusicDisc) {
            itemRecord = new ItemRecord();
            RegisterHelper.registerItem(itemRecord, Names.Objects.RECORD);
        }
    }
}
