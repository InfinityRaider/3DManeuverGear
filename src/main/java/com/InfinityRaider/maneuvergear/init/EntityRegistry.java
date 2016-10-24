package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.infinityraider.infinitylib.entity.EntityRegistryEntry;

public class EntityRegistry {
    private static EntityRegistry INSTANCE = new EntityRegistry();

    public static EntityRegistry getInstance() {
        return INSTANCE;
    }

    public final EntityRegistryEntry<EntityDart> entityDartEntry;

    private EntityRegistry() {
        entityDartEntry = new EntityRegistryEntry<>(EntityDart.class, Names.Objects.DART)
                .setTrackingDistance(EntityDart.CABLE_LENGTH * 2)
                .setVelocityUpdates(true)
                .setUpdateFrequency(1)
                .setRenderFactory(EntityDart.RenderFactory.getInstance());
    }
}
