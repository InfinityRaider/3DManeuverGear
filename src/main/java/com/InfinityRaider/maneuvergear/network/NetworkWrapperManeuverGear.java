package com.InfinityRaider.maneuvergear.network;

import com.InfinityRaider.maneuvergear.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkWrapperManeuverGear {
    public static final int MSG_ID_REQUEST_BAUBLES = 0;
    public static final int MSG_ID_SYNC_BAUBLES = 1;
    public static final int MSG_ID_EQUIP_BAUBLE = 2;
    public static final int MSG_ID_MOUSE_PRESSED = 3;
    public static final int MSG_ID_SWING_LEFT = 4;
    public static final int MSG_ID_ATTACK_DUAL = 5;
    public static final int MSG_ID_BOOST_USED = 6;
    public static final int MSG_ID_SPAWN_STEAM = 7;

    public static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
        initMessages();
    }

    private static void initMessages() {
        wrapper.registerMessage(MessageRequestBaubles.MessageHandler.class, MessageRequestBaubles.class, MSG_ID_REQUEST_BAUBLES, Side.SERVER);
        wrapper.registerMessage(MessageSyncBaubles.MessageHandler.class, MessageSyncBaubles.class, MSG_ID_SYNC_BAUBLES, Side.CLIENT);
        wrapper.registerMessage(MessageNotifyBaubleEquip.MessageHandler.class, MessageNotifyBaubleEquip.class, MSG_ID_EQUIP_BAUBLE, Side.CLIENT);
        wrapper.registerMessage(MessageMouseButtonPressed.MessageHandler.class, MessageMouseButtonPressed.class, MSG_ID_MOUSE_PRESSED, Side.SERVER);
        wrapper.registerMessage(MessageSwingLeftWeapon.MessageHandler.class, MessageSwingLeftWeapon.class, MSG_ID_SWING_LEFT, Side.CLIENT);
        wrapper.registerMessage(MessageAttackDualWielded.MessageHandler.class, MessageAttackDualWielded.class, MSG_ID_ATTACK_DUAL, Side.SERVER);
        wrapper.registerMessage(MessageBoostUsed.MessageHandler.class, MessageBoostUsed.class, MSG_ID_BOOST_USED, Side.SERVER);
        wrapper.registerMessage(MessageSpawnSteamParticles.MessageHandler.class, MessageSpawnSteamParticles.class, MSG_ID_SPAWN_STEAM, Side.CLIENT);
    }
}
