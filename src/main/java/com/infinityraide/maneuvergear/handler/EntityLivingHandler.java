package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
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
        if(!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        if(!event.getSource().damageType.equals(DamageSource.fall.getDamageType())) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        ItemStack boots = player.inventory.armorInventory[0];
        if(boots != null && boots.getItem() == ItemRegistry.getInstance().itemFallBoots) {
            event.setAmount((1.0F-ConfigurationHandler.getInstance().bootFallDamageReduction)*event.getAmount());
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWitherDeath(LivingDropsEvent event) {
        if(ItemRegistry.getInstance().itemRecord == null) {
            return;
        }
        if(!(event.getEntity() instanceof EntityWither)) {
            return;
        }
        Entity killer = event.getSource().getSourceOfDamage();
        if(event.isRecentlyHit() && killer != null && killer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) killer;
            ItemStack left = player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack right = player.getHeldItem(EnumHand.OFF_HAND);
            if(isValidStack(left) && isValidStack(right)) {
                EntityItem drop = new EntityItem(event.getEntity().worldObj, event.getEntity().posX, event.getEntity().posY+0.5D, event.getEntity().posZ,
                        new ItemStack(ItemRegistry.getInstance().itemRecord));
                event.getDrops().add(drop);
            }
        }
    }

    private boolean isValidStack(ItemStack stack) {
        return stack != null && stack.getItem() == ItemRegistry.getInstance().itemManeuverGearHandle;
    }
}
