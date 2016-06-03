package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.render.model.ModelPlayerCustomized;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ArmSwingHandler {
    private static final ArmSwingHandler INSTANCE = new ArmSwingHandler();

    public static ArmSwingHandler getInstance() {
        return INSTANCE;
    }

    private final Map<UUID, Map<EnumHand, SwingProgress>> swingHandlers;

    private ArmSwingHandler() {
        this.swingHandlers = new HashMap<>();
    }

    public void swingArm(EntityPlayer player, EnumHand hand) {
        this.getSwingProgressForPlayerAndHand(player, hand).swingArm();
    }

    public float getSwingProgress(EntityPlayer player, EnumHand hand, float partialTick) {
        return this.getSwingProgressForPlayerAndHand(player, hand).getSwingProgress(partialTick);
    }

    public SwingProgress getSwingProgressForPlayerAndHand(EntityPlayer player, EnumHand hand) {
        if(!swingHandlers.containsKey(player.getUniqueID())) {
            SwingProgress progress = new SwingProgress(player, hand);
            Map<EnumHand, SwingProgress> subMap = new HashMap<>();
            subMap.put(hand, progress);
            swingHandlers.put(player.getUniqueID(), subMap);
            return progress;
        }
        Map<EnumHand, SwingProgress> subMap = swingHandlers.get(player.getUniqueID());
        if(!subMap.containsKey(hand)) {
            SwingProgress progress = new SwingProgress(player, hand);
            subMap.put(hand, progress);
            return progress;
        }
        return subMap.get(hand);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onUpdateTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            for(Map<EnumHand, SwingProgress> subMap : this.swingHandlers.values()) {
                subMap.values().forEach(ArmSwingHandler.SwingProgress::onUpdate);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRenderCall(RenderPlayerEvent.Pre event) {
        ModelPlayer model = event.getRenderer().getMainModel();
        if(model instanceof ModelPlayerCustomized) {
            float left = this.getSwingProgress(event.getEntityPlayer(), EnumHand.OFF_HAND, event.getPartialRenderTick());
            float right = this.getSwingProgress(event.getEntityPlayer(), EnumHand.MAIN_HAND, event.getPartialRenderTick());
            ((ModelPlayerCustomized) model).setSwingProgress(left, right);
        }
    }

    private static class SwingProgress {
        private final EntityPlayer player;
        private final EnumHand hand;

        private float swingProgress;
        private float swingProgressPrev;
        private int swingProgressInt;
        private boolean isSwingInProgress;

        private SwingProgress(EntityPlayer player, EnumHand hand) {
            this.player = player;
            this.hand = hand;
        }

        public EntityPlayer getPlayer() {
            return this.player;
        }

        public EnumHand getHand() {
            return this.hand;
        }

        public float getSwingProgress(float partialTick) {
            float f = this.swingProgress - this.swingProgressPrev;
            if (f < 0.0F) {
                ++f;
            }
            return this.swingProgressPrev + f * partialTick;
        }

        private void onUpdate() {
            this.swingProgressPrev = this.swingProgress;
            this.updateArmSwingProgress();
        }

        private void updateArmSwingProgress() {
            int i = this.getArmSwingAnimationEnd();
            if (this.isSwingInProgress) {
                ++this.swingProgressInt;
                if (this.swingProgressInt >= i) {
                    this.swingProgressInt = 0;
                    this.isSwingInProgress = false;
                }
            } else {
                this.swingProgressInt = 0;
            }
            this.swingProgress = (float) this.swingProgressInt / (float) i;
        }

        public void swingArm() {
            ItemStack stack = this.getPlayer().getHeldItem(getHand());
            if (stack != null && stack.getItem().onEntitySwing(this.getPlayer(), stack)) {
                return;
            }
            if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
                this.swingProgressInt = -1;
            }
            this.isSwingInProgress = true;
        }

        private int getArmSwingAnimationEnd() {
            return this.getPlayer().isPotionActive(MobEffects.HASTE)
                    ? 6 - (1 + this.getPlayer().getActivePotionEffect(MobEffects.HASTE).getAmplifier())
                    : (this.getPlayer().isPotionActive(MobEffects.MINING_FATIGUE)
                    ? 6 + (1 + this.getPlayer().getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6);
        }
    }
}
