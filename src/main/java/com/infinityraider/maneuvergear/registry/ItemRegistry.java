package com.infinityraider.maneuvergear.registry;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.item.*;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class ItemRegistry {
    private static final ItemRegistry INSTANCE = new ItemRegistry();

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    // Functional items
    public final Item itemManeuverGear;
    public final Item itemManeuverGearHandle;
    public final Item itemSwordBlade;

    // Crafting resources
    public final Item itemGasCanister;
    public final Item itemBladeHolster;
    public final Item itemBladeHolsterAssembly;
    public final Item itemBelt;
    public final Item itemGirdle;
    public final Item itemGasNozzle;
    public final Item itemCableCoil;
    public final Item itemGrappleLauncher;

    // Gimmicks
    @Nullable
    public final Item itemFallBoots;
    @Nullable
    public final Item itemRecord;

    private ItemRegistry() {
        this.itemManeuverGear = new ItemManeuverGear();
        this.itemManeuverGearHandle = new ItemManeuverGearHandle();
        this.itemSwordBlade = new ItemSwordBlade();

        this.itemGasCanister = new ItemResource(Names.Items.GAS_CANISTER);
        this.itemBladeHolster = new ItemResource(Names.Items.BLADE_HOLSTER);
        this.itemBladeHolsterAssembly = new ItemResource(Names.Items.BLADE_HOLSTER_ASSEMBLY);
        this.itemBelt = new ItemResource(Names.Items.BELT);
        this.itemGirdle = new ItemResource(Names.Items.GIRDLE);
        this.itemGasNozzle = new ItemResource(Names.Items.GAS_NOZZLE);
        this.itemCableCoil = new ItemResource(Names.Items.CABLE_COIL);
        this.itemGrappleLauncher = new ItemResource(Names.Items.GRAPPLE_LAUNCHER);

        this.itemFallBoots = ManeuverGear.instance.getConfig().disableFallBoots() ? null : new ItemFallBoots();
        this.itemRecord = ManeuverGear.instance.getConfig().disableFallBoots() ? null : new ItemRecord();

    }
}
