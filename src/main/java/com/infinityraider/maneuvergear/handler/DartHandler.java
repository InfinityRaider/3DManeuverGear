package com.infinityraider.maneuvergear.handler;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.entity.EntityDart;
import com.infinityraider.maneuvergear.item.ItemManeuverGear;
import com.infinityraider.maneuvergear.network.MessageDartAnchored;
import com.infinityraider.maneuvergear.network.MessageDartRetracted;
import com.infinityraider.maneuvergear.network.MessageManeuverGearEquipped;
import com.infinityraider.maneuvergear.physics.PhysicsEngine;
import com.infinityraider.maneuvergear.physics.PhysicsEngineDummy;
import com.infinityraider.maneuvergear.utility.ManeuverGearHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Handles all interaction between the player and his two darts */
public class DartHandler {
    public static double CABLE_IMPACT_RETRACTION = 0.9;

    private static final PhysicsEngine DUMMY = new PhysicsEngineDummy();
    public static final DartHandler instance = new DartHandler();

    //I split this up because else on a LAN world it got confused
    private static HashMap<UUID, PhysicsEngine> physicsEnginesClient;
    private static HashMap<UUID, PhysicsEngine> physicsEnginesServer;

    private DartHandler() {
        physicsEnginesClient = new HashMap<>();
        physicsEnginesServer = new HashMap<>();
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

    public PhysicsEngine getPhysicsEngine(PlayerEntity player) {
        if (player == null) {
            return DUMMY;
        }
        HashMap<UUID, PhysicsEngine> physicsEngines = player.getEntityWorld().isRemote ? physicsEnginesClient : physicsEnginesServer;
        if (!physicsEngines.containsKey(player.getUniqueID())) {
            return DUMMY;
        }
        return physicsEngines.get(player.getUniqueID());
    }

    public EntityDart getDart(PlayerEntity player, boolean left) {
        return getPhysicsEngine(player).getDart(left);
    }

    public EntityDart getLeftDart(PlayerEntity player) {
        return getDart(player, true);
    }

    public EntityDart getRightDart(PlayerEntity player) {
        return getDart(player, false);
    }

    public boolean hasDart(PlayerEntity player, boolean left) {
        return getDart(player, left) != null;
    }

    public boolean hasLeftDart(PlayerEntity player) {
        return hasDart(player, true);
    }

    public boolean hasRightDart(PlayerEntity player) {
        return hasDart(player, false);
    }

    /** performs needed operations when the player fires a new dart */
    public void fireDart(World world, PlayerEntity player, boolean left) {
        if(world.isRemote) {
            return;
        }
        if(isWearingGear(player)) {
            EntityDart dart = new EntityDart(player, left);
            getPhysicsEngine(player).setDart(dart, left);
            world.addEntity(dart);
        }
    }

    public void onDartAnchored(EntityDart dart, Vector3d position, double cableLength, float yaw, float pitch) {
        PhysicsEngine engine = this.getPhysicsEngine(dart.getPlayer());
        dart.setPositionAndRotation(position.getX(), position.getY(), position.getZ(), yaw, pitch);
        dart.setRawPosition(position.getX(), position.getY(), position.getZ());
        dart.rotationYaw = yaw;
        dart.rotationPitch = pitch;
        dart.setMotion(0, 0, 0);
        dart.setHooked();
        // Reduce the cable length on impact a bit to make the player jerk towards the impacted dart
        dart.setCableLength(cableLength*CABLE_IMPACT_RETRACTION);
        if(!dart.getEntityWorld().isRemote) {
            new MessageDartAnchored(dart, position, cableLength, yaw, pitch).sendTo(dart.getPlayer());
        }
        engine.onDartAnchored(dart, position);
    }

    /** performs needed operations when a dart is retracted */
    public void retractDart(PlayerEntity player, boolean left) {
        if (!player.getEntityWorld().isRemote) {
            new MessageDartRetracted(left).sendTo(player);
        }
        PhysicsEngine physicsEngine = getPhysicsEngine(player);
        EntityDart dart = physicsEngine.getDart(left);
        if(dart != null) {
            physicsEngine.setDart(null, left);
            physicsEngine.onDartRetracted(left);
            dart.setDead();
        }
    }

    public void retractDarts(PlayerEntity player) {
        retractDart(player, true);
        retractDart(player, false);
    }

    public boolean isWearingGear(PlayerEntity player) {
        if(player.getEntityWorld().isRemote) {
            return physicsEnginesClient.containsKey(player.getUniqueID());
        } else {
            return physicsEnginesServer.containsKey(player.getUniqueID());
        }
    }

    public ItemStack getManeuverGear(PlayerEntity player) {
        return ManeuverGearHelper.findManeuverGear(player);
    }

    private boolean checkGear(PlayerEntity player) {
        ItemStack belt = ManeuverGearHelper.findManeuverGear(player);
        return (belt != null) && (belt.getItem() instanceof ItemManeuverGear);
    }

    public void equipGear(PlayerEntity player) {
        if(!isWearingGear(player)) {
            if(player.getEntityWorld().isRemote) {
                physicsEnginesClient.put(player.getUniqueID(), ManeuverGear.instance.proxy().createPhysicsEngine(player));
            } else {
                physicsEnginesServer.put(player.getUniqueID(), ManeuverGear.instance.proxy().createPhysicsEngine(player));
                new MessageManeuverGearEquipped(true).sendTo(player);
            }
        }
    }

    public void unEquipGear(PlayerEntity player) {
        if(isWearingGear(player)) {
            retractDarts(player);
            if(player.getEntityWorld().isRemote) {
                physicsEnginesClient.remove(player.getUniqueID());
            } else {
                physicsEnginesServer.remove(player.getUniqueID());
                new MessageManeuverGearEquipped(false).sendTo(player);
            }
        }
    }

    /**
     * Event handlers for cases where the player's ManeuverGear could be unequipped or darts should be reset.
     */

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        retractDarts(event.getPlayer());
        if(checkGear(event.getPlayer())) {
            equipGear(event.getPlayer());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        if(checkGear(event.getPlayer())) {
            equipGear(event.getPlayer());
        } else {
            unEquipGear(event.getPlayer());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        retractDarts(event.getPlayer());
        if(checkGear(event.getPlayer())) {
            equipGear(event.getPlayer());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.getPlayer() == null) {
            return;
        }
        unEquipGear(event.getPlayer());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    public void onClientDisconnectFromServer(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        PlayerEntity player = ManeuverGear.instance.getClientPlayer();
        if(player == null) {
            return;
        }
        unEquipGear(player);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            this.unEquipGear(player);
            if (checkGear(player)) {
                equipGear(player);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if(isWearingGear(player)) {
                retractDarts(player);
            }
        }
    }
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity player = event.getPlayer();
        if(isWearingGear(player)) {
            // PlayerEntity instance will change, therefore replace the PhysicsEngine instance as well
            unEquipGear(player);
            equipGear(player);
        }
    }
}
