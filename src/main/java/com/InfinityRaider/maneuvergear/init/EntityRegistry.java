package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.reference.Names;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityRegistry {
    private static EntityRegistry INSTANCE = new EntityRegistry();

    public static EntityRegistry getInstance() {
        return INSTANCE;
    }

    public final EntityRegistryEntry<EntityDart> entityDartEntry;

    private EntityRegistry() {
        entityDartEntry = new EntityRegistryEntry<EntityDart>(EntityDart.class, Names.Objects.DART)
                .setTrackingDistance(EntityDart.CABLE_LENGTH * 2)
                .setRenderFactory(EntityDart.RenderFactory.getInstance());
    }

    @SideOnly(Side.SERVER)
    public void serverInit() {
        for(EntityRegistryEntry entry : EntityRegistryEntry.entries) {
            entry.register();
        }
    }

    @SideOnly(Side.CLIENT)
    public void clientInit() {
        for(EntityRegistryEntry entry : EntityRegistryEntry.entries) {
            entry.register();
            entry.registerRenderer();
        }
    }

    public static class EntityRegistryEntry<T extends Entity> {
        private static final List<EntityRegistryEntry> entries = new ArrayList<EntityRegistryEntry>();

        private Class<? extends T> entityClass;
        private String name;
        private int trackingDistance;
        private int updateFrequency;
        private boolean velocityUpdates;
        private final int id;

        @SideOnly(Side.CLIENT)
        private IRenderFactory<T> renderFactory;

        public EntityRegistryEntry(Class<? extends T> entityClass, String name) {
            this.entityClass = entityClass;
            this.name = name;
            this.trackingDistance = 32;
            this.updateFrequency = 1;
            this.velocityUpdates = true;
            this.id = entries.size();
            entries.add(this);
        }

        EntityRegistryEntry<T> setTrackingDistance(int trackingDistance) {
            this.trackingDistance = trackingDistance;
            return this;
        }

        EntityRegistryEntry<T> setUpdateFrequency(int updateFrequency) {
            this.updateFrequency = updateFrequency;
            return this;
        }

        EntityRegistryEntry<T> setVelocityUpdates(boolean velocityUpdates) {
            this.velocityUpdates = velocityUpdates;
            return this;
        }

        EntityRegistryEntry<T> setRenderFactory(IRenderFactory<T> renderFactory) {
            this.renderFactory = renderFactory;
            return this;
        }

        private void register() {
            net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(entityClass, name, id, ManeuverGear.instance, trackingDistance, updateFrequency, velocityUpdates);
        }

        @SideOnly(Side.CLIENT)
        private void registerRenderer() {
            RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
        }
    }
}
