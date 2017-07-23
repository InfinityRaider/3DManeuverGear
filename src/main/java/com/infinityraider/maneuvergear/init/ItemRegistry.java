package com.infinityraider.maneuvergear.init;

import com.infinityraider.maneuvergear.handler.ConfigurationHandler;
import com.infinityraider.maneuvergear.item.*;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    private static final ItemRegistry INSTANCE = new ItemRegistry();

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    public Item itemManeuverGear;
    public Item itemManeuverGearHandle;
    public Item itemResource;
    public Item itemFallBoots;
    public Item itemRecord;

    private List<Item> items;

    private ItemRegistry() {
        this.items = new ArrayList<>();
        this.init();
    }

    public List<Item> getItems() {
        return items;
    }

    private void init() {
        itemManeuverGear = new ItemManeuverGear();
        items.add(itemManeuverGear);

        itemManeuverGearHandle = new ItemManeuverGearHandle();
        items.add(itemManeuverGearHandle);

        itemResource = new ItemResource();
        items.add(itemResource);

        if(!ConfigurationHandler.getInstance().disableFallBoots) {
            itemFallBoots = new ItemFallBoots();
            items.add(itemFallBoots);
        }

        if(!ConfigurationHandler.getInstance().disableMusicDisc) {
            itemRecord = new ItemRecord("guren_no_yumiya");
            items.add(itemRecord);
        }
    }
}
