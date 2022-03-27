package com.infinityraider.maneuvergear.handler;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.capability.CapabilityFallBoots;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.infinityraider.maneuvergear.utility.ManeuverGearHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FallDamageHandler {
    private static final FallDamageHandler INSTANCE = new FallDamageHandler();

    private FallDamageHandler() {}

    public static FallDamageHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerFall(LivingHurtEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(!event.getSource().getMsgId().equals(DamageSource.FALL.getMsgId())) {
            return;
        }
        Player player = (Player) event.getEntity();
        // check if player is wearing maneuver gear compatible boots
        if(CapabilityFallBoots.areFallBoots(player.getItemBySlot(EquipmentSlot.FEET))) {
            // check if player is wearing maneuver gear
            ItemStack gear = ManeuverGearHelper.findManeuverGear(player);
            if(gear != null && gear.getItem() == ItemRegistry.itemManeuverGear) {
                event.setAmount((1.0F - ManeuverGear.instance.getConfig().getBootFallDmgReduction()) * event.getAmount());
            }
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWitherDeath(LivingDropsEvent event) {
        if(ItemRegistry.itemRecord == null) {
            return;
        }
        if(!(event.getEntity() instanceof WitherBoss)) {
            return;
        }
        Entity killer = event.getSource().getEntity();
        if(event.isRecentlyHit() && killer != null && killer instanceof Player) {
            Player player = (Player) killer;
            ItemStack left = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack right = player.getItemInHand(InteractionHand.OFF_HAND);
            if(isValidStack(left) && isValidStack(right)) {
                ItemEntity drop = new ItemEntity(
                        event.getEntity().getLevel(), event.getEntity().getX(), event.getEntity().getY()+0.5D, event.getEntity().getZ(),
                        new ItemStack(ItemRegistry.itemRecord));
                event.getDrops().add(drop);
            }
        }
    }

    private boolean isValidStack(ItemStack stack) {
        return stack != null && stack.getItem() == ItemRegistry.itemManeuverGearHandle;
    }
}
