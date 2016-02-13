package com.InfinityRaider.maneuvergear.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Class used to swing a player's left hand
 */
public class SwingLeftHandHandler {
    private static final SwingLeftHandHandler INSTANCE = new SwingLeftHandHandler();

    private HashMap<UUID, SwingProgress> swingProgresses;

    private SwingLeftHandHandler() {
        this.swingProgresses = new HashMap<UUID, SwingProgress>();
    }

    public static SwingLeftHandHandler getInstance() {
        return INSTANCE;
    }

    public void onLeftWeaponSwing(EntityPlayer player) {
        if (!isSwingInProgress(player) || getSwingProgressInt(player) >= getArmSwingAnimationEnd(player) / 2 || getSwingProgressInt(player) < 0) {
            setSwingProgressInt(player, -1);
            setSwingInProgress(player, true);
        }
    }

    public void updateArmSwingProgress(EntityPlayer player) {
        int i = getArmSwingAnimationEnd(player);
        if (isSwingInProgress(player)) {
            setSwingProgressInt(player, getSwingProgressInt(player)+1);
            if (getSwingProgressInt(player) >= i) {
                setSwingProgressInt(player, 0);
                setSwingInProgress(player, false);
            }
        } else {
            setSwingProgressInt(player, 0);
        }
        setSwingProgress(player, (float) getSwingProgressInt(player) / (float) i);
    }

    public boolean isSwingInProgress(EntityPlayer player) {
        return player != null && getSwingProgressObject(player).isSwingInProgress();
    }

    public void setSwingInProgress(EntityPlayer player, boolean status) {
        getSwingProgressObject(player).setSwingInProgress(status);
    }

    public int getSwingProgressInt(EntityPlayer player) {
        return getSwingProgressObject(player).getSwingProgressInt();
    }

    public void setSwingProgressInt(EntityPlayer player, int progress) {
        getSwingProgressObject(player).setSwingProgressInt(progress);
    }

    public float getSwingProgress(EntityPlayer player, float partialTick) {
        return getSwingProgressObject(player).getSwingProgress(partialTick);
    }

    public void setSwingProgress(EntityPlayer player, float progress) {
        getSwingProgressObject(player).setSwingProgress(progress);
    }

    public int getArmSwingAnimationEnd(EntityPlayer player) {
        return getSwingProgressObject(player).getArmSwingAnimationEnd(player);
    }

    public SwingProgress getSwingProgressObject(EntityPlayer player) {
        if(!swingProgresses.containsKey(player.getUniqueID())) {
            swingProgresses.put(player.getUniqueID(), new SwingProgress());
        }
        return swingProgresses.get(player.getUniqueID());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLivingUpdateEvent(TickEvent.PlayerTickEvent event) {
        updateArmSwingProgress(event.player);
    }

    public static class SwingProgress {
        private static final int ANIMATION_MULTIPLIER = 4;

        private boolean swingInProgress;
        private int swingProgressInt;
        private float swingProgress;
        private float prevSwingProgress;

        public SwingProgress() {
        }

        public boolean isSwingInProgress() {
            return swingInProgress;
        }

        public void setSwingInProgress(boolean status) {
            this.swingInProgress = status;
        }

        public int getSwingProgressInt() {
            return swingProgressInt;
        }

        public void setSwingProgressInt(int progress) {
            this.swingProgressInt = progress;
        }

        public float getSwingProgress(float partialTicks) {
            float f1 = this.swingProgress - this.prevSwingProgress;
            if (f1 < 0.0F) {
                ++f1;
            }
            return this.prevSwingProgress + f1 * partialTicks;
        }

        public void setSwingProgress(float progress) {
            this.prevSwingProgress = this.swingProgress;
            this.swingProgress = progress;
        }

        public int getArmSwingAnimationEnd(EntityPlayer player) {
            return ANIMATION_MULTIPLIER *
                    (player.isPotionActive(Potion.digSpeed)
                    ?  6 - (1 + player.getActivePotionEffect(Potion.digSpeed).getAmplifier())
                    : (player.isPotionActive(Potion.digSlowdown) ? 6 + (1 + player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6));
        }

    }
}
