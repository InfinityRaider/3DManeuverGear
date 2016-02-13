package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigurationHandler {
    public static final String CATEGORY_NUMBERS = "Numbers";
    public static final String CATEGORY_CONTENT = "Content";
    public static final String CATEGORY_CLIENT = "Client";
    public static final String CATEGORY_DEBUG = "Debug";

    public static Configuration config;

    //numbers
    public static int durability;
    public static float damage;
    public static float bootFallDamageReduction;
    public static int cableLength;

    //content
    public static boolean disableMusicDisc;
    public static boolean disableFallBoots;

    //client
    @SideOnly(Side.CLIENT)
    public static boolean overridePlayerRenderer;
    @SideOnly(Side.CLIENT)
    public static float retractingSpeed;

    //debug
    public static boolean debug;

    public static void init(FMLPreInitializationEvent event) {
        if(config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
        }
        loadConfiguration();
        if(config.hasChanged()) {
            config.save();
        }
        LogHelper.debug("Configuration Loaded");
    }

    @SideOnly(Side.CLIENT)
    public static void initClientConfigs(FMLPreInitializationEvent event) {
        if(config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
        }
        loadClientConfiguration();
        if(config.hasChanged()) {
            config.save();
        }
        LogHelper.debug("Client configuration Loaded");
    }

    private static void loadConfiguration() {
        //numbers
        durability = config.getInt("Blade durability", CATEGORY_NUMBERS, 64, 10, 100,
                "The number of attacks after which a sword blade breaks");
        damage = config.getFloat("Blade damage", CATEGORY_NUMBERS, 8.0F, 1.0F, 30.0F,
                "The damage dealt by sword blade attacks");
        bootFallDamageReduction = config.getFloat("Fall boots damage reduction", CATEGORY_NUMBERS, 0.85F, 0.0F, 1.0F,
                "The fraction of fall damage reduction when wearing fall boots");
        cableLength = config.getInt("Cable length", CATEGORY_NUMBERS, 64, 30, 100,
                "The maximum length of a cable, this defines how far grapples will fly before being automatically retracted if they don't hit anything." +
                "(must match on client and server");

        //content
        disableMusicDisc = config.getBoolean("Disable music disc", CATEGORY_CONTENT, false, "Set to true to disable the music disc");
        disableFallBoots = config.getBoolean("Disable fall boots", CATEGORY_CONTENT, false, "Set to true to disable the fall boots");

        //debug
        debug = config.getBoolean("debug", CATEGORY_DEBUG, false, "Set to true if you wish to enable debug mode");
    }

    @SideOnly(Side.CLIENT)
    private static void loadClientConfiguration() {
        //override rendering
        overridePlayerRenderer = config.getBoolean("Left arm swing animation", CATEGORY_CLIENT, true,
                "Set to false if you experience issues with player rendering, " +
                "disabling this will have the effect of no animating the left arm of players using the maneuver gear sword." +
                "This is a client side only config and does not have to match the server");
        retractingSpeed = config.getFloat("Retracting velocity", CATEGORY_NUMBERS, 12.0F, 5.0F, 50.0F,
                "The speed at which a grapple is reeled in while holding the hotkey." +
                "(note that this is a client side config and does not need to match the server, can be tweaked to personnal preference");
    }
}
