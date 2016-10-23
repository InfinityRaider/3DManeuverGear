package com.InfinityRaider.maneuvergear;

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
import net.minecraftforge.fml.common.event.*;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        guiFactory = Reference.GUI_FACTORY_CLASS,
        dependencies = "required-after:"+ Names.Mods.baubles
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
        return null;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageAttackDualWielded.class);
        wrapper.registerMessage(MessageBoostUsed.class);
        wrapper.registerMessage(MessageDartAnchored.class);
        wrapper.registerMessage(MessageEquipManeuverGear.class);
        wrapper.registerMessage(MessageManeuverGearEquipped.class);
        wrapper.registerMessage(MessageMouseButtonPressed.class);
        wrapper.registerMessage(MessageNotifyBaubleEquip.class);
        wrapper.registerMessage(MessageRequestBaubles.class);
        wrapper.registerMessage(MessageSpawnSteamParticles.class);
        wrapper.registerMessage(MessageSwingArm.class);
        wrapper.registerMessage(MessageSyncBaubles.class);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onPreInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onInit(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onPostInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public  void onServerStart(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }
}
