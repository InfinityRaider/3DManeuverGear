package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDartRetracted extends MessageBase {
    private boolean left;

    @SuppressWarnings("unused")
    public MessageDartRetracted() {}

    public MessageDartRetracted(boolean left) {
        this.left = left;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        PlayerEntity player = ManeuverGear.instance.getClientPlayer();
        if(player == null) {
            return;
        }
        DartHandler.instance.retractDart(player, this.left);
    }
}
