package com.InfinityRaider.maneuvergear.proxy;

import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    /** Registers the relevant event handlers for the current side */
    void registerEventHandlers();

    /** Registers the renderers on the client, does nothing on the server */
    void registerRenderers();

    /** Registers the key bindings on the client, does nothing on the server */
    void registerKeyBindings();

    /** Initializes and reads the configuration file with the options relevant to the current side*/
    void initConfiguration(FMLPreInitializationEvent event);

    /** Replaces the player model to have left arm animations on the client, does nothing on the server */
    void replacePlayerModel();

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
