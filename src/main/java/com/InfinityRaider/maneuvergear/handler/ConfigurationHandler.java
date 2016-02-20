package com.InfinityRaider.maneuvergear.handler;

import com.InfinityRaider.maneuvergear.reference.Reference;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class ConfigurationHandler {
    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }

    public Configuration config;

    //numbers
    public int durability;
    public float damage;
    public float bootFallDamageReduction;
    public int cableLength;

    //content
    public boolean disableMusicDisc;
    public boolean disableFallBoots;

    //client
    @SideOnly(Side.CLIENT)
    public boolean overridePlayerRenderer;
    @SideOnly(Side.CLIENT)
    public float retractingSpeed;
    @SideOnly(Side.CLIENT)
    public boolean useConfigKeyBinds;
    @SideOnly(Side.CLIENT)
    public int retractLeftKey;
    @SideOnly(Side.CLIENT)
    public int retractRightKey;

    //debug
    public boolean debug;

    public void init(FMLPreInitializationEvent event) {
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
    public void initClientConfigs(FMLPreInitializationEvent event) {
        if(config == null) {
            config = new Configuration(event.getSuggestedConfigurationFile());
        }
        loadClientConfiguration();
        if(config.hasChanged()) {
            config.save();
        }
        LogHelper.debug("Client configuration Loaded");
    }

    private void loadConfiguration() {
        //numbers
        durability = config.getInt("Blade durability", Categories.CATEGORY_NUMBERS.getName(), 64, 10, 100,
                "The number of attacks after which a sword blade breaks");
        damage = config.getFloat("Blade damage", Categories.CATEGORY_NUMBERS.getName(), 8.0F, 1.0F, 30.0F,
                "The damage dealt by sword blade attacks");
        bootFallDamageReduction = config.getFloat("Fall boots damage reduction", Categories.CATEGORY_NUMBERS.getName(), 0.85F, 0.0F, 1.0F,
                "The fraction of fall damage reduction when wearing fall boots");
        cableLength = config.getInt("Cable length", Categories.CATEGORY_NUMBERS.getName(), 64, 30, 100,
                "The maximum length of a cable, this defines how far grapples will fly before being automatically retracted if they don't hit anything." +
                "(must match on client and server");

        //content
        disableMusicDisc = config.getBoolean("Disable music disc", Categories.CATEGORY_CONTENT.getName(), false, "Set to true to disable the music disc");
        disableFallBoots = config.getBoolean("Disable fall boots", Categories.CATEGORY_CONTENT.getName(), false, "Set to true to disable the fall boots");

        //debug
        debug = config.getBoolean("debug", Categories.CATEGORY_DEBUG.getName(), false, "Set to true if you wish to enable debug mode");
    }

    @SideOnly(Side.CLIENT)
    private void loadClientConfiguration() {
        //override rendering
        overridePlayerRenderer = config.getBoolean("Left arm swing animation", Categories.CATEGORY_CLIENT.getName(), true,
                "Set to false if you experience issues with player rendering, " +
                "disabling this will have the effect of no animating the left arm of players using the maneuver gear sword." +
                "This is a client side only config and does not have to match the server");
        retractingSpeed = config.getFloat("Retracting velocity", Categories.CATEGORY_NUMBERS.getName(), 12.0F, 5.0F, 50.0F,
                "The speed at which a grapple is reeled in while holding the hot key." +
                        "(note that this is a client side config and does not need to match the server, can be tweaked to personal preference");
        useConfigKeyBinds = config.getBoolean("Use config keybinds", Categories.CATEGORY_CLIENT.getName(), true,
                "Set this to false if you want to use the vanilla minecraft keybind system. This is set to true by default to avoid keybind clutter" +
                "If this is true, the ingame keybinds will no longer work, so you can unbind them to make space for others,"+
                " if you make them overlap, make sure they are overlapping with something that doesn't result in weird behaviour.");
        retractLeftKey = config.getInt("Retract left cable hotkey", Categories.CATEGORY_CLIENT.getName(), Keyboard.KEY_Z, 0, Keyboard.KEYBOARD_SIZE,
                "This is the hotkey to toggle the retracting of the left cable." +
                "This will only work when 'Use config keybinds' is set to true." +
                "You can find a list of all keys and their numbering here: http://www.penticoff.com/nb/kbds/ibm104kb.htm");
        retractRightKey = config.getInt("Retract right cable hotkey", Categories.CATEGORY_CLIENT.getName(), Keyboard.KEY_X, 0, Keyboard.KEYBOARD_SIZE,
                "This is the hotkey to toggle the retracting of the right cable." +
                "This will only work when 'Use config keybinds' is set to true." +
                "You can find a list of all keys and their numbering here: http://www.penticoff.com/nb/kbds/ibm104kb.htm");
    }

    public enum Categories {
        CATEGORY_NUMBERS("Numbers"),
        CATEGORY_CONTENT("Content"),
        CATEGORY_CLIENT("Client"),
        CATEGORY_DEBUG("Debug");

        private final String name;

        Categories(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return Reference.MOD_ID + " " + name + " Settings";
        }

        public String getLangKey() {
            return Reference.MOD_ID.toLowerCase() +".configGui.ctgy." + name;
        }
    }


}
