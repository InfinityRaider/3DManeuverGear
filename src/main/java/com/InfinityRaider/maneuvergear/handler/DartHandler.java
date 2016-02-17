package com.InfinityRaider.maneuvergear.handler;

import baubles.api.BaublesApi;
import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.item.ItemManeuverGear;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngine;
import com.InfinityRaider.maneuvergear.physics.PhysicsEngineDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Handles all interaction between the player and his two darts */
public class DartHandler {
    public static final int BELT_SLOT = 3;

    private static final PhysicsEngine DUMMY = new PhysicsEngineDummy();
    public static final DartHandler instance = new DartHandler();

    //I split this up because else on a LAN world it got confused
    private static HashMap<UUID, PhysicsEngine> physicsEnginesClient;
    private static HashMap<UUID, PhysicsEngine> physicsEnginesServer;

    private DartHandler() {
        physicsEnginesClient = new HashMap<UUID, PhysicsEngine>();
        physicsEnginesServer = new HashMap<UUID, PhysicsEngine>();
    }

    public void reset() {
        for(Map.Entry<UUID, PhysicsEngine> entry: physicsEnginesClient.entrySet()) {
            if(entry.getValue()!=null && entry.getValue().getDart(true)!=null) {
                entry.getValue().getDart(true).setDead();
            }
            if(entry.getValue()!=null && entry.getValue().getDart(false)!=null) {
                entry.getValue().getDart(false).setDead();
            }
            physicsEnginesClient.remove(entry.getKey());
        }
        for(Map.Entry<UUID, PhysicsEngine> entry: physicsEnginesServer.entrySet()) {
            if(entry.getValue()!=null && entry.getValue().getDart(true)!=null) {
                entry.getValue().getDart(true).setDead();
            }
            if(entry.getValue()!=null && entry.getValue().getDart(false)!=null) {
                entry.getValue().getDart(false).setDead();
            }
            physicsEnginesServer.remove(entry.getKey());
        }
    }

    public PhysicsEngine getPhysicsEngine(EntityPlayer player) {
        if (player == null) {
            return DUMMY;
        }
        HashMap<UUID, PhysicsEngine> physicsEngines = player.worldObj.isRemote ? physicsEnginesClient : physicsEnginesServer;
        if (!physicsEngines.containsKey(player.getUniqueID())) {
            return DUMMY;
        }
        return physicsEngines.get(player.getUniqueID());
    }

    public EntityDart getDart(EntityPlayer player, boolean left) {
        return getPhysicsEngine(player).getDart(left);
    }

    public EntityDart getLeftDart(EntityPlayer player) {
        return getDart(player, true);
    }

    public EntityDart getRightDart(EntityPlayer player) {
        return getDart(player, false);
    }

    public boolean hasDart(EntityPlayer player, boolean left) {
        return getDart(player, left) != null;
    }

    public boolean hasLeftDart(EntityPlayer player) {
        return hasDart(player, true);
    }

    public boolean hasRightDart(EntityPlayer player) {
        return hasDart(player, false);
    }

    /** performs needed operations when the player fires a new dart */
    public void fireDart(World world, EntityPlayer player, boolean left) {
        if(world.isRemote) {
            return;
        }
        if(isWearingGear(player)) {
            EntityDart dart = new EntityDart(player, left);
            getPhysicsEngine(player).setDart(dart, left);
            world.spawnEntityInWorld(dart);
        }
    }

    public void onDartAnchored(EntityDart dart) {
        PhysicsEngine engine = this.getPhysicsEngine(dart.getPlayer());
        engine.onDartAnchored(dart);
    }

    /** performs needed operations when a dart is retracted */
    public void retractDart(EntityPlayer player, boolean left) {
        if (player.worldObj.isRemote) {
            return;
        }
        PhysicsEngine physicsEngine = getPhysicsEngine(player);
        EntityDart dart = physicsEngine.getDart(left);
        if(dart != null) {
            physicsEngine.setDart(null, left);
            physicsEngine.onDartRetracted(left);
            dart.setDead();
        }
    }

    public void retractDarts(EntityPlayer player) {
        retractDart(player, true);
        retractDart(player, false);
    }

    public boolean isWearingGear(EntityPlayer player) {
        if(player.worldObj.isRemote) {
            return physicsEnginesClient.containsKey(player.getUniqueID());
        } else {
            return physicsEnginesServer.containsKey(player.getUniqueID());
        }
    }

    public ItemStack getManeuverGear(EntityPlayer player) {
        return BaublesApi.getBaubles(player).getStackInSlot(BELT_SLOT);
    }

    private boolean checkGear(EntityPlayer player) {
        IInventory baubles = BaublesApi.getBaubles(player);
        ItemStack belt = baubles.getStackInSlot(BELT_SLOT);
        return (belt!=null) && (belt.getItem()!=null) && (belt.getItem() instanceof ItemManeuverGear);
    }

    public void equipGear(EntityPlayer player) {
        if(!isWearingGear(player)) {
            if(player.worldObj.isRemote) {
                physicsEnginesClient.put(player.getUniqueID(), ManeuverGear.proxy.createPhysicsEngine(player));
            } else {
                physicsEnginesServer.put(player.getUniqueID(), ManeuverGear.proxy.createPhysicsEngine(player));
            }
        }
    }

    public void unEquipGear(EntityPlayer player) {
        if(isWearingGear(player)) {
            retractDarts(player);
            if(player.worldObj.isRemote) {
                physicsEnginesClient.remove(player.getUniqueID());
            } else {
                physicsEnginesServer.remove(player.getUniqueID());
            }
        }
    }

    /**
     * Event handlers for cases where the player's ManeuverGear could be unequipped or darts should be reset.
     */

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) {
            return;
        }
        retractDarts(event.player);
        if(checkGear(event.player)) {
            equipGear(event.player);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player == null) {
            return;
        }
        if(checkGear(event.player)) {
            equipGear(event.player);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player == null) {
            return;
        }
        retractDarts(event.player);
        if(checkGear(event.player)) {
            equipGear(event.player);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.player == null) {
            return;
        }
        unEquipGear(event.player);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        EntityPlayer player = ManeuverGear.proxy.getClientPlayer();
        if(player == null) {
            return;
        }
        unEquipGear(player);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.entity != null && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (checkGear(player)) {
                equipGear(player);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDeath(LivingDeathEvent event) {
        if(event.entity != null && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if(isWearingGear(player)) {
                retractDarts(player);
            }
        }
    }
}
