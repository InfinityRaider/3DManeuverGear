package com.infinityraider.maneuvergear;

import com.infinityraider.maneuvergear.config.Config;
import com.infinityraider.maneuvergear.proxy.ClientProxy;
import com.infinityraider.maneuvergear.proxy.IProxy;
import com.infinityraider.maneuvergear.proxy.ServerProxy;
import com.infinityraider.maneuvergear.registry.EntityRegistry;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.infinityraider.maneuvergear.network.*;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.maneuvergear.registry.SoundRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class ManeuverGear extends InfinityMod<IProxy, Config> {
    public static ManeuverGear instance;

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected ClientProxy createClientProxy() {
        return new ClientProxy();
    }

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    protected ServerProxy createServerProxy() {
        return new ServerProxy();
    }

    @Override
    public ItemRegistry getModItemRegistry() {
        return ItemRegistry.getInstance();
    }

    @Override
    public EntityRegistry getModEntityRegistry() {
        return EntityRegistry.getInstance();
    }
    @Override
    public SoundRegistry getModSoundRegistry() {
        return SoundRegistry.getInstance();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageBoostUsed.class);
        wrapper.registerMessage(MessageDartAnchored.class);
        wrapper.registerMessage(MessageDartRetracted.class);
        wrapper.registerMessage(MessageManeuverGearEquipped.class);
        wrapper.registerMessage(MessageSpawnSteamParticles.class);
    }
}
