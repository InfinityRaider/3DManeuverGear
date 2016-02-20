package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.network.MessageBoostUsed;
import com.InfinityRaider.maneuvergear.network.NetworkWrapperManeuverGear;
import com.InfinityRaider.maneuvergear.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {
    private static final KeyInputHandler INSTANCE = new KeyInputHandler();

    private boolean status_left;
    private boolean status_right;

    private int boostCoolDown = 0;

    private KeyInputHandler() {}

    public static KeyInputHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        boolean left = ConfigurationHandler.getInstance().useConfigKeyBinds ? Keyboard.isKeyDown(ConfigurationHandler.getInstance().retractLeftKey) : ClientProxy.retractLeft.isKeyDown();
        boolean right = ConfigurationHandler.getInstance().useConfigKeyBinds ? Keyboard.isKeyDown(ConfigurationHandler.getInstance().retractRightKey) : ClientProxy.retractRight.isKeyDown();
        boolean space = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
        boolean sneak = Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();

        if(left != status_left) {
            toggleRetracting(ManeuverGear.proxy.getClientPlayer(), true, left);
            status_left = left;
        }
        if(right != status_right) {
            toggleRetracting(ManeuverGear.proxy.getClientPlayer(), false, right);
            status_right = right;
        }

        if(space && sneak) {
            applyBoost(ManeuverGear.proxy.getClientPlayer());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            boostCoolDown = Math.max(0, boostCoolDown - 1);
        }
    }

    private void toggleRetracting(EntityPlayer player, boolean left, boolean status) {
        if(DartHandler.instance.isWearingGear(player)) {
           DartHandler.instance.getPhysicsEngine(player).toggleRetracting(left, status);
        }
    }

    private void applyBoost(EntityPlayer player) {
        if(boostCoolDown <= 0 && DartHandler.instance.isWearingGear(player)) {
            NetworkWrapperManeuverGear.wrapper.sendToServer(new MessageBoostUsed());
            DartHandler.instance.getPhysicsEngine(player).applyBoost();
            boostCoolDown = 20;
        }
    }
}
