package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.infinitylib.proxy.IProxyBase;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy extends IProxyBase {
    /** Registers the key bindings on the client, does nothing on the server */
    void registerKeyBindings();

    /** Initializes the EntityRegistry */
    void initEntities();

    /** Create the relevant physics engine */
    PhysicsEngine createPhysicsEngine(EntityPlayer player);

    /** Spawns the steam particles on the client, does nothing on the server */
    void spawnSteamParticles(EntityPlayer player);

    /** Replace the player model on the client, do nothing on the server */
    void replacePlayerModel();
}
