package com.infinityraider.maneuvergear.network;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageManeuverGearEquipped extends MessageBase<IMessage> {
    public MessageManeuverGearEquipped() {}

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        DartHandler.instance.equipGear(ManeuverGear.proxy.getClientPlayer());
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
