package com.InfinityRaider.maneuvergear;

import com.InfinityRaider.maneuvergear.handler.DartHandler;
import com.InfinityRaider.maneuvergear.init.Entities;
import com.InfinityRaider.maneuvergear.init.ItemRegistry;
import com.InfinityRaider.maneuvergear.network.NetworkWrapperManeuverGear;
import com.InfinityRaider.maneuvergear.proxy.IProxy;
import com.InfinityRaider.maneuvergear.reference.Names;
import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:"+ Names.Mods.baubles)
public class ManeuverGear {
    @Mod.Instance(Reference.MOD_ID)
    public static ManeuverGear instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        proxy.registerEventHandlers();
        NetworkWrapperManeuverGear.init();
        proxy.initConfiguration(event);
        ItemRegistry.getInstance().init();
        Entities.init();
        proxy.registerRenderers();
        LogHelper.debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        ItemRegistry.getInstance().initRecipes();
        proxy.registerKeyBindings();
        LogHelper.debug("Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        proxy.replacePlayerModel();
        LogHelper.debug("Post-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void onServerStart(FMLServerStartingEvent event) {
        DartHandler.instance.reset();
    }
}
