package com.InfinityRaider.maneuvergear.render;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.InfinityRaider.maneuvergear.item.IBaubleRendered;
import com.InfinityRaider.maneuvergear.network.MessageRequestBaubles;
import com.InfinityRaider.maneuvergear.network.NetworkWrapperManeuverGear;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class RenderBauble {
    private static final RenderBauble INSTANCE = new RenderBauble();

    private HashMap<EntityPlayer, HashMap<BaubleType, ItemStack>> baublesToRender;

    private RenderBauble() {
        baublesToRender = new HashMap<EntityPlayer, HashMap<BaubleType, ItemStack>>();
    }

    public static RenderBauble getInstance() {
        return INSTANCE;
    }

    public void syncBaubles(EntityPlayer player, ItemStack[] baubles) {
       HashMap<BaubleType, ItemStack> baubleMap = new HashMap<BaubleType, ItemStack>();
        for(ItemStack stack:baubles) {
            if((stack!=null) && (stack.getItem()!=null) && (stack.getItem() instanceof IBaubleRendered)) {
                baubleMap.put(((IBaubleRendered) stack.getItem()).getBaubleType(stack), stack);
            }
        }
        baublesToRender.put(player, baubleMap);
    }

    public void equipBauble(EntityPlayer player, ItemStack bauble) {
        if(bauble==null || bauble.getItem()==null || !(bauble.getItem() instanceof IBaubleRendered)) {
            return;
        }
        if(!baublesToRender.containsKey(player)) {
            baublesToRender.put(player, new HashMap<BaubleType, ItemStack>());
        }
        HashMap<BaubleType, ItemStack> baubles = baublesToRender.get(player);
        baubles.put(((IBaubleRendered) bauble.getItem()).getBaubleType(bauble), bauble);
    }

    public void unequipBauble(EntityPlayer player, ItemStack bauble) {
        if(bauble==null || bauble.getItem()==null || !(bauble.getItem() instanceof IBaubleRendered)) {
            return;
        }
        if(!baublesToRender.containsKey(player)) {
            baublesToRender.put(player, new HashMap<BaubleType, ItemStack>());
            return;
        }
        baublesToRender.get(player).remove(((IBaubleRendered) bauble.getItem()).getBaubleType(bauble));
    }

    /**
     * Baubles aren't synced to the client apparently, so before rendering the player,
     * send a packet to the server to request all the equipped baubles which have to be rendered on this player
     *
     * I realize that syncing the baubles every tick is a brute force method,
     * but I do not know of a way to keep baubles synced when NBT is edited.
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if(event.player == Minecraft.getMinecraft().thePlayer) {
                IInventory inv = BaublesApi.getBaubles(event.player);
                ItemStack[] baubles = new ItemStack[inv.getSizeInventory()];
                for(int i=0;i<baubles.length;i++) {
                    baubles[i] = inv.getStackInSlot(i);
                }
                this.syncBaubles(event.player, baubles);
            } else {
                NetworkWrapperManeuverGear.wrapper.sendToServer(new MessageRequestBaubles(event.player));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void renderBauble(RenderPlayerEvent.Specials.Post event) {
        if(event.entityPlayer.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            return;
        }
        if(!baublesToRender.containsKey(event.entityPlayer)) {
            return;
        }
        HashMap<BaubleType, ItemStack> map = baublesToRender.get((event.entityPlayer));
        for(ItemStack stack:map.values()) {
            if(stack==null || stack.getItem()==null || !(stack.getItem() instanceof IBaubleRendered)) {
                continue;
            }
            IBaubleRendered bauble = (IBaubleRendered) stack.getItem();

            GL11.glPushMatrix();

            bauble.getRenderer(stack).renderBauble(event.entityPlayer, stack, event.partialRenderTick);

            GL11.glPopMatrix();
        }
    }
}
