package com.infinityraider.maneuvergear.network;

import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageManeuverGearEquipped extends MessageBase {
    private boolean equipped;

    @SuppressWarnings("unused")
    public MessageManeuverGearEquipped() {}

    public MessageManeuverGearEquipped(boolean equipped) {
        this.equipped = equipped;
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
        if(this.equipped) {
            DartHandler.instance.equipGear(player);
        } else {
            DartHandler.instance.unEquipGear(player);
        }
    }
}
