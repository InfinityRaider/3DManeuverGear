package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.entity.EntityTypeBase;
import com.infinityraider.maneuvergear.entity.EntityDart;
import com.infinityraider.maneuvergear.reference.Constants;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;

public class EntityRegistry {
    private static EntityRegistry INSTANCE = new EntityRegistry();

    public static EntityRegistry getInstance() {
        return INSTANCE;
    }

    public final EntityType<EntityDart> entityDartEntry;

    private EntityRegistry() {
        this.entityDartEntry = EntityTypeBase.entityTypeBuilder(Names.Entities.DART, EntityDart.class, EntityDart.SpawnFactory.getInstance(),
                EntityClassification.MISC, EntitySize.fixed(Constants.UNIT, Constants.UNIT))

                .setTrackingRange(32)
                .setUpdateInterval(1)
                .setVelocityUpdates(true)
                .setRenderFactory(EntityDart.RenderFactory.getInstance())
                .build();
    }
}
