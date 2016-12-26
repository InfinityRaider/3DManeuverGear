package com.InfinityRaider.maneuvergear;

import com.InfinityRaider.maneuvergear.init.EntityRegistry;
import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import com.InfinityRaider.maneuvergear.network.*;
import com.InfinityRaider.maneuvergear.proxy.IProxy;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        guiFactory = Reference.GUI_FACTORY_CLASS,
        dependencies = "required-after:infinitylib;after:"+ Names.Mods.baubles
)
public class ManeuverGear extends InfinityMod {
    @Mod.Instance(Reference.MOD_ID)
    public static ManeuverGear instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Override
    public IProxyBase proxy() {
        return proxy;
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public Object getModBlockRegistry() {
        return this;
    }

    @Override
    public Object getModItemRegistry() {
        return ItemRegistry.getInstance();
    }

    @Override
    public Object getModEntityRegistry() {
        return EntityRegistry.getInstance();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageBoostUsed.class);
        wrapper.registerMessage(MessageDartAnchored.class);
        wrapper.registerMessage(MessageEquipManeuverGear.class);
        wrapper.registerMessage(MessageManeuverGearEquipped.class);
        wrapper.registerMessage(MessageSpawnSteamParticles.class);
    }
}
