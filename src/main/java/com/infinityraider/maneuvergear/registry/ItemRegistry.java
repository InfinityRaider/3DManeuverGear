package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.item.*;
import com.infinityraider.maneuvergear.reference.Names;

import javax.annotation.Nullable;

public final class ItemRegistry extends ModContentRegistry {
    private static final ItemRegistry INSTANCE = new ItemRegistry();

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    // Functional items
    private final RegistryInitializer<ItemManeuverGear> maneuverGear;
    private final RegistryInitializer<ItemManeuverGearHandle> maneuverGearHandle;
    private final RegistryInitializer<ItemSwordBlade> swordBlade;

    // Crafting resources
    private final RegistryInitializer<ItemResource> gasCanister;
    private final RegistryInitializer<ItemResource> bladeHolster;
    private final RegistryInitializer<ItemResource> bladeHolsterAssembly;
    private final RegistryInitializer<ItemResource> belt;
    private final RegistryInitializer<ItemResource> girdle;
    private final RegistryInitializer<ItemResource> gasNozzle;
    private final RegistryInitializer<ItemResource> cableCoil;
    private final RegistryInitializer<ItemResource> grappleLauncher;

    // Gimmicks
    @Nullable
    private final RegistryInitializer<ItemFallBoots> fallBoots;
    @Nullable
    private final RegistryInitializer<ItemRecord> musicDisk;

    private ItemRegistry() {
        this.maneuverGear = this.item(ItemManeuverGear::new);
        this.maneuverGearHandle = this.item(ItemManeuverGearHandle::new);
        this.swordBlade = this.item(ItemSwordBlade::new);

        this.gasCanister = this.item(() -> new ItemResource(Names.Items.GAS_CANISTER));
        this.bladeHolster = this.item(() -> new ItemResource(Names.Items.BLADE_HOLSTER));
        this.bladeHolsterAssembly = this.item(() -> new ItemResource(Names.Items.BLADE_HOLSTER_ASSEMBLY));
        this.belt = this.item(() -> new ItemResource(Names.Items.BELT));
        this.girdle = this.item(() -> new ItemResource(Names.Items.GIRDLE));
        this.gasNozzle = this.item(() -> new ItemResource(Names.Items.GAS_NOZZLE));
        this.cableCoil = this.item(() -> new ItemResource(Names.Items.CABLE_COIL));
        this.grappleLauncher = this.item(() -> new ItemResource(Names.Items.GRAPPLE_LAUNCHER));

        this.fallBoots = ManeuverGear.instance.getConfig().disableFallBoots() ? null : this.item(ItemFallBoots::new);
        this.musicDisk = ManeuverGear.instance.getConfig().disableMusicDisc() ? null : this.item(ItemRecord::new);
    }

    public ItemManeuverGear getManeuverGearItem() {
        return this.maneuverGear.get();
    }

    public ItemManeuverGearHandle getManeuverGearHandle() {
        return this.maneuverGearHandle.get();
    }

    public ItemSwordBlade getSwordBladeItem() {
        return this.swordBlade.get();
    }

    public ItemResource getGasCanisterItem() {
        return this.gasCanister.get();
    }

    public ItemResource getBladeHolsterItem() {
        return this.bladeHolster.get();
    }

    public ItemResource getBladeHolsterAssemblyItem() {
        return this.bladeHolsterAssembly.get();
    }

    public ItemResource getBeltItem() {
        return this.belt.get();
    }

    public ItemResource getGirdleItem() {
        return this.girdle.get();
    }

    public ItemResource getGasNozzleItem() {
        return this.gasNozzle.get();
    }

    public ItemResource getCableCoilItem() {
        return this.cableCoil.get();
    }

    public ItemResource getGrappleLauncherItem() {
        return this.grappleLauncher.get();
    }

    @Nullable
    public ItemFallBoots getFallBootsItem() {
        return this.fallBoots == null ? null : this.fallBoots.get();
    }

    @Nullable
    public ItemRecord getMusicDiskItem() {
        return this.musicDisk == null ? null : this.musicDisk.get();
    }
}
