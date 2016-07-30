package com.InfinityRaider.maneuvergear.render;

import baubles.api.BaubleType;
import com.InfinityRaider.maneuvergear.item.IBaubleRendered;
import com.InfinityRaider.maneuvergear.network.MessageRequestBaubles;
import com.InfinityRaider.maneuvergear.utility.BaublesWrapper;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import com.infinityraider.infinitylib.render.RenderUtilBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class RenderBauble extends RenderUtilBase {
    private static final RenderBauble INSTANCE = new RenderBauble();

    private HashMap<EntityPlayer, HashMap<BaubleType, ItemStack>> baublesToRender;

    private RenderBauble() {
        baublesToRender = new HashMap<>();
    }

    public static RenderBauble getInstance() {
        return INSTANCE;
    }

    public void syncBaubles(EntityPlayer player, ItemStack[] baubles) {
       HashMap<BaubleType, ItemStack> baubleMap = new HashMap<>();
        for(ItemStack stack:baubles) {
            if((stack!=null) && (stack.getItem() instanceof IBaubleRendered)) {
                baubleMap.put(((IBaubleRendered) stack.getItem()).getBaubleType(stack), stack);
            }
        }
        baublesToRender.put(player, baubleMap);
    }

    public void equipBauble(EntityPlayer player, ItemStack bauble) {
        if(bauble==null || !(bauble.getItem() instanceof IBaubleRendered)) {
            return;
        }
        if(!baublesToRender.containsKey(player)) {
            baublesToRender.put(player, new HashMap<>());
        }
        HashMap<BaubleType, ItemStack> baubles = baublesToRender.get(player);
        baubles.put(((IBaubleRendered) bauble.getItem()).getBaubleType(bauble), bauble);
    }

    public void unequipBauble(EntityPlayer player, ItemStack bauble) {
        if(bauble==null || !(bauble.getItem() instanceof IBaubleRendered)) {
            return;
        }
        if(!baublesToRender.containsKey(player)) {
            baublesToRender.put(player, new HashMap<>());
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
                IInventory inv = BaublesWrapper.getInstance().getBaubles(event.player);
                ItemStack[] baubles = new ItemStack[inv.getSizeInventory()];
                for(int i=0;i<baubles.length;i++) {
                    baubles[i] = inv.getStackInSlot(i);
                }
                this.syncBaubles(event.player, baubles);
            } else {
                NetworkWrapper.getInstance().sendToServer(new MessageRequestBaubles(event.player));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void renderBauble(RenderPlayerEvent.Post event) {
        if(event.getEntityPlayer().isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            return;
        }
        if(!baublesToRender.containsKey(event.getEntityPlayer())) {
            return;
        }
        HashMap<BaubleType, ItemStack> map = baublesToRender.get((event.getEntityPlayer()));
        for(ItemStack stack:map.values()) {
            if(stack==null || !(stack.getItem() instanceof IBaubleRendered)) {
                continue;
            }
            IBaubleRendered bauble = (IBaubleRendered) stack.getItem();

            applyEntityTransforms(event.getEntityPlayer(), event.getPartialRenderTick(), false);

            GL11.glPushMatrix();
            bauble.getRenderer(stack).renderBauble(event.getEntityPlayer(), stack, event.getPartialRenderTick());

            GL11.glPopMatrix();
            applyEntityTransforms(event.getEntityPlayer(), event.getPartialRenderTick(), true);
        }
    }

    private void applyEntityTransforms(EntityPlayer player, float partialRenderTick, boolean inverse) {
        EntityPlayer local = Minecraft.getMinecraft().thePlayer;
        double X1 = local.prevPosX + (local.posX - local.prevPosX)*partialRenderTick;
        double X2 = player.prevPosX + (player.posX - player.prevPosX)*partialRenderTick;
        double Y1 = local.prevPosY + (local.posY - local.prevPosY)*partialRenderTick;
        double Y2 = player.prevPosY + (player.posY - player.prevPosY)*partialRenderTick;
        double Z1 = local.prevPosZ + (local.posZ - local.prevPosZ)*partialRenderTick;
        double Z2 = player.prevPosZ + (player.posZ - player.prevPosZ)*partialRenderTick;
        double X = X2 - X1;
        double Y = Y2 - Y1;
        double Z = Z2 - Z1;
        if(inverse) {
            GL11.glTranslated(-X, -Y, -Z);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glTranslated(X, Y, Z);
        }
    }
}
