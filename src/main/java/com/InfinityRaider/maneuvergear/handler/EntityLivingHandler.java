package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.init.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityLivingHandler {
    private static final EntityLivingHandler INSTANCE = new EntityLivingHandler();

    private EntityLivingHandler() {}

    public static EntityLivingHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerFall(LivingHurtEvent event) {
        if(!(event.entity instanceof EntityPlayer)) {
            return;
        }
        if(!event.source.damageType.equals(DamageSource.fall.getDamageType())) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entity;
        ItemStack boots = player.inventory.armorInventory[0];
        if(boots != null && boots.getItem() != null && boots.getItem() == Items.itemFallBoots) {
            event.ammount = (1.0F-ConfigurationHandler.bootFallDamageReduction)*event.ammount;
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWitherDeath(LivingDropsEvent event) {
        if(Items.itemRecord == null) {
            return;
        }
        if(!(event.entity instanceof EntityWither)) {
            return;
        }
        Entity killer = event.source.getSourceOfDamage();
        if(event.recentlyHit && killer != null && killer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) killer;
            ItemStack stack = player.getCurrentEquippedItem();
            if(stack != null && stack.getItem() != null && stack.getItem() == Items.itemManeuverGearHandle) {
                EntityItem drop = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY+0.5D, event.entity.posZ, new ItemStack(Items.itemRecord));
                event.drops.add(drop);
            }
        }
    }

}
