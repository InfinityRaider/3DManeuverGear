package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.handler.EntityLivingHandler;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IProxy extends IProxyBase {
    /** Registers the key bindings on the client, does nothing on the server */
    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        registerEventHandlers();
        initConfiguration(event);
        initEntities();
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        registerKeyBindings();
    }

    @Override
    default void postInitStart(FMLPostInitializationEvent event) {
        this.replacePlayerModel();
    }

    @Override
    default void onServerStarting(FMLServerStartingEvent event) {
        DartHandler.instance.reset();
    }

    default void registerKeyBindings() {}

    /** Initializes the EntityRegistry */
    default void initEntities() {}

    /** Create the relevant physics engine */
    default PhysicsEngine createPhysicsEngine(EntityPlayer player) {
        return new PhysicsEngineDummy();
    }

    default void spawnSteamParticles(EntityPlayer player) {}

    default void replacePlayerModel() {}

    @Override
    default void registerCapabilities() {}

    @Override
    default void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(DartHandler.instance);
        MinecraftForge.EVENT_BUS.register(EntityLivingHandler.getInstance());
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    @Override
    default void activateRequiredModules() {}

}
