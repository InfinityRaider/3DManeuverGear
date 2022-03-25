package com.infinityraider.maneuvergear.registry;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.item.*;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class ItemRegistry {
    // Functional items
    public static final Item itemManeuverGear = new ItemManeuverGear();
    public static final Item itemManeuverGearHandle = new ItemManeuverGearHandle();
    public static final Item itemSwordBlade = new ItemSwordBlade();

    // Crafting resources
    public static final Item itemGasCanister = new ItemResource(Names.Items.GAS_CANISTER);
    public static final Item itemBladeHolster = new ItemResource(Names.Items.BLADE_HOLSTER);
    public static final Item itemBladeHolsterAssembly = new ItemResource(Names.Items.BLADE_HOLSTER_ASSEMBLY);
    public static final Item itemBelt = new ItemResource(Names.Items.BELT);
    public static final Item itemGirdle = new ItemResource(Names.Items.GIRDLE);
    public static final Item itemGasNozzle = new ItemResource(Names.Items.GAS_NOZZLE);
    public static final Item itemCableCoil = new ItemResource(Names.Items.CABLE_COIL);
    public static final Item itemGrappleLauncher = new ItemResource(Names.Items.GRAPPLE_LAUNCHER);

    // Gimmicks
    @Nullable
    public static final Item itemFallBoots = ManeuverGear.instance.getConfig().disableFallBoots() ? null : new ItemFallBoots();
    @Nullable
    public static final Item itemRecord = ManeuverGear.instance.getConfig().disableFallBoots() ? null : new ItemRecord();
}
