package com.infinityraider.maneuvergear.proxy;

import com.infinityraider.maneuvergear.handler.ConfigurationHandler;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.handler.EntityLivingHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.infinitylib.modules.dualwield.ModuleDualWield;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IProxy extends IProxyBase {
    /** Registers the key bindings on the client, does nothing on the server */
    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        initConfiguration(event);
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        registerKeyBindings();
    }

    @Override
    default void onServerStarting(FMLServerStartingEvent event) {
        DartHandler.instance.reset();
    }

    default void registerKeyBindings() {}

    /** Create the relevant physics engine */
    default PhysicsEngine createPhysicsEngine(EntityPlayer player) {
        return new PhysicsEngineDummy();
    }

    default void spawnSteamParticles(EntityPlayer player) {}

    @Override
    default void registerCapabilities() {}

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(DartHandler.instance);
        this.registerEventHandler(EntityLivingHandler.getInstance());
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    @Override
    default void activateRequiredModules() {
        ModuleDualWield.getInstance().activate();
    }

}
