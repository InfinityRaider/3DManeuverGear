package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageManeuverGearEquipped extends MessageBase {
    public MessageManeuverGearEquipped() {}

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        DartHandler.instance.equipGear(ManeuverGear.instance.getClientPlayer());

    }
}
