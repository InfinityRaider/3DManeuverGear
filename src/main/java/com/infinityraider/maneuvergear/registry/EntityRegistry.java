package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.entity.EntityTypeBase;
import com.infinityraider.maneuvergear.entity.EntityDart;
import com.infinityraider.maneuvergear.reference.Constants;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {
    public static final EntityType<EntityDart> entityDartEntry = EntityTypeBase.entityTypeBuilder(
            Names.Entities.DART, EntityDart.class, EntityDart.SpawnFactory.getInstance(),
            MobCategory.MISC, EntityDimensions.fixed(Constants.UNIT, Constants.UNIT))
                .setTrackingRange(32)
                .setUpdateInterval(1)
                .setVelocityUpdates(true)
                .setRenderFactory(EntityDart.RenderFactory.getInstance())
                .build();
}
