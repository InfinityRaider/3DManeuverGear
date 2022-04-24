package com.infinityraider.maneuvergear.registry;

import com.infinityraider.infinitylib.entity.EntityTypeBase;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import com.infinityraider.maneuvergear.entity.EntityDart;
import com.infinityraider.maneuvergear.reference.Constants;
import com.infinityraider.maneuvergear.reference.Names;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;

public final class EntityRegistry extends ModContentRegistry {
    private static final EntityRegistry INSTANCE = new EntityRegistry();

    public static EntityRegistry getInstance() {
        return INSTANCE;
    }

    private final RegistryInitializer<EntityTypeBase<EntityDart>> dart;

    private EntityRegistry() {
        this.dart = this.entity(() -> EntityTypeBase.entityTypeBuilder(
                Names.Entities.DART, EntityDart.class, EntityDart.SpawnFactory.getInstance(),
                MobCategory.MISC, EntityDimensions.fixed(Constants.UNIT, Constants.UNIT))
                .setTrackingRange(32)
                .setUpdateInterval(1)
                .setVelocityUpdates(true)
                .setRenderFactory(EntityDart.RenderFactory.getInstance())
                .build());
    }

    public EntityTypeBase<EntityDart> getDartEntityType() {
        return this.dart.get();
    }
}
