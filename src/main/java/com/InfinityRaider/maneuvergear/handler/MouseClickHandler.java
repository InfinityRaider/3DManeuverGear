package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.item.IDualWieldedWeapon;
import com.InfinityRaider.maneuvergear.network.MessageAttackDualWielded;
import com.InfinityRaider.maneuvergear.network.MessageMouseButtonPressed;
import com.InfinityRaider.maneuvergear.network.NetworkWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class MouseClickHandler {
    private static final MouseClickHandler INSTANCE = new MouseClickHandler();

    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    private static final int LMB = 0;
    private static final int RMB = 1;

    private MouseClickHandler() {}

    public static MouseClickHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLeftClick(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack stack = player.getHeldItemOffhand();
        if(event.getButton() != LMB) {
            return;
        }
        leftButtonPressed = !leftButtonPressed;
        if(stack == null) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(leftButtonPressed) {
                //boolean shift = Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();
                //boolean ctrl = Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
                boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
                boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, true, shift, ctrl);
                Minecraft.getMinecraft().thePlayer.swingArm(EnumHand.OFF_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, EnumHand.OFF_HAND);
                NetworkWrapper.getInstance().sendToServer(new MessageMouseButtonPressed(true, shift, ctrl));
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRightClick(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack stack = player.getHeldItemMainhand();
        if(event.getButton() != RMB) {
            return;
        }
        rightButtonPressed = !rightButtonPressed;
        if(stack == null) {
            return;
        }
        if(stack.getItem() instanceof IDualWieldedWeapon) {
            if(rightButtonPressed) {
                boolean shift = Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();
                boolean ctrl = Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
                IDualWieldedWeapon weapon = (IDualWieldedWeapon) stack.getItem();
                attackEntity(weapon, player, stack, false, shift, ctrl);
                Minecraft.getMinecraft().thePlayer.swingArm(EnumHand.MAIN_HAND);
                weapon.onItemUsed(stack, player, shift, ctrl, EnumHand.MAIN_HAND);
                NetworkWrapper.getInstance().sendToServer(new MessageMouseButtonPressed(false, shift, ctrl));
            }
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    private void attackEntity(IDualWieldedWeapon weapon, EntityPlayer player, ItemStack stack, boolean left, boolean shift, boolean ctrl) {
        if(Minecraft.getMinecraft().objectMouseOver == null) {
            return;
        }
        Entity entity =  Minecraft.getMinecraft().objectMouseOver.entityHit;
        if(entity != null) {
            if(!weapon.onItemAttack(stack, player, entity, shift, ctrl, left ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND)) {
                NetworkWrapper.getInstance().sendToServer(new MessageAttackDualWielded(entity, left, shift, ctrl));
                Minecraft.getMinecraft().playerController.attackEntity(player, entity);
            }
        }
    }
}
