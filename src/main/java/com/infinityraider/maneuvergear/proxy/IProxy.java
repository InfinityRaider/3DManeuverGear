package com.infinityraider.maneuvergear.proxy;

import com.infinityraider.maneuvergear.config.Config;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.handler.EntityLivingHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.infinitylib.modules.dualwield.ModuleDualWield;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public interface IProxy extends IProxyBase<Config> {

    @Override
    default void onServerStartingEvent(final FMLServerStartingEvent event) {
        DartHandler.instance.reset();
    }

    /** Create the relevant physics engine */
    default PhysicsEngine createPhysicsEngine(PlayerEntity player) {
        return new PhysicsEngineDummy();
    }

    @Override
    default void registerCapabilities() {}

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(DartHandler.instance);
        this.registerEventHandler(EntityLivingHandler.getInstance());
    }

    @Override
    default void activateRequiredModules() {
        ModuleDualWield.getInstance().activate();
    }

}
