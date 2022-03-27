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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public static double CABLE_IMPACT_RETRACTION = 0.1;
    public static double CABLE_IMPACT_RETRACTION_MAX = 2.5;

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
                entry.getValue().getDart(true).kill();
            }
            if(entry.getValue()!=null && entry.getValue().getDart(false)!=null) {
                entry.getValue().getDart(false).kill();
            }
            physicsEnginesClient.remove(entry.getKey());
        }
        for(Map.Entry<UUID, PhysicsEngine> entry: physicsEnginesServer.entrySet()) {
            if(entry.getValue()!=null && entry.getValue().getDart(true)!=null) {
                entry.getValue().getDart(true).kill();
            }
            if(entry.getValue()!=null && entry.getValue().getDart(false)!=null) {
                entry.getValue().getDart(false).kill();
            }
            physicsEnginesServer.remove(entry.getKey());
        }
    }

    public PhysicsEngine getPhysicsEngine(Player player) {
        if (player == null) {
            return DUMMY;
        }
        HashMap<UUID, PhysicsEngine> physicsEngines = player.getLevel().isClientSide() ? physicsEnginesClient : physicsEnginesServer;
        if (!physicsEngines.containsKey(player.getUUID())) {
            return DUMMY;
        }
        return physicsEngines.get(player.getUUID());
    }

    public EntityDart getDart(Player player, boolean left) {
        return getPhysicsEngine(player).getDart(left);
    }

    public EntityDart getLeftDart(Player player) {
        return getDart(player, true);
    }

    public EntityDart getRightDart(Player player) {
        return getDart(player, false);
    }

    public boolean hasDart(Player player, boolean left) {
        return getDart(player, left) != null;
    }

    public boolean hasLeftDart(Player player) {
        return hasDart(player, true);
    }

    public boolean hasRightDart(Player player) {
        return hasDart(player, false);
    }

    /** performs needed operations when the player fires a new dart */
    public void fireDart(Level world, Player player, boolean left) {
        if(world.isClientSide()) {
            return;
        }
        if(isWearingGear(player)) {
            EntityDart dart = new EntityDart(player, left);
            getPhysicsEngine(player).setDart(dart, left);
            world.addFreshEntity(dart);
        }
    }

    public void onDartAnchored(EntityDart dart, Vec3 position, double cableLength, float yaw, float pitch) {
        PhysicsEngine engine = this.getPhysicsEngine(dart.getOwner());
        dart.setXRot(pitch);
        dart.setYRot(yaw);
        dart.setPosRaw(position.x(), position.y(), position.z());
        dart.yRotO = yaw;
        dart.xRotO = pitch;
        dart.setDeltaMovement(0, 0, 0);
        dart.setHooked();
        // Reduce the cable length on impact a bit to make the player jerk towards the impacted dart
        double length = cableLength - Math.min(cableLength*CABLE_IMPACT_RETRACTION, CABLE_IMPACT_RETRACTION_MAX);
        dart.setCableLength(length);
        if(!dart.getLevel().isClientSide()) {
            new MessageDartAnchored(dart, position, length, yaw, pitch).sendTo(dart.getOwner());
        }
        engine.onDartAnchored(dart, position);
    }

    /** performs needed operations when a dart is retracted */
    public void retractDart(Player player, boolean left) {
        if (!player.getLevel().isClientSide()) {
            new MessageDartRetracted(left).sendTo(player);
        }
        PhysicsEngine physicsEngine = getPhysicsEngine(player);
        EntityDart dart = physicsEngine.getDart(left);
        if(dart != null) {
            physicsEngine.setDart(null, left);
            physicsEngine.onDartRetracted(left);
            dart.kill();
        }
    }

    public void retractDarts(Player player) {
        retractDart(player, true);
        retractDart(player, false);
    }

    public boolean isWearingGear(Player player) {
        if(player.getLevel().isClientSide()) {
            return physicsEnginesClient.containsKey(player.getUUID());
        } else {
            return physicsEnginesServer.containsKey(player.getUUID());
        }
    }

    public ItemStack getManeuverGear(Player player) {
        return ManeuverGearHelper.findManeuverGear(player);
    }

    private boolean checkGear(Player player) {
        ItemStack belt = ManeuverGearHelper.findManeuverGear(player);
        return (belt != null) && (belt.getItem() instanceof ItemManeuverGear);
    }

    public void equipGear(Player player) {
        if(!isWearingGear(player)) {
            if(player.getLevel().isClientSide()) {
                physicsEnginesClient.put(player.getUUID(), ManeuverGear.instance.proxy().createPhysicsEngine(player));
            } else {
                physicsEnginesServer.put(player.getUUID(), ManeuverGear.instance.proxy().createPhysicsEngine(player));
                new MessageManeuverGearEquipped(true).sendTo(player);
            }
        }
    }

    public void unEquipGear(Player player) {
        if(isWearingGear(player)) {
            retractDarts(player);
            if(player.getLevel().isClientSide()) {
                physicsEnginesClient.remove(player.getUUID());
            } else {
                physicsEnginesServer.remove(player.getUUID());
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
        Player player = ManeuverGear.instance.getClientPlayer();
        if(player == null) {
            return;
        }
        unEquipGear(player);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            this.unEquipGear(player);
            if (checkGear(player)) {
                equipGear(player);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntity() != null && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(isWearingGear(player)) {
                retractDarts(player);
            }
        }
    }
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        Player player = event.getPlayer();
        if(isWearingGear(player)) {
            // PlayerEntity instance will change, therefore replace the PhysicsEngine instance as well
            unEquipGear(player);
            equipGear(player);
        }
    }
}
