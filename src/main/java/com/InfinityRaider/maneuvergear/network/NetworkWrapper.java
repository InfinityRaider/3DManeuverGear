package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.ManeuverGear;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapper {
    private static NetworkWrapper INSTANCE = new NetworkWrapper();

    public static NetworkWrapper getInstance() {
        return INSTANCE;
    }

    private final SimpleNetworkWrapper wrapper;
    private int nextId = 0;

    private NetworkWrapper() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
    }

    public void init() {
        registerMessage(MessageRequestBaubles.class);
        registerMessage(MessageSyncBaubles.class);
        registerMessage(MessageNotifyBaubleEquip.class);
        registerMessage(MessageMouseButtonPressed.class);
        registerMessage(MessageAttackDualWielded.class);
        registerMessage(MessageBoostUsed.class);
        registerMessage(MessageSpawnSteamParticles.class);
        registerMessage(MessageDartAnchored.class);
        registerMessage(MessageEquipManeuverGear.class);
        registerMessage(MessageManeuverGearEquipped.class);
        registerMessage(MessageSwingArm.class);
    }

    public void sendToAll(MessageBase message) {
        wrapper.sendToAll(message);
    }

    public void sendTo(MessageBase message, EntityPlayerMP player) {
        wrapper.sendTo(message, player);
    }

    public void sendToAllAround(MessageBase message, World world, double x, double y, double z, double range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, range));
    }

    public void sendToAllAround(MessageBase message, int dimension, double x, double y, double z, double range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public void sendToAllAround(MessageBase message, NetworkRegistry.TargetPoint point) {
        wrapper.sendToAllAround(message, point);
    }

    public void sendToDimension(MessageBase messageBase, World world) {
        sendToDimension(messageBase, world.provider.getDimension());
    }

    public void sendToDimension(MessageBase message, int dimensionId) {
        wrapper.sendToDimension(message, dimensionId);
    }

    public void sendToServer(MessageBase message) {
        wrapper.sendToServer(message);
    }

    private <REQ extends MessageBase<REPLY>, REPLY extends IMessage> void registerMessage(Class<? extends REQ> message) {
        try {
            Side side = message.getDeclaredConstructor().newInstance().getMessageHandlerSide();
            wrapper.registerMessage(new MessageHandler<REQ, REPLY>(), message, nextId, side);
            nextId = nextId + 1;
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    private static final class MessageHandler<REQ extends MessageBase<REPLY>, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {
        private MessageHandler() {}

        @Override
        public final REPLY onMessage(REQ message, MessageContext ctx) {
            ManeuverGear.proxy.queueTask(new MessageTask(message, ctx));
            return message.getReply(ctx);
        }
    }

    private static class MessageTask implements Runnable {
        private final MessageBase message;
        private final MessageContext ctx;

        private MessageTask(MessageBase message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            this.message.processMessage(this.ctx);
        }
    }
}
