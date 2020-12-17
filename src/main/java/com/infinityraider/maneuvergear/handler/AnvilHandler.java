package com.infinityraider.maneuvergear.handler;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.capability.CapabilityFallBoots;
import com.infinityraider.maneuvergear.item.ItemFallBoots;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnvilHandler {

    private static final AnvilHandler INSTANCE = new AnvilHandler();

    public static final AnvilHandler getInstance() {
        return INSTANCE;
    }

    private AnvilHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onAnvilUse(AnvilUpdateEvent event) {
        // Fetch input stacks
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        // Verify imbuing conditions
        if(left == null || right == null || left.isEmpty() || right.isEmpty()) {
            // Do nothing if either slot is empty
            return;
        }
        if(!(left.getItem() instanceof ArmorItem)) {
            // Do nothing if the left slot does not contain an armor piece
            return;
        }
        if(!(right.getItem() instanceof ArmorItem)) {
            // Do nothing if the right slot does not contain an armor piece
            return;
        }
        if((left.getItem() instanceof ItemFallBoots) && (right.getItem() instanceof ItemFallBoots)) {
            // Do nothing if both slots contain fall boots
            return;
        }
        if(!(left.getItem() instanceof ItemFallBoots) && !(right.getItem() instanceof ItemFallBoots)) {
            // Do nothing if neither slot contains fall boots
            return;
        }
        if(((ArmorItem) left.getItem()).getEquipmentSlot() != ((ArmorItem) right.getItem()).getEquipmentSlot()) {
            // Do nothing if the slots contain armor pieces for different body parts
            return;
        }
        // Fetch the input ItemStack
        ItemStack input = (left.getItem() instanceof ItemFallBoots) ? right : left;
        // Check if the input stack is imbued already
        if(CapabilityFallBoots.areFallBoots(input)) {
            // Do nothing if the input stack is already imbued
            return;
        }
        // Copy the input ItemStack
        ItemStack output = input.copy();
        // Activate the ninja armor status on the capability
        output.getCapability(CapabilityFallBoots.CAPABILITY).ifPresent(cap -> cap.setFallBoots(true));
        // Set the name
        String inputName = event.getName();
        if(inputName == null || inputName.isEmpty()) {
            output.clearCustomName();
        } else {
            output.setDisplayName(new StringTextComponent(inputName));
        }
        // Set the output
        event.setOutput(output);
        // Set the cost
        event.setCost(ManeuverGear.instance.getConfig().getImbueCost());
    }
}
