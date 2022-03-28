package com.infinityraider.maneuvergear.proxy;

import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.maneuvergear.capability.CapabilityFallBoots;
import com.infinityraider.maneuvergear.compat.CuriosCompat;
import com.infinityraider.maneuvergear.config.Config;
import com.infinityraider.maneuvergear.handler.AnvilHandler;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.handler.FallDamageHandler;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.infinitylib.modules.dualwield.ModuleDualWield;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public interface IProxy extends IProxyBase<Config> {

    @Override
    default void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        CuriosCompat.sendInterModMessages();
    }

    @Override
    default void onServerStartingEvent(final ServerStartingEvent event) {
        DartHandler.instance.reset();
    }

    /** Create the relevant physics engine */
    default PhysicsEngine createPhysicsEngine(Player player) {
        return new PhysicsEngineDummy();
    }

    @Override
    default void registerCapabilities() {
        this.registerCapability(CapabilityFallBoots.getInstance());
        this.registerCapability(CuriosCompat.getCurioCapabilityImplementation());
    }

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(DartHandler.instance);
        this.registerEventHandler(FallDamageHandler.getInstance());
        this.registerEventHandler(AnvilHandler.getInstance());
    }

    @Override
    default void activateRequiredModules() {
        ModuleDualWield.getInstance().activate();
        ModuleKeyboard.getInstance().activate();
    }

     default boolean isShiftPressed() {
        return false;
     }

}
