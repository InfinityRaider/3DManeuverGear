package com.infinityraider.maneuvergear.handler;

import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.config.Config;
import com.infinityraider.maneuvergear.network.MessageBoostUsed;
import com.infinityraider.maneuvergear.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyInputHandler {
    private static final KeyInputHandler INSTANCE = new KeyInputHandler();

    private final Config config;
    private final ModuleKeyboard keyboard;

    private boolean status_left;
    private boolean status_right;

    private int boostCoolDown = 0;

    private KeyInputHandler() {
        this.config = ManeuverGear.instance.getConfig();
        this.keyboard = ModuleKeyboard.getInstance();
    }

    public static KeyInputHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(Minecraft.getInstance().screen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        boolean space = Minecraft.getInstance().options.keyJump.isDown();
        boolean sneak = Minecraft.getInstance().options.keyShift.isDown();
        if(space && sneak) {
            applyBoost(ManeuverGear.instance.getClientPlayer());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(Minecraft.getInstance().screen != null) {
            // We do not want to do anything while a GUI is open
            return;
        }
        // check keybinds
        boolean left = this.config.useConfigKeyBinds() ? keyboard.isKeyPressed(config.retractLeftKey()) : ClientProxy.KEY_RETRACT_LEFT.isDown();
        boolean right = this.config.useConfigKeyBinds() ? keyboard.isKeyPressed(config.retractRightKey()) : ClientProxy.KEY_RETRACT_RIGHT.isDown();
        // update retracting status
        if(left != status_left) {
            toggleRetracting(ManeuverGear.instance.getClientPlayer(), true, left);
            status_left = left;
        }
        if(right != status_right) {
            toggleRetracting(ManeuverGear.instance.getClientPlayer(), false, right);
            status_right = right;
        }
        // decrement boost cooldown
        if(event.phase == TickEvent.Phase.END) {
            boostCoolDown = Math.max(0, boostCoolDown - 1);
        }
    }

    private void toggleRetracting(Player player, boolean left, boolean status) {
        if(DartHandler.instance.isWearingGear(player)) {
           DartHandler.instance.getPhysicsEngine(player).toggleRetracting(left, status);
        }
    }

    private void applyBoost(Player player) {
        if(boostCoolDown <= 0 && DartHandler.instance.isWearingGear(player)) {
            new MessageBoostUsed().sendToServer();
            DartHandler.instance.getPhysicsEngine(player).applyBoost();
            boostCoolDown = 20;
        }
    }
}
