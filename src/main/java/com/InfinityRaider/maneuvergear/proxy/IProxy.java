package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    void registerEventHandlers();

    void registerRenderers();

    void registerKeyBindings();

    void initConfiguration(FMLPreInitializationEvent event);

    /** Returns the instance of the EntityPlayer on the client, null on the server */
    EntityPlayer getClientPlayer();

    /** Returns the client World object on the client, null on the server */
    World getClientWorld();

    /** Returns the World object corresponding to the dimension id */
    World getWorldByDimensionId(int dimension);

    /** Returns the entity in that dimension with that id */
    Entity getEntityById(int dimension, int id);

    /** Returns the entity in that World object with that id */
    Entity getEntityById(World world, int id);

    /** Create the relevant physics engine */
    PhysicsEngine createPhysicsEngine(EntityPlayer player);

    /** Spawns the steam particles on the client, does nothing on the server */
    void spawnSteamParticles(EntityPlayer player);
}
