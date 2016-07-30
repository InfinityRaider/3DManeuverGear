package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.handler.EntityLivingHandler;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public abstract class CommonProxy implements IProxy {
    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.getInstance().init(event);
    }

    @Override
    public PhysicsEngine createPhysicsEngine(EntityPlayer player) {
        return new PhysicsEngineDummy();
    }

    @Override
    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(DartHandler.instance);
        MinecraftForge.EVENT_BUS.register(EntityLivingHandler.getInstance());
    }

    @Override
    public void registerKeyBindings() {}
}
