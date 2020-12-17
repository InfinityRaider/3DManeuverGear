package com.infinityraider.maneuvergear.handler;

import com.infinityraider.maneuvergear.capability.CapabilityFallBoots;
import com.infinityraider.maneuvergear.item.ItemFallBoots;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TooltipHandler {
    private static final TooltipHandler INSTANCE = new TooltipHandler();

    public static TooltipHandler getInstance() {
        return INSTANCE;
    }

    private TooltipHandler() {
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        if (CapabilityFallBoots.areFallBoots(stack) && !(stack.getItem() instanceof ItemFallBoots)) {
            event.getToolTip().add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.fall_boots_imbued"));
        }
    }
}